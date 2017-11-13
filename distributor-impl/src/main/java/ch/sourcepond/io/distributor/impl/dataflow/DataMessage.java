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

import ch.sourcepond.io.distributor.impl.DistributionMessage;

class DataMessage extends DistributionMessage {
    private final byte[] data;

    public DataMessage(final String pPath, final byte[] pData) {
        super(pPath);
        data = pData;
    }

    public byte[] getData() {
        return data;
    }
}
