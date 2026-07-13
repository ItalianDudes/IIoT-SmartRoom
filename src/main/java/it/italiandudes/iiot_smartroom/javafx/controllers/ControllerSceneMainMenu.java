package it.italiandudes.iiot_smartroom.javafx.controllers;

import it.italiandudes.idl.javafx.JFXUtils;
import it.italiandudes.idl.javafx.components.SceneController;
import it.italiandudes.iiot_smartroom.javafx.Client;
import it.italiandudes.iiot_smartroom.javafx.JFXDefs;
import it.italiandudes.iiot_smartroom.javafx.scene.SceneLoading;
import it.italiandudes.iiot_smartroom.javafx.scene.SceneSimulation;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

public final class ControllerSceneMainMenu {

    // Graphic Elements
    @FXML private ImageView imageViewLogo;

    // Initialize
    @FXML
    private void initialize() {
        imageViewLogo.setImage(JFXDefs.AppInfo.LOGO);
    }

    // EDT
    @FXML
    private void startSimulation() {
        Client.setScene(SceneLoading.getScene());
        final SceneController sceneController = SceneSimulation.getScene();
        JFXUtils.startVoidServiceTask(() -> {
            while (!((ControllerSceneSimulation) sceneController.getController()).isSimulationReady()) Thread.onSpinWait();
            Platform.runLater(() -> Client.setScene(sceneController));
        });
    }
}
