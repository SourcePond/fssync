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
package ch.sourcepond.io.distributor.impl.dataflow;

import ch.sourcepond.io.distributor.api.GlobalPath;
import ch.sourcepond.io.distributor.impl.common.client.ClientListener;
import ch.sourcepond.io.distributor.impl.common.StatusMessage;
import ch.sourcepond.io.distributor.spi.Receiver;
import com.hazelcast.core.ITopic;
import org.slf4j.Logger;

import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

final class DeleteRequestListener extends ClientListener<String> {
    private static final Logger LOG = getLogger(DeleteRequestListener.class);

    public DeleteRequestListener(final Receiver pReceiver, final ITopic<StatusMessage> pSendResponseTopic) {
        super(pReceiver, pSendResponseTopic);
    }

    @Override
    protected Logger getLog() {
        return LOG;
    }

    @Override
    protected void processMessage(final GlobalPath pPath, final String pPayload) throws IOException {
        receiver.delete(pPath);
    }

    @Override
    protected String toPath(final String pPayload) {
        return pPayload;
    }
}
