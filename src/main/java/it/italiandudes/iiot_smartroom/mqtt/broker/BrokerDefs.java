package it.italiandudes.iiot_smartroom.mqtt.broker;

public final class BrokerDefs {

    public static final String BROKER_HOST = "127.0.0.1";
    public static String getBrokerURL(int port) {
        return "tcp://" + BROKER_HOST + ":" + port;
    }
}
