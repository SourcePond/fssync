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
package ch.sourcepond.io.fssync.source.fs.trigger;

import ch.sourcepond.io.fssync.common.api.SyncPath;
import ch.sourcepond.io.fssync.distributor.api.Distributor;
import ch.sourcepond.io.fssync.source.fs.Config;
import ch.sourcepond.io.fssync.source.fs.fswatch.RegularFile;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;

import static org.slf4j.LoggerFactory.getLogger;

class SyncTrigger implements Runnable {
    private static final Logger LOG = getLogger(SyncTrigger.class);
    private final ScheduledExecutorService executor;
    private final Distributor distributor;
    private final Config config;
    private final SyncTriggerFunction trigger;
    private final RegularFile path;
    private volatile int retries;

    public SyncTrigger(final ScheduledExecutorService pExecutor,
                       final Distributor pDistributor,
                       final Config pConfig,
                       final RegularFile pPath,
                       final SyncTriggerFunction pTrigger) {
        executor = pExecutor;
        distributor = pDistributor;
        config = pConfig;
        path = pPath;
        trigger = pTrigger;
    }

    @Override
    public void run() {
        try {
            final SyncPath syncPath = path.getSyncPath();
            if (distributor.tryLock(syncPath)) {
                try {
                    trigger.process(path);
                } finally {
                    distributor.unlock(syncPath);
                }
            } else if (config.retryAttempts() > retries++) {
                executor.schedule(this, config.retryDelay(), config.retryDelayUnit());
            } else {
                LOG.warn("Gave up syncing after {} trials", config.retryAttempts());
            }
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
