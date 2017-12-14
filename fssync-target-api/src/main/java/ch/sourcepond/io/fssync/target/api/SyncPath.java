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
package ch.sourcepond.io.fssync.target.api;

import java.util.Objects;

import static java.lang.String.format;

/**
 * An instance of this class combines a sending-node and a file path to a global-path. A global path is
 * network-wide unique.
 */
public class SyncPath {
    private final String syncDir;
    private final String path;

    public SyncPath(final String pSyncDir,
                    final String pPath) {
        syncDir = pSyncDir;
        path = pPath;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SyncPath that = (SyncPath) o;
        return Objects.equals(syncDir, that.syncDir) &&
                Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(syncDir, path);
    }

    public String getSyncDir() {
        return syncDir;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return format("[%s]:%s", syncDir, path);
    }
}