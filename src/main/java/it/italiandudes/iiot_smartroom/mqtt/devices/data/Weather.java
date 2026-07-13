package it.italiandudes.iiot_smartroom.mqtt.devices.data;

import org.jetbrains.annotations.NotNull;

public enum Weather {
    CLEAR("SERENO",5, 1),
    WINDY("VENTOSO",20, 1),
    RAIN("PIOGGIA",5, 2.5),
    STORM("TEMPESTA",35, 7.5);

    // Attributes
    @NotNull public final String displayName;
    public final double windThreshold;
    public final double rainThreshold;

    // Constructor
    Weather(@NotNull final String displayName, final double windThreshold, final double rainThreshold) {
        this.displayName = displayName;
        this.windThreshold = windThreshold;
        this.rainThreshold = rainThreshold;
    }
}
