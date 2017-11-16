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
package ch.sourcepond.io.distributor.impl;

import ch.sourcepond.io.distributor.spi.Receiver;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Member;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import org.junit.Before;

import static ch.sourcepond.io.distributor.impl.Constants.EXPECTED_NODE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class ClientListenerTest<L extends MessageListener<M>, M> {
    protected final Receiver receiver = mock(Receiver.class);
    protected final ITopic<StatusResponseMessage> sendResponseTopic = mock(ITopic.class);
    protected final Member member = mock(Member.class);
    protected Message<M> message = mock(Message.class);
    protected M payload;
    protected L listener;

    @Before
    public void setup() {
        payload = createPayload();
        listener = createListener();
        when(member.getUuid()).thenReturn(EXPECTED_NODE);
        when(message.getPublishingMember()).thenReturn(member);
        when(message.getMessageObject()).thenReturn(payload);
    }

    protected abstract M createPayload();

    protected abstract L createListener();
}