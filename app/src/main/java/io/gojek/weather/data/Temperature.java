package io.gojek.weather.data;

import java.io.Serializable;
import java.util.List;

public class Temperature implements Serializable {

    private double currentTemperatureCelcius;
    private List<Forecast> forecasts;

    public Temperature (){
    }

    public double getCurrentTemperatureCelcius() {
        return currentTemperatureCelcius;
    }

    public void setCurrentTemperatureCelcius(double currentTemperatureCelcius) {
        this.currentTemperatureCelcius = currentTemperatureCelcius;
    }

    public List<Forecast> getForecasts() {
        return forecasts;
    }

    public void setForecasts(List<Forecast> forecasts) {
        this.forecasts = forecasts;
    }
}
