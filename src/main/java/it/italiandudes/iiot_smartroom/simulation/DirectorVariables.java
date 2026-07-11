package it.italiandudes.iiot_smartroom.simulation;

/**
 * This class contains the real data of the simulation, with no error/jitter.
 * Variables contained inside this class are meant to be manually changed by the user during the simulation
 * with console or UI.
 */
public final class DirectorVariables {

    // External Data
    public static final double EXTERNAL_TEMPERATURE_CEL = 26.0;
    public static final double EXTERNAL_HUMIDITY_RH = 60.0;
    public static final double WIND_SPEED_KMH = 0.0;
    public static final double RAIN_VOLUME_MM = 0.0;
    public static final int PM10_UG_M3 = 0;

    // Room Data
    public static final int ENERGY_CONSUMPTION_W = 720;
}
