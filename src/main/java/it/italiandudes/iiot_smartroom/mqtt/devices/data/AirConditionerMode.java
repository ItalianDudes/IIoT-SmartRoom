package it.italiandudes.iiot_smartroom.mqtt.devices.data;

import org.jetbrains.annotations.NotNull;

public enum AirConditionerMode {
    DRY("DEUMIDIFICATORE"),
    COOL("RAFFREDDAMENTO"),
    HEAT("RISCALDAMENTO"),
    ECO("RISPARMIO ENERGETICO"),
    FAN("SOLO VENTOLA");

    // Attributes
    @NotNull public final String displayName;

    // Constructor
    AirConditionerMode(@NotNull final String displayName) {
        this.displayName = displayName;
    }
}
