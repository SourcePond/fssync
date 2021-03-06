/*Copyright (C) 2017 Roland Hauser, <sourcepond@gmail.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/
package ch.sourcepond.io.fssync.distributor.hazelcast.lock;

import ch.sourcepond.io.fssync.common.api.SyncPath;
import ch.sourcepond.io.fssync.distributor.hazelcast.annotations.Lock;
import ch.sourcepond.io.fssync.distributor.hazelcast.annotations.Unlock;
import ch.sourcepond.io.fssync.distributor.hazelcast.common.DistributionMessage;
import ch.sourcepond.io.fssync.distributor.hazelcast.config.DistributorConfig;
import ch.sourcepond.io.fssync.distributor.hazelcast.exception.LockException;
import ch.sourcepond.io.fssync.distributor.hazelcast.exception.UnlockException;
import ch.sourcepond.io.fssync.distributor.hazelcast.response.ClusterResponseBarrierFactory;
import ch.sourcepond.io.fssync.distributor.hazelcast.response.ResponseException;
import com.hazelcast.core.ITopic;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.concurrent.TimeoutException;

import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static org.slf4j.LoggerFactory.getLogger;

public class LockManager implements AutoCloseable {
    private static final Logger LOG = getLogger(LockManager.class);
    private final ClusterResponseBarrierFactory factory;
    private final Locks locks;
    private final ITopic<DistributionMessage> lockRequestTopic;
    private final ITopic<DistributionMessage> unlockRequestTopic;
    private final DistributorConfig config;

    @Inject
    public LockManager(final ClusterResponseBarrierFactory pFactory,
                       final Locks pLocks,
                       final DistributorConfig pConfig,
                       @Lock final ITopic<DistributionMessage> pLockRequestTopic,
                       @Unlock final ITopic<DistributionMessage> pUnlockRequestTopic) {
        factory = pFactory;
        locks = pLocks;
        lockRequestTopic = pLockRequestTopic;
        unlockRequestTopic = pUnlockRequestTopic;
        config = pConfig;
    }

    /**
     * Acquires on all known cluster-nodes a {@link java.nio.channels.FileLock} for the path specified. This method
     * blocks until all nodes have responded to the request. If the path does not exist on a node, it will be created
     * and locked.
     *
     * @param pPath Path to be locked on all nodes, must not be {@code null}.
     * @throws ResponseException Thrown, if the lock acquisition failed on some node.
     * @throws LockException     Thrown, if the lock acquisition timed out for a node.
     */
    private void acquireGlobalFileLock(final SyncPath pPath) throws ResponseException, TimeoutException {
        // In this case, the path is also the request-message
        factory.create(pPath, lockRequestTopic).awaitResponse(new DistributionMessage(pPath));
    }

    /**
     * Releases on all known cluster-nodes the file-locks for the path specified (see {@link java.nio.channels.FileLock#release()}). If
     * no locks exist, nothing happens.
     *
     * @param pPath Path to be released on all nodes, must not be {@code null}
     */
    private void releaseGlobalFileLock(final SyncPath pPath) throws ResponseException, TimeoutException {
        // In this case, the path is also the request-message
        factory.create(pPath, unlockRequestTopic).awaitResponse(new DistributionMessage(pPath));
    }

    private boolean lockAcquisitionFailed(final SyncPath pPath,
                                          final String pMessage,
                                          final Exception pCause)
            throws LockException {
        try {
            throw new LockException(pMessage, pCause);
        } finally {
            try {
                releaseGlobalFileLock(pPath);
            } catch (final ResponseException | TimeoutException e) {
                LOG.warn(e.getMessage(), e);
            } finally {
                locks.unlock(pPath.toAbsolutePath());
            }
        }
    }

    public boolean tryLock(final SyncPath pPath) throws LockException {
        try {
            if (locks.tryLock(pPath.toAbsolutePath())) {
                acquireGlobalFileLock(pPath);
                return true;
            } else {
                return lockAcquisitionFailed(pPath, format("Lock acquisition timed out after %d %s",
                        config.lockTimeout(), config.lockTimeoutUnit()), null);
            }
        } catch (final InterruptedException e) {
            currentThread().interrupt();
            return lockAcquisitionFailed(pPath, format("Lock acquisition interrupted for %s!", pPath), e);
        } catch (final ResponseException | TimeoutException e) {
            return lockAcquisitionFailed(pPath, format("Lock acquisition failed for %s!", pPath), e);
        }
    }

    public void unlock(final SyncPath pPath) throws UnlockException {
        try {
            releaseGlobalFileLock(pPath);
        } catch (final ResponseException | TimeoutException e) {
            throw new UnlockException(format("Exception occurred while releasing file-lock for %s", pPath), e);
        } finally {
            locks.unlock(pPath.toAbsolutePath());
        }
    }

    @Override
    public void close() {
        locks.close();
    }
}
