package io.quarkus.workshop.superheroes.version.runtime;

import org.jboss.logging.Logger;

import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class VersionRecorder {
    public void printVersion(String version) {
        Logger.getLogger(VersionRecorder.class.getName()).infof("Version: %s", version);
    }
}
