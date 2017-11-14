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
package ch.sourcepond.io.distributor.impl.lock.client;

import ch.sourcepond.io.distributor.spi.Receiver;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Member;
import com.hazelcast.core.Message;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.io.IOException;

import static ch.sourcepond.io.distributor.impl.lock.client.Constants.EXPECTED_EXCEPTION;
import static ch.sourcepond.io.distributor.impl.lock.client.Constants.EXPECTED_NODE;
import static ch.sourcepond.io.distributor.impl.lock.client.Constants.EXPECTED_PATH;
import static ch.sourcepond.io.distributor.impl.lock.client.Constants.FAILURE_RESPONSE_ARGUMENT_MATCHER;
import static ch.sourcepond.io.distributor.impl.lock.client.Constants.GLOBAL_PATH_ARGUMENT_MATCHER;
import static ch.sourcepond.io.distributor.impl.lock.client.Constants.SUCCESS_RESPONSE_ARGUMENT_MATCHER;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ClientUnlockListenerTest {
    private final Receiver receiver = mock(Receiver.class);
    private final Member member = mock(Member.class);
    private final Message<String> message = mock(Message.class);
    private final ITopic sendFileUnlockResponseTopic = mock(ITopic.class);
    private final ClientUnlockListener listener = new ClientUnlockListener(receiver, sendFileUnlockResponseTopic);

    @Before
    public void setup() {
        when(member.getUuid()).thenReturn(EXPECTED_NODE);
        when(message.getPublishingMember()).thenReturn(member);
        when(message.getMessageObject()).thenReturn(EXPECTED_PATH);
    }

    @Test
    public void onMessageFailure() throws IOException {
        doThrow(EXPECTED_EXCEPTION).when(receiver).unlockLocally(argThat(GLOBAL_PATH_ARGUMENT_MATCHER));
        listener.onMessage(message);
        verify(sendFileUnlockResponseTopic).publish(argThat(FAILURE_RESPONSE_ARGUMENT_MATCHER));
    }

    @Test
    public void onMessageSuccess() throws IOException {
        listener.onMessage(message);
        final InOrder order = inOrder(receiver, sendFileUnlockResponseTopic);
        order.verify(receiver).unlockLocally(argThat(GLOBAL_PATH_ARGUMENT_MATCHER));
        order.verify(sendFileUnlockResponseTopic).publish(argThat(SUCCESS_RESPONSE_ARGUMENT_MATCHER));
    }
}