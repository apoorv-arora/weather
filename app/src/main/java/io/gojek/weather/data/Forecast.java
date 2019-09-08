package io.gojek.weather.data;

import java.io.Serializable;

public class Forecast implements Serializable {

    private double temperatureCelcius;
    private long timestamp;

    public Forecast (){
    }

    public double getTemperatureCelcius() {
        return temperatureCelcius;
    }

    public void setTemperatureCelcius(double temperatureCelcius) {
        this.temperatureCelcius = temperatureCelcius;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
