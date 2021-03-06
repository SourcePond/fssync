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

import ch.sourcepond.io.fssync.distributor.hazelcast.annotations.Lock;
import ch.sourcepond.io.fssync.distributor.hazelcast.annotations.Unlock;
import ch.sourcepond.io.fssync.distributor.hazelcast.common.ClientMessageListenerFactory;
import ch.sourcepond.io.fssync.distributor.hazelcast.common.DistributionMessage;
import ch.sourcepond.io.fssync.distributor.hazelcast.common.MessageListenerRegistration;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.MessageListener;

import javax.inject.Singleton;

public class LockModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ClientLockProcessor.class);
        bind(ClientUnlockProcessor.class);
        bind(Locks.class);
        bind(LockManager.class);
    }

    @Provides
    @Singleton
    @Lock
    MessageListener<DistributionMessage> lockListener(final ClientMessageListenerFactory pFactory, final ClientLockProcessor pProcessor) {
        return pFactory.createListener(pProcessor);
    }

    @Provides
    @Singleton
    @Unlock
    MessageListener<DistributionMessage> unlockListener(final ClientMessageListenerFactory pFactory, final ClientUnlockProcessor pProcessor) {
        return pFactory.createListener(pProcessor);
    }

    @Provides
    @Singleton
    @Lock
    MessageListenerRegistration registerLockListener(final @Lock ITopic<DistributionMessage> pLockTopic, final @Lock MessageListener<DistributionMessage> pLockListener) {
        return MessageListenerRegistration.register(pLockTopic, pLockListener);
    }

    @Provides
    @Singleton
    @Unlock
    MessageListenerRegistration registerUnlockListener(final @Unlock ITopic<DistributionMessage> pUnlockTopic, final @Unlock MessageListener<DistributionMessage> pUnlockListener) {
        return MessageListenerRegistration.register(pUnlockTopic, pUnlockListener);
    }
}
