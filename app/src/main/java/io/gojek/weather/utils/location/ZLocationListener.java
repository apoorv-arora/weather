package io.gojek.weather.utils.location;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import io.gojek.weather.WApplication;
import io.gojek.weather.utils.CommonLib;

public class ZLocationListener implements com.google.android.gms.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

private ArrayList<ZLocationCallback> callbacks;
private WApplication zapp;
public boolean forced = false;
public boolean receiveUpdates = false;

private GoogleApiClient mGoogleApiClient;

public ZLocationListener(WApplication zapp) {
        callbacks = new ArrayList<ZLocationCallback>();
        this.zapp = zapp;
        }

public void addCallback(ZLocationCallback callback) {
        callbacks.add(callback);
        }

public void removeCallback(ZLocationCallback callback) {
        if (receiveUpdates && mGoogleApiClient != null && mGoogleApiClient.isConnected())
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        if (callbacks.contains(callback))
        callbacks.remove(callback);
        }

/**
 * Called when the location has changed.
 * <p/>
 * <p>
 * There are no restrictions on the use of the supplied Location object.
 *
 * @param loc
 *            The new location, as a Location object.
 */
@Override
public void onLocationChanged(Location loc) {
        if (loc != null) {

        boolean callToBeFired = false;
        if (forced || CommonLib.distFrom(zapp.lat, zapp.lon, loc.getLatitude(), loc.getLongitude()) > .2) {
        zapp.lat = loc.getLatitude();
        zapp.lon = loc.getLongitude();
        callToBeFired = true;
        }

        zapp.interruptLocationTimeout();

        for (ZLocationCallback callback : callbacks) {
        // zapp.currentCity = zapp.getCityIdFromLocation(loc);
        callback.onCoordinatesIdentified(loc);
        }

        CommonLib.VLog("zll", "call to be fired " + forced);
        // if(zapp.locationManager != null) {
        // zapp.locationManager.removeUpdates(this);
        // }

        }

        if (zapp.locationManager != null) {
        zapp.locationManager.removeUpdates(this);
        }

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected() && !receiveUpdates) {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
        }

/**
 * Called when the provider status changes. This method is called when a
 * provider is unable to fetch a location or if the provider has recently
 * become available after a period of unavailability.
 *
 * @param provider
 *            the name of the location provider associated with this update.
 * @param status
 *            {@link android.location.LocationProvider#OUT_OF_SERVICE} if
 *            the provider is out of service, and this is not expected to
 *            change in the near future;
 *            {@link android.location.LocationProvider#TEMPORARILY_UNAVAILABLE}
 *            if the provider is temporarily unavailable but is expected to
 *            be available shortly; and
 *            {@link android.location.LocationProvider#AVAILABLE} if the
 *            provider is currently available.
 * @param extras
 *            an optional Bundle which will contain provider specific status
 *            variables.
 *            <p/>
 *            <p>
 *            A number of common key/value pairs for the extras Bundle are
 *            listed below. Providers that use any of the keys on this list
 *            must provide the corresponding value as described below.
 *            <p/>
 *            <ul>
 *            <li>satellites - the number of satellites used to derive the
 *            fix
 */
@Override
public void onStatusChanged(String provider, int status, Bundle extras) {
        CommonLib.VLog("zll ", "onStatusChanged");
        }

/**
 * Called when the provider is enabled by the user.
 *
 * @param provider
 *            the name of the location provider associated with this update.
 */
@Override
public void onProviderEnabled(String provider) {
        CommonLib.VLog("zll ", "onProviderEnabled");
        }

/**
 * Called when the provider is disabled by the user. If
 * requestLocationUpdates is called on an already disabled provider, this
 * method is called immediately.
 *
 * @param provider
 *            the name of the location provider associated with this update.
 */
@Override
public void onProviderDisabled(String provider) {
        CommonLib.VLog("zll ", "onProviderDisabled");
        }

public void getFusedLocation(WApplication zapp) {
        PackageManager pm = zapp.getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_LOCATION)) {
        this.zapp = zapp;
        mGoogleApiClient = new GoogleApiClient.Builder(zapp)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(LocationServices.API)
        .build();
        mGoogleApiClient.connect();
        }
        }

@Override
public void onConnected(Bundle bundle) {

        CommonLib.VLog("zll", "onConnected");
        Location currentLocation = null;
        try {
        PackageManager pm = zapp.getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_LOCATION)
        || pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)
        || pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_NETWORK)) {
        } else {
        return;
        }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mGoogleApiClient.isConnected()) {
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        if (currentLocation != null) {
//            LocationRequest mLocationRequest = LocationRequest.create();
            /*LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
            */onLocationChanged(currentLocation);
        } else {
        zapp.getAndroidLocation();
        }
        }

@Override
public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
        }

@Override
public void onConnectionFailed(ConnectionResult connectionResult) {
        CommonLib.VLog("zll", "onConnectionFailed");
        zapp.getAndroidLocation();
        }

public void locationNotEnabled() {
        CommonLib.VLog("zll", "locationNotEnabled");
        if (forced) {
        zapp.lat = 0;
        zapp.lon = 0;
        for (ZLocationCallback callback : callbacks)
        callback.locationNotEnabled();
        }
        }

public void interruptProcess() {
        CommonLib.VLog("zll", "interupptProcess");

        zapp.lat = 0;
        zapp.lon = 0;
        zapp.locationManager.removeUpdates(this);
        for (ZLocationCallback callback : callbacks) {
        callback.onLocationTimedOut();
        }
        }

        }
