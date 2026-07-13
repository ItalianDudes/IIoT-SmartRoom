package it.italiandudes.iiot_smartroom.javafx.scene.tabs;

import it.italiandudes.idl.javafx.components.SceneController;
import it.italiandudes.idl.logger.Logger;
import it.italiandudes.iiot_smartroom.javafx.Client;
import it.italiandudes.iiot_smartroom.javafx.JFXDefs;
import it.italiandudes.iiot_smartroom.javafx.controllers.ControllerSceneSimulation;
import it.italiandudes.iiot_smartroom.javafx.controllers.tabs.ControllerSceneSimulationTabSettings;
import it.italiandudes.iiot_smartroom.utils.Defs;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;

public final class SceneSimulationTabSettings {

    // Scene Generator
    @NotNull
    public static SceneController getScene(@NotNull final ControllerSceneSimulation mainController) {
        return Objects.requireNonNull(genScene(mainController));
    }
    @Nullable
    private static SceneController genScene(@NotNull final ControllerSceneSimulation mainController) {
        try {
            FXMLLoader loader = new FXMLLoader(Defs.Resources.get(JFXDefs.Resources.FXML.SimulationTabs.TAB_SETTINGS));
            Parent root = loader.load();
            ControllerSceneSimulationTabSettings controller = loader.getController();
            controller.setMainController(mainController);
            controller.configurationComplete();
            return new SceneController(root, controller);
        } catch (IOException e) {
            Logger.log(e, Defs.LOGGER_CONTEXT);
            Client.exit(-1);
            return null;
        }
    }
}
