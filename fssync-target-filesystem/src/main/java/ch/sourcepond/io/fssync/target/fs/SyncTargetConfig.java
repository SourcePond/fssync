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
package ch.sourcepond.io.fssync.target.fs;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import java.lang.annotation.Retention;
import java.util.concurrent.TimeUnit;

import static ch.sourcepond.io.fssync.target.fs.Activator.FACTORY_PID;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

@Retention(RUNTIME)
@ObjectClassDefinition(name = "Fssync target filesystem", description = "Configuration for target filesystem", factoryPid = {FACTORY_PID})
public @interface SyncTargetConfig {

    @AttributeDefinition(
            min = "0",
            description = "Timeout after which a file-lock is forced to be released"
    )
    long forceUnlockTimeout() default 5;

    @AttributeDefinition
    TimeUnit forceUnlockTimoutUnit() default MINUTES;

    @AttributeDefinition(min = "1")
    String syncDir() default "/";

    @AttributeDefinition(min = "1")
    long forceUnlockSchedulePeriod() default 10;

    @AttributeDefinition
    TimeUnit forceUnlockSchedulePeriodUnit() default SECONDS;
}