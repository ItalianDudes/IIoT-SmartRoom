package it.italiandudes.iiot_smartroom.mqtt.devices;

import it.italiandudes.idl.logger.Logger;
import it.italiandudes.iiot_smartroom.mqtt.interfaces.ISimulatedSensor;
import it.italiandudes.iiot_smartroom.mqtt.MQTTQoS;
import it.italiandudes.iiot_smartroom.utils.Defs;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class SimulatedMqttDevice {

    // Attributes
    protected final String deviceId;
    protected final String brokerUrl;
    protected MqttClient client;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    // Constructors
    public SimulatedMqttDevice(@NotNull final String deviceId, @NotNull final String brokerUrl) {
        this.deviceId = deviceId;
        this.brokerUrl = brokerUrl;
    }

    // Methods
    public final void connect() throws MqttException {
        if (client != null) throw new RuntimeException("MQTT Device already connected, please disconnect first.");
        client = new MqttClient(brokerUrl, deviceId, new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        client.connect(options);
        onConnected();
    }
    protected void onConnected() {} // Can be overridden
    protected final void subscribe(@NotNull final String topic, @SuppressWarnings("SameParameterValue") @NotNull final MQTTQoS qos, @NotNull final Consumer<String> handler) {
        try {
            client.subscribe(topic, qos.ordinal(), (t, msg) ->
                    handler.accept(new String(msg.getPayload(), StandardCharsets.UTF_8)));
        } catch (MqttException e) {
            Logger.log(e, Defs.LOGGER_CONTEXT);
        }
    }
    protected final void subscribe(@NotNull final String topic, @SuppressWarnings("SameParameterValue") @NotNull final MQTTQoS qos, @NotNull final BiConsumer<String, String> handler) {
        try {
            client.subscribe(topic, qos.ordinal(), (t, msg) ->
                    handler.accept(t, new String(msg.getPayload(), StandardCharsets.UTF_8)));
        } catch (MqttException e) {
            Logger.log(e, Defs.LOGGER_CONTEXT);
        }
    }
    protected final void publish(@NotNull final String topic, @NotNull final String payload, @SuppressWarnings("SameParameterValue") @NotNull final MQTTQoS qos, final boolean retained) {
        try {
            MqttMessage message = new MqttMessage(payload.getBytes(StandardCharsets.UTF_8));
            message.setQos(qos.ordinal());
            message.setRetained(retained);
            client.publish(topic, message);
        }catch (MqttException e) {
            Logger.log(e, Defs.LOGGER_CONTEXT);
        }
    }
    public final boolean isConnected() {
        return client != null && client.isConnected();
    }
    public final void disconnect() {
        if (client != null) {
            try {
                if (client.isConnected()) client.disconnect();
            } catch (Exception e) {
                Logger.log(e, Defs.LOGGER_CONTEXT);
            }
            stopSimulation();
            client = null;
        }
    }
    public final void startSimulation(final long periodMillis) {
        if (!(this instanceof ISimulatedSensor)) return;
        if (scheduler.isShutdown()) return;
        scheduler.scheduleAtFixedRate(((ISimulatedSensor) this)::simulateAndPublish, 0, periodMillis, TimeUnit.MILLISECONDS);
    }
    public final void stopSimulation() {
        if (!(this instanceof ISimulatedSensor)) return;
        scheduler.shutdown();
    }
}
