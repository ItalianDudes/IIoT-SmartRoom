package it.italiandudes.iiot_smartroom.javafx;

import it.italiandudes.iiot_smartroom.utils.Defs;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.stage.Screen;

@SuppressWarnings("unused")
public final class JFXDefs {

    // App Info
    public static final class AppInfo {
        public static final String NAME = "IIoT-SmartRoom";
        public static final Image LOGO = new Image(Defs.Resources.get(Resources.Image.IMAGE_LOGO).toString());
    }

    // System Info
    public static final class SystemGraphicInfo {
        public static final Rectangle2D SCREEN_RESOLUTION = Screen.getPrimary().getBounds();
        public static final double SCREEN_WIDTH = SCREEN_RESOLUTION.getWidth();
        public static final double SCREEN_HEIGHT = SCREEN_RESOLUTION.getHeight();
    }

    // Resource Locations
    public static final class Resources {

        // FXML Location
        public static final class FXML {
            private static final String FXML_DIR = Defs.Resources.PROJECT_RESOURCES_ROOT + "fxml/";
            public static final String FXML_LOADING = FXML_DIR + "SceneLoading.fxml";
            public static final String FXML_MAIN_MENU = FXML_DIR + "SceneMainMenu.fxml";
            public static final String FXML_SIMULATION = FXML_DIR + "SceneSimulation.fxml";
            public static final class SimulationTabs {
                private static final String TABS_DIR = FXML_DIR + "tabs/";
                public static final String TAB_DOOR = TABS_DIR + "SceneSimulationTabDoor.fxml";
                public static final String TAB_WINDOW = TABS_DIR + "SceneSimulationTabWindow.fxml";
                public static final String TAB_ENVIRONMENTAL_MONITOR = TABS_DIR + "SceneSimulationTabEnvironmentalMonitor.fxml";
                public static final String TAB_DISPLAY = TABS_DIR + "SceneSimulationTabDisplay.fxml";
                public static final String TAB_CONDITIONER = TABS_DIR + "SceneSimulationTabAirConditioner.fxml";
                public static final String TAB_ELECTRICAL_PANEL = TABS_DIR + "SceneSimulationTabElectricalPanel.fxml";
                public static final String TAB_DIRECTOR = TABS_DIR + "SceneSimulationTabDirector.fxml";
                public static final String TAB_SETTINGS = TABS_DIR + "SceneSimulationTabSettings.fxml";
            }
        }

        // GIF Location
        public static final class GIF {
            private static final String GIF_DIR = Defs.Resources.PROJECT_RESOURCES_ROOT + "gif/";
            public static final String GIF_LOADING = GIF_DIR+"loading.gif";
        }

        // CSS Location
        public static final class CSS {
            private static final String CSS_DIR = Defs.Resources.PROJECT_RESOURCES_ROOT + "css/";
            public static final String CSS_THEME = CSS_DIR + "theme.css";
        }

        // Image Location
        public static final class Image {
            private static final String IMAGE_DIR = Defs.Resources.PROJECT_RESOURCES_ROOT + "image/";
            public static final String IMAGE_LOGO = IMAGE_DIR + "logo.png";
            public static final String IMAGE_FILE_EXPLORER = IMAGE_DIR+"file-explorer.png";
            public static final String IMAGE_PLAY = IMAGE_DIR + "play.png";
            public static final String IMAGE_STOP = IMAGE_DIR + "stop.png";
        }

    }

}
