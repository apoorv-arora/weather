package io.gojek.weather;

import android.app.Application;
import android.content.Context;
import android.location.LocationManager;
import android.os.AsyncTask;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.List;

import io.gojek.weather.utils.CommonLib;
import io.gojek.weather.utils.VPrefsReader;
import io.gojek.weather.utils.location.ZLocationListener;
import io.gojek.weather.utils.networking.UploadManager;
import io.gojek.weather.utils.networking.UploadManagerCallback;

public class WApplication extends Application implements UploadManagerCallback {
    private VPrefsReader prefs;

    // location stuffs
    public ZLocationListener zll = new ZLocationListener(this);
    public LocationManager locationManager = null;
    public boolean isNetworkProviderEnabled = false;
    public boolean isGpsProviderEnabled = false;
    private CheckLocationTimeoutAsync checkLocationTimeoutThread;
    public double lat = 0;
    public double lon = 0;
    public boolean isLocationRequested = false;
    @Override
    public void onCreate() {
        super.onCreate();

        // init our piggi bank
        prefs = VPrefsReader.getInstance();
        prefs.setContext(getApplicationContext());

        // init our stalker
        UploadManager.getInstance().setContext(this);

    }

    @Override
    public void uploadStarted(int requestType, Object data, Object requestData) {

    }

    @Override
    public void uploadFinished(int requestType, Object data, boolean status, String errorMessage, Object requestData) {
    }


    public void startLocationCheck() {
        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (result == ConnectionResult.SUCCESS) {
            zll.getFusedLocation(this);
        } else {
            getAndroidLocation();
        }
    }

    public boolean isLocationAvailable() {
        return (isNetworkProviderEnabled || isGpsProviderEnabled);
    }

    public void getAndroidLocation() {

        CommonLib.VLog("zll", "getAndroidLocation");

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);

        if (providers != null) {
            for (String providerName : providers) {
                if (providerName.equals(LocationManager.GPS_PROVIDER))
                    isGpsProviderEnabled = true;
                if (providerName.equals(LocationManager.NETWORK_PROVIDER))
                    isNetworkProviderEnabled = true;
            }
        }

        if (isNetworkProviderEnabled || isGpsProviderEnabled) {

            if (isGpsProviderEnabled)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 500.0f, zll);
            if (isNetworkProviderEnabled)
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000L, 500.0f, zll);

            if (checkLocationTimeoutThread != null) {
                checkLocationTimeoutThread.interrupt = false;
            }

            checkLocationTimeoutThread = new CheckLocationTimeoutAsync();
            checkLocationTimeoutThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } else {
            zll.locationNotEnabled();
        }
    }

    private class CheckLocationTimeoutAsync extends AsyncTask<Void, Void, Void> {
        boolean interrupt = true;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void arg) {
            if (interrupt) {
                zll.interruptProcess();
            }
        }
    }

    public void interruptLocationTimeout() {
        // checkLocationTimeoutThread.interrupt();
        if (checkLocationTimeoutThread != null)
            checkLocationTimeoutThread.interrupt = false;
    }


}
