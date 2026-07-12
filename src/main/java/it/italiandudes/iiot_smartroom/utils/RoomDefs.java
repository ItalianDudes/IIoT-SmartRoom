package it.italiandudes.iiot_smartroom.utils;

public final class RoomDefs {

    // Arbitrary Room Humidity Constants
    public static final String ROOT_TOPIC = "room6329/";
    public static final String SENSORS_TOPIC = "sensors/";
    public static final String ACTUATORS_TOPIC = "actuators/";
    public static final String STATES_TOPIC = "states/";
    public static final String TOPIC_ALL_SENSORS = ROOT_TOPIC + "+/" + SENSORS_TOPIC + "#";
    public static final String TOPIC_ALL_STATES = ROOT_TOPIC + "+/" + STATES_TOPIC + "#";
    public static final double IDEAL_ROOM_HUMIDITY = 45;
}
