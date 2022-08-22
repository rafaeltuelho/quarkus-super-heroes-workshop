package io.quarkus.workshop.superheroes.hero;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.jboss.logging.Logger;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.configuration.ProfileManager;

@ApplicationScoped
public class HeroApplicationLifeCycle {
    private static final Logger LOGGER = Logger.getLogger(HeroApplicationLifeCycle.class);

    void onStart(@Observes StartupEvent ev) {
        LOGGER.info("The application HERO is starting with profile " + ProfileManager.getActiveProfile());
        LOGGER.info(" _    _                _                _____ _____ ");
        LOGGER.info(" | |  | |              ( )         /\\   |  __ \\_   _");
        LOGGER.info(" | |__| | ___ _ __ ___ |/ ___     /  \\  | |__) || | ");
        LOGGER.info(" |  __  |/ _ \\ '__/ _ \\  / __|   / /\\ \\ |  ___/ | | ");
        LOGGER.info(" | |  | |  __/ | | (_) | \\__ \\  / ____ \\| |    _| |_");
        LOGGER.info(" |_|  |_|\\___|_|  \\___/  |___/ /_/    \\_\\_|   |_____");
    }

    void onStop(@Observes ShutdownEvent ev) {
        LOGGER.info("The application HERO is stopping...");
    }
}
