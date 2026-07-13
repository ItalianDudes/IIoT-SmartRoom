package it.italiandudes.iiot_smartroom.utils;

import it.italiandudes.idl.common.TargetPlatform;
import it.italiandudes.iiot_smartroom.IIoT_SmartRoom;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

@SuppressWarnings("unused")
public final class Defs {

    // App Name (This name must match the repository name)
    public static final String APP_NAME = "IIoT_SmartRoom";

    // Current Platform
    @Nullable public static final TargetPlatform CURRENT_PLATFORM = TargetPlatform.getCurrentPlatform();

    // Debug Mode?
    public static final boolean IS_DEBUG_MODE = false;

    // Smart Home Name
    public static final String SMART_ROOM_NAME = "SmartRoom6329";

    // Logger Context
    public static final String LOGGER_CONTEXT = "IIoT-SmartRoom";

    // Jar App Position
    public static final String JAR_POSITION;
    static {
        try {
            JAR_POSITION = new File(IIoT_SmartRoom.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    // JSON Settings
    public static final class SettingsKeys {
        public static final String BROKER_PORT = "broker_port";
    }

    // Resources Location
    public static final class Resources {

        // Project Resources Root
        public static final String PROJECT_RESOURCES_ROOT = "/it/italiandudes/iiot_smartroom/resources/";

        //Resource Getters
        public static URL get(@NotNull final String resourceConst) {
            return Objects.requireNonNull(IIoT_SmartRoom.class.getResource(resourceConst));
        }
        public static InputStream getAsStream(@NotNull final String resourceConst) {
            return Objects.requireNonNull(IIoT_SmartRoom.class.getResourceAsStream(resourceConst));
        }

        // JSON
        public static final class JSON {
            public static final String SETTINGS = "settings.json";
            public static final String DEFAULT_JSON_SETTINGS = PROJECT_RESOURCES_ROOT + "json/" + SETTINGS;
        }
    }
}
