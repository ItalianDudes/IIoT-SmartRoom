package it.italiandudes.iiot_smartroom.mqtt;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

public class SenMLRecord {

    // Attributes
    @Getter @NotNull private final String deviceId;
    @Getter @NotNull private final String name;
    @Getter private final String unit;
    @Getter private final Double value;
    @Getter private final String stringValue;
    @Getter private final Boolean boolValue;
    @Getter private final long timestamp;

    // Constructor
    private SenMLRecord(@NotNull final Builder b) {
        this.deviceId = b.deviceId;
        this.name = b.name;
        this.unit = b.unit;
        this.value = b.value;
        this.stringValue = b.stringValue;
        this.boolValue = b.boolValue;
        this.timestamp = b.timestamp;
    }

    // Methods
    public String toJson() {
        JSONArray pack = new JSONArray();

        JSONObject record = new JSONObject();
        record.put("bn", deviceId + ":");
        record.put("bt", timestamp / 1000L);
        record.put("n", name);
        if (unit != null) record.put("u", unit);
        if (value != null) record.put("v", value);
        if (boolValue != null) record.put("vb", boolValue);
        if (stringValue != null) record.put("vs", stringValue);
        pack.put(record);
        return pack.toString();
    }

    // Builder Method
    public static Builder builder(@NotNull final String deviceId, @NotNull final String name) {
        return new Builder(deviceId, name);
    }

    // Builder
    public static class Builder {
        @NotNull private final String deviceId;
        @NotNull private final String name;
        private String unit;
        private Double value;
        private String stringValue;
        private Boolean boolValue;
        private long timestamp = System.currentTimeMillis();

        private Builder(@NotNull String deviceId, @NotNull String name) {
            this.deviceId = deviceId;
            this.name = name;
        }

        public Builder unit(String unit) { this.unit = unit; return this; }
        public Builder value(double value) { this.value = value; return this; }
        public Builder stringValue(String stringValue) { this.stringValue = stringValue; return this; }
        public Builder boolValue(boolean boolValue) { this.boolValue = boolValue; return this; }
        public Builder timestamp(long ts) { this.timestamp = ts; return this; }
        public SenMLRecord build() { return new SenMLRecord(this); }
    }
}
