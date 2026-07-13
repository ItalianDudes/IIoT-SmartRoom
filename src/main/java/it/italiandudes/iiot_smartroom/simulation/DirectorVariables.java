package it.italiandudes.iiot_smartroom.simulation;

/**
 * This class contains the real data of the simulation, with no error/jitter.
 * Variables contained inside this class are meant to be manually changed by the user during the simulation
 * with console or UI.
 */
public final class DirectorVariables {

    // External Data
    public static volatile double EXTERNAL_TEMPERATURE_CEL = 26.0;
    public static volatile double EXTERNAL_HUMIDITY_RH = 60.0;
    public static volatile double WIND_SPEED_KMH = 0.0;
    public static volatile double RAIN_VOLUME_MM = 0.0;
    public static volatile int PM10_UG_M3 = 0;

    // Room Data
    public static volatile int ENERGY_CONSUMPTION_W = 720;
    public static volatile boolean IS_DOOR_OPEN = false;
    public static volatile boolean IS_WINDOW_OPEN = false;
}
