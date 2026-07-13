package it.italiandudes.iiot_smartroom.javafx.utils;

import it.italiandudes.iiot_smartroom.utils.Defs;
import it.italiandudes.idl.handler.JarHandler;
import it.italiandudes.idl.json.JSONManager;
import it.italiandudes.idl.logger.Logger;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public final class Settings {

    // Settings
    private static JSONObject SETTINGS = null;

    // Settings Loader
    public static void loadSettingsFile() {
        File settingsFile = new File(new File(Defs.JAR_POSITION).getParent() + File.separator + Defs.Resources.JSON.SETTINGS);
        if (!settingsFile.exists() || !settingsFile.isFile()) {
            try {
                JarHandler.copyFileFromJar(new File(Defs.JAR_POSITION), Defs.Resources.JSON.DEFAULT_JSON_SETTINGS, settingsFile, true);
            } catch (IOException e) {
                Logger.log(e, Defs.LOGGER_CONTEXT);
                return;
            }
        }
        try {
            SETTINGS = JSONManager.readJSON(settingsFile);
            fixJSONSettings();
        } catch (IOException | JSONException e) {
            Logger.log(e, Defs.LOGGER_CONTEXT);
        }
    }

    // Settings Checker
    private static void fixJSONSettings() throws JSONException, IOException {
        try {
            SETTINGS.getInt(Defs.SettingsKeys.BROKER_PORT);
        } catch (JSONException e) {
            SETTINGS.remove(Defs.SettingsKeys.BROKER_PORT);
            SETTINGS.put(Defs.SettingsKeys.BROKER_PORT, 1883);
        }
        writeJSONSettings();
    }

    // Settings Writer
    public static void writeJSONSettings() throws IOException {
        JSONManager.writeJSON(SETTINGS, new File(new File(Defs.JAR_POSITION).getParent() + File.separator + Defs.Resources.JSON.SETTINGS));
    }

    // Settings Getter
    @NotNull
    public static JSONObject getSettings() {
        return SETTINGS;
    }
}
