package it.italiandudes.iiot_smartroom.javafx.utils;

import it.italiandudes.iiot_smartroom.javafx.JFXDefs;
import it.italiandudes.iiot_smartroom.utils.Defs;
import javafx.scene.Scene;
import org.jetbrains.annotations.NotNull;

public final class ThemeHandler {

    // Config Theme
    public static void loadTheme(@NotNull final Scene scene) {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(Defs.Resources.get(JFXDefs.Resources.CSS.CSS_THEME).toExternalForm());
    }
}
