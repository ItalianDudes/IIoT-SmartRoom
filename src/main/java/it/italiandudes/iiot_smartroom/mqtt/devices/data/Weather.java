package it.italiandudes.iiot_smartroom.mqtt.devices.data;

public enum Weather {
    CLEAR(5, 1),
    WINDY(20, 1),
    RAIN(5, 2.5),
    STORM(35, 7.5);

    // Attributes
    public final double windThreshold;
    public final double rainThreshold;

    // Constructor
    Weather(final double windThreshold, final double rainThreshold) {
        this.windThreshold = windThreshold;
        this.rainThreshold = rainThreshold;
    }
}
