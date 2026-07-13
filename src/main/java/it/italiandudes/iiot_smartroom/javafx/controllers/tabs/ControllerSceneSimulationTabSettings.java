package it.italiandudes.iiot_smartroom.javafx.controllers.tabs;

import it.italiandudes.idl.javafx.JFXUtils;
import it.italiandudes.idl.javafx.alert.ConfirmationAlert;
import it.italiandudes.iiot_smartroom.javafx.Client;
import it.italiandudes.iiot_smartroom.javafx.controllers.ControllerSceneSimulation;
import it.italiandudes.iiot_smartroom.javafx.scene.SceneLoading;
import it.italiandudes.iiot_smartroom.javafx.scene.SceneMainMenu;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.jetbrains.annotations.NotNull;

public final class ControllerSceneSimulationTabSettings {

    // Attributes
    private ControllerSceneSimulation mainController = null;
    private volatile boolean configurationComplete = false;

    // Methods
    public void setMainController(@NotNull final ControllerSceneSimulation mainController) {
        this.mainController = mainController;
    }
    public void configurationComplete() {
        configurationComplete = true;
    }

    // Graphic Elements
    @FXML private Button buttonBack;
    @FXML private Button buttonExit;

    // Initialize
    @FXML
    private void initialize() {
        JFXUtils.startVoidServiceTask(() -> {
            while (!configurationComplete) Thread.onSpinWait();
            Platform.runLater(() -> {
                buttonBack.setDisable(false);
                buttonExit.setDisable(false);
            });
        });
    }

    // EDT
    @FXML
    public void backToMenu() {
        if(!new ConfirmationAlert(Client.getStage(),"MENU", "Tornare al Menu", "Vuoi davvero tornare al menu principale?").result) return;
        Client.setScene(SceneLoading.getScene());
        JFXUtils.startVoidServiceTask(() -> {
            mainController.shutdownMQTT();
            Platform.runLater(() -> Client.setScene(SceneMainMenu.getScene()));
        });
    }
    @FXML
    public void exitApp() {
        if(!new ConfirmationAlert(Client.getStage(),"CHIUSURA", "Chiusura Applicazione", "Vuoi davvero chiudere l'applicazione?").result) return;
        Client.setScene(SceneLoading.getScene());
        JFXUtils.startVoidServiceTask(() -> {
            mainController.shutdownMQTT();
            Platform.runLater(Client::exit);
        });
    }
}
