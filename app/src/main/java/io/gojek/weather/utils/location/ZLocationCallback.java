package io.gojek.weather.utils.location;

import android.location.Location;

/**
 * Created by apoorvarora on 11/02/19.
 */

public interface ZLocationCallback {
    void onCoordinatesIdentified(Location loc);
    void onLocationIdentified();
    void onLocationNotIdentified();
    void onDifferentCityIdentified();
    void locationNotEnabled();
    void onLocationTimedOut();
    void onNetworkError();
}
