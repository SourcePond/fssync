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

import ch.sourcepond.io.fssync.common.api.SyncPath;
import ch.sourcepond.io.fssync.target.api.NodeInfo;
import ch.sourcepond.io.fssync.target.api.SyncTarget;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.MavenUrlReference;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.util.tracker.ServiceTracker;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Hashtable;

import static ch.sourcepond.testing.OptionsHelper.karafContainer;
import static ch.sourcepond.testing.OptionsHelper.provisionBundlesFromUserDir;
import static java.lang.System.getProperty;
import static java.nio.file.FileSystems.getDefault;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static org.osgi.framework.Constants.OBJECTCLASS;
import static org.osgi.framework.Constants.SERVICE_PID;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class TargetFilesystemTest {
    private static final String FACTORY_PID = "ch.sourcepond.io.fssync.target.fs.TargetDirectory";
    private static final String EXPECTED_LOCAL_NODE = "expectedLocalNode";
    private static final String EXPECTED_REMOTE_NODE = "expectedRemoteNode";
    private final Path testSyncBaseDir = getDefault().getPath(getProperty("user.dir"), "build", "test_syncdir");

    @Inject
    private SyncTarget defaultSyncTarget;

    @Inject
    private ConfigurationAdmin configAdmin;

    @Inject
    private BundleContext context;

    private TestCustomizer customizer;
    private ServiceTracker<SyncTarget, SyncTarget> tracker;

    @Configuration
    public Option[] configure() throws Exception {
        MavenUrlReference karafStandardRepo = maven()
                .groupId("org.apache.karaf.features")
                .artifactId("standard")
                .classifier("features")
                .type("xml")
                .versionAsInProject();
        return new Option[]{
                karafContainer(getDefault().getPath(getProperty("user.dir"), "build", "paxexam"),
                        features(karafStandardRepo)),
                mavenBundle("commons-io", "commons-io").versionAsInProject(),
                provisionBundlesFromUserDir("build", "paxexam")
        };
    }

    @Before
    public void setup() throws Exception {
        customizer = new TestCustomizer(context);
        tracker = new ServiceTracker<>(context,
                context.createFilter(String.format("(&(%s=%s)(!(%s=%s)))", OBJECTCLASS, SyncTarget.class.getName(), SERVICE_PID, FACTORY_PID)),
                customizer);
        tracker.open();
    }

    @After
    public void tearDown() throws Exception {
        deleteDirectory(getDefault().getPath(getProperty("user.dir"), "temp_testdata").toFile());
        tracker.close();
    }

    @Test
    public void verifyDefaultSync() throws IOException {
        final NodeInfo ni = new NodeInfo(EXPECTED_REMOTE_NODE, EXPECTED_LOCAL_NODE);
        final SyncPath syncPath = new SyncPath(File.separator, getProperty("user.dir"), "temp_testdata/examfile.txt");
        defaultSyncTarget.lock(ni, syncPath);
        assertTrue(Files.exists(getDefault().getPath(syncPath.getSyncDir(), syncPath.getRelativePath())));
    }

    @Test
    public void registerAndUnregisterSyncTargets() throws Exception {
        org.osgi.service.cm.Configuration config = configAdmin.createFactoryConfiguration(FACTORY_PID, null);
        final Hashtable<String, Object> values = new Hashtable<>();
        values.put("syncDir", testSyncBaseDir.resolve("temp_testdata2").toString());
        config.update(values);

        config = configAdmin.createFactoryConfiguration(FACTORY_PID, null);
        values.put("syncDir", testSyncBaseDir.resolve("temp_testdata3").toString());
        config.update(values);
        customizer.waitForRegistrations();

        final Object[] services = tracker.getServices();
        assertEquals(2, services.length);
        assertTrue(customizer.targets.contains(services[0]));
        assertTrue(customizer.targets.contains(services[1]));
    }
}
