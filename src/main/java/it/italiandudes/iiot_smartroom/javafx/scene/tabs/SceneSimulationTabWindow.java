package it.italiandudes.iiot_smartroom.javafx.scene.tabs;

import it.italiandudes.idl.javafx.components.SceneController;
import it.italiandudes.idl.logger.Logger;
import it.italiandudes.iiot_smartroom.javafx.Client;
import it.italiandudes.iiot_smartroom.javafx.JFXDefs;
import it.italiandudes.iiot_smartroom.javafx.controllers.tabs.ControllerSceneSimulationTabWindow;
import it.italiandudes.iiot_smartroom.mqtt.DataCollectorAndManager;
import it.italiandudes.iiot_smartroom.utils.Defs;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;

public final class SceneSimulationTabWindow {

    // Scene Generator
    @NotNull
    public static SceneController getScene(@NotNull final DataCollectorAndManager dcm) {
        return Objects.requireNonNull(genScene(dcm));
    }
    @Nullable
    private static SceneController genScene(@NotNull final DataCollectorAndManager dcm) {
        try {
            FXMLLoader loader = new FXMLLoader(Defs.Resources.get(JFXDefs.Resources.FXML.SimulationTabs.TAB_WINDOW));
            Parent root = loader.load();
            ControllerSceneSimulationTabWindow controller = loader.getController();
            controller.setDCM(dcm);
            controller.configurationComplete();
            return new SceneController(root, controller);
        } catch (IOException e) {
            Logger.log(e, Defs.LOGGER_CONTEXT);
            Client.exit(-1);
            return null;
        }
    }
}
