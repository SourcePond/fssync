package ch.sourcepond.io.distributor.impl.lock;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.ITopic;
import org.slf4j.Logger;

import java.util.concurrent.TimeoutException;

import static java.lang.String.format;
import static org.slf4j.LoggerFactory.getLogger;

class MasterFileLockManager {

    @FunctionalInterface
    private interface ClusterAction<T> {

        void perform(MasterResponseListener<T> pListener, ITopic<String> pSenderTopic, ITopic<T> pReceiverTopic);
    }

    private static final Logger LOG = getLogger(ClientFileLockManager.class);
    private final Cluster cluster;
    private final ITopic<String> sendFileLockRequestTopic;
    private final ITopic<LockMessage> receiveFileLockResponseTopic;
    private final ITopic<String> sendFileUnlockRequstTopic;
    private final ITopic<String> receiveFileUnlockResponseTopic;

    public MasterFileLockManager(final Cluster pCluster,
                                 final ITopic<String> pSendFileLockRequestTopic,
                                 final ITopic<LockMessage> pReceiveFileLockResponseTopic,
                                 final ITopic<String> pSendFileUnlockRequstTopic,
                                 final ITopic<String> pReceiveFileUnlockResponseTopic) {
        cluster = pCluster;
        sendFileLockRequestTopic = pSendFileLockRequestTopic;
        receiveFileLockResponseTopic = pReceiveFileLockResponseTopic;
        sendFileUnlockRequstTopic = pSendFileUnlockRequstTopic;
        receiveFileUnlockResponseTopic = pReceiveFileUnlockResponseTopic;
    }

    private <E> void performAction(final ITopic<String> pSenderTopic,
                                   final ITopic<E> pReceiverTopic,
                                   final String pPath,
                                   final MasterResponseListener<E> pListener)
            throws TimeoutException, FileLockException {
        final String membershipId = cluster.addMembershipListener(pListener);
        try {
            final String registrationId = pReceiverTopic.addMessageListener(pListener);
            try {
                pSenderTopic.publish(pPath);
                pListener.awaitNodeAnswers();
            } finally {
                pReceiverTopic.removeMessageListener(registrationId);
            }
        } finally {
            cluster.removeMembershipListener(membershipId);
        }
    }

    public void acquireGlobalFileLock(final String pPath) throws TimeoutException, FileLockException {
        performAction(sendFileLockRequestTopic, receiveFileLockResponseTopic, pPath,
                new MasterFileLockResponseListener(pPath, cluster.getMembers()));
    }

    public void releaseGlobalFileLock(final String pPath) {
        try {
            performAction(sendFileUnlockRequstTopic, receiveFileUnlockResponseTopic, pPath,
                    new MasterFileUnlockResponseListener(pPath, cluster.getMembers()));
        } catch (final TimeoutException | FileLockException e) {
            LOG.warn(format("Exception occurred while releasing file-lock for %s", pPath), e);
        }
    }
}
