/*Copyright (C) 2018 Roland Hauser, <sourcepond@gmail.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/
package ch.sourcepond.io.fssync.common.lib;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import java.util.function.Consumer;

import static java.lang.String.format;
import static org.osgi.framework.Constants.OBJECTCLASS;
import static org.osgi.framework.ServiceEvent.REGISTERED;

public class ServiceListenerRegistrar {

    public <T> void registerListener(final BundleContext pBundleContext,
                                     final Consumer<T> pRegistration,
                                     final Runnable pUnregistration,
                                     final Class<T> pServiceInterface) {
        final ServiceListener listener = new ServiceListenerImpl<>(pBundleContext, pRegistration, pUnregistration);
        try {
            pBundleContext.addServiceListener(listener, format("(%s=%s)", OBJECTCLASS, pServiceInterface.getName()));
            for (final ServiceReference<T> reference : pBundleContext.getServiceReferences(pServiceInterface, null)) {
                listener.serviceChanged(new ServiceEvent(REGISTERED, reference));
            }
        } catch (final InvalidSyntaxException e) {
            // This should never happen
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
