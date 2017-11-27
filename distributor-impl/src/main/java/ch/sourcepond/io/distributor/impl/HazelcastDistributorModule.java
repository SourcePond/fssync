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

import ch.sourcepond.io.distributor.impl.annotations.Delete;
import ch.sourcepond.io.distributor.impl.annotations.Lock;
import ch.sourcepond.io.distributor.impl.annotations.Store;
import ch.sourcepond.io.distributor.impl.annotations.Transfer;
import ch.sourcepond.io.distributor.impl.annotations.Unlock;
import ch.sourcepond.io.distributor.impl.binding.BindingModule;
import ch.sourcepond.io.distributor.impl.common.CommonModule;
import ch.sourcepond.io.distributor.impl.common.MessageListenerRegistration;
import ch.sourcepond.io.distributor.impl.lock.LockModule;
import ch.sourcepond.io.distributor.impl.request.RequestModule;
import ch.sourcepond.io.distributor.impl.response.ResponseModule;
import ch.sourcepond.io.distributor.spi.Receiver;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.multibindings.Multibinder;

import java.util.Map;

import static com.google.inject.Key.get;
import static com.google.inject.multibindings.Multibinder.newSetBinder;

public class HazelcastDistributorModule extends AbstractModule {
    private final Receiver receiver;
    private final Map<String, String> instantiationProperties;

    public HazelcastDistributorModule(final Receiver pReceiver, final Map<String, String> pInstantiationProperties) {
        receiver = pReceiver;
        instantiationProperties = pInstantiationProperties;
    }

    @Override
    protected void configure() {
        bind(Receiver.class).toInstance(receiver);
        install(new BindingModule(instantiationProperties));
        install(new CommonModule());
        install(new LockModule());
        install(new RequestModule());
        install(new ResponseModule());

        Multibinder<MessageListenerRegistration> registrations = newSetBinder(binder(), MessageListenerRegistration.class);
        registrations.addBinding().to(get(MessageListenerRegistration.class, Lock.class));
        registrations.addBinding().to(get(MessageListenerRegistration.class, Unlock.class));
        registrations.addBinding().to(get(MessageListenerRegistration.class, Delete.class));
        registrations.addBinding().to(get(MessageListenerRegistration.class, Transfer.class));
        registrations.addBinding().to(get(MessageListenerRegistration.class, Store.class));

        bind(HazelcastDistributor.class);
    }
}