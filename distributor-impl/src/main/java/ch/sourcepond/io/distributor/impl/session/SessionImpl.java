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

import ch.sourcepond.io.distributor.impl.MasterListener;
import ch.sourcepond.io.distributor.spi.Receiver;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;

abstract class SessionImpl extends MasterListener implements Closeable {
    protected final Receiver receiver;

    public SessionImpl(final String pPath,
                       final long pTimeout,
                       final TimeUnit pUnit,
                       final Receiver pReceiver) {
        super(pPath, pTimeout, pUnit);
        receiver = pReceiver;
    }
}
