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
package ch.sourcepond.io.distributor.impl.session;

import ch.sourcepond.io.distributor.api.DeleteSession;
import ch.sourcepond.io.distributor.api.GlobalLockException;
import ch.sourcepond.io.distributor.impl.lock.LockManager;
import ch.sourcepond.io.distributor.spi.Receiver;
import com.hazelcast.core.ITopic;

import java.util.concurrent.TimeUnit;

public class SessionFactory {
    private final Receiver receiver;
    private final LockManager glm;
    private final ITopic<String> sendDeleteTopic;

    SessionFactory(final Receiver pReceiver, final LockManager pGlm, final ITopic<String> pSendDeleteTopic) {
        receiver = pReceiver;
        glm = pGlm;
        sendDeleteTopic = pSendDeleteTopic;
    }

    public DeleteSession lockDelete(final String pPath, final TimeUnit pTimeoutUnit, final long pTimeout) throws GlobalLockException {
        glm.lockGlobally(pPath, pTimeoutUnit, pTimeout);

        return null;
    }
}
