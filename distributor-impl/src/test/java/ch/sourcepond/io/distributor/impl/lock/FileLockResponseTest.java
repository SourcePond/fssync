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
package ch.sourcepond.io.distributor.impl.lock;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class FileLockResponseTest {
    private static final String EXPECTED_PATH = "somePath";

    @Test
    public void success() {
        final FileLockResponse response = new FileLockResponse(EXPECTED_PATH);
        assertEquals(EXPECTED_PATH, response.getPath());
        assertNull(response.getFailureOrNull());
    }

    @Test
    public void failure() {
        final IOException expected = new IOException();
        final FileLockResponse response = new FileLockResponse(EXPECTED_PATH, expected);
        assertEquals(EXPECTED_PATH, response.getPath());
        assertSame(expected, response.getFailureOrNull());
    }
}