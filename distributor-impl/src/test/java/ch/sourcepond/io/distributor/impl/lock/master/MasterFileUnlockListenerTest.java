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
package ch.sourcepond.io.distributor.impl.lock.master;

import ch.sourcepond.io.distributor.impl.MasterListener;
import ch.sourcepond.io.distributor.impl.MasterListenerTest;
import ch.sourcepond.io.distributor.impl.StatusResponseMessage;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

public class MasterFileUnlockListenerTest extends MasterListenerTest<FileUnlockException> {
    private static final String EXPECTED_FAILURE_MESSAGE = "someMessage";

    @Override
    protected MasterListener createListener() {
        return new MasterFileUnlockListener(EXPECTED_PATH, EXPECTED_TIMOUT, EXPECTED_UNIT, members);
    }

    @Override
    protected Class<FileUnlockException> getValidationExceptionType() {
        return FileUnlockException.class;
    }

    @Test(timeout = 2000)
    public void validateAnswers() throws Exception {
        final IOException expected = new IOException(EXPECTED_FAILURE_MESSAGE);
        payload = new StatusResponseMessage(EXPECTED_PATH, expected);
        when(message.getMessageObject()).thenReturn(payload);
        listener.onMessage(message);
        try {
            listener.awaitNodeAnswers();
            fail("Exception expected");
        } catch (final FileUnlockException e) {
            assertTrue(expected.getMessage().contains(EXPECTED_FAILURE_MESSAGE));
        }
    }
}
