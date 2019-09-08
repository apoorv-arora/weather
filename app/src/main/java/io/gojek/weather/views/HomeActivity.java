package io.gojek.weather.views;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import io.gojek.weather.R;
import io.gojek.weather.WApplication;
import io.gojek.weather.data.Forecast;
import io.gojek.weather.data.Temperature;
import io.gojek.weather.utils.CommonLib;
import io.gojek.weather.utils.location.ZLocationCallback;
import io.gojek.weather.utils.networking.UploadManager;
import io.gojek.weather.utils.networking.UploadManagerCallback;

public class HomeActivity extends AppCompatActivity implements UploadManagerCallback, ZLocationCallback {

    private WApplication sApplication;
    private boolean destroyed = false;
    private Activity mActivity;
    private GoogleApiClient googleApiClient;
    private final static int LOCATION_RESOLUTION_FIX = 1000;

    private TextView currentTemperatureTv, day1, temperature1, day2, temperature2, day3, temperature3, day4, temperature4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        UploadManager.getInstance().addCallback(this, UploadManager.FORECAST);

        mActivity = this;
        sApplication = (WApplication) getApplication();
        sApplication.zll.forced = true;
        sApplication.zll.addCallback(this);

        currentTemperatureTv = (TextView) findViewById(R.id.temperature);
        day1 = (TextView) findViewById(R.id.day1);
        day2 = (TextView) findViewById(R.id.day2);
        day3 = (TextView) findViewById(R.id.day3);
        day4 = (TextView) findViewById(R.id.day4);

        temperature1 = (TextView) findViewById(R.id.temperature1);
        temperature2 = (TextView) findViewById(R.id.temperature2);
        temperature3 = (TextView) findViewById(R.id.temperature3);
        temperature4 = (TextView) findViewById(R.id.temperature4);
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        locationCheck();

        findViewById(R.id.retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationCheck();
            }
        });
    }

    @Override
    public void onDestroy() {
        destroyed = true;
        UploadManager.getInstance().removeCallback(this, UploadManager.FORECAST);
        sApplication.zll.removeCallback(this);
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case CommonLib.MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationCheck();
                } else {
                    Toast.makeText(HomeActivity.this, "Please provide location permission to enable location autodetect.", Toast.LENGTH_SHORT).show();
                    toggleViews(false, null);
                }
            }
        }
    }

    @Override
    public void uploadStarted(int requestType, Object data, Object requestData) {
    }

    public void animateBottomSheet () {
        try {
            Animation animation1 = AnimationUtils.loadAnimation(mActivity, R.anim.slide_in_bottom);
            animation1.setDuration(1000);
            animation1.restrictDuration(1000);
            animation1.scaleCurrentDuration(1);
            findViewById(R.id.days_container).setVisibility(View.VISIBLE);
            findViewById(R.id.days_container).setAnimation(animation1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void uploadFinished(int requestType, Object data, boolean status, String errorMessage, Object requestData) {
        if (requestType == UploadManager.FORECAST) {
            if (!destroyed) {
                if (status && data instanceof Temperature) {
                    toggleViews(true, data);
                } else {
                    toggleViews(false, null);
                }
            }
        }
    }

    private void toggleViews (boolean show, Object data) {
        if (show) {
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            findViewById(R.id.empty_layout).setVisibility(View.GONE);
            findViewById(R.id.content_layout).setVisibility(View.VISIBLE);


            Temperature currentTemperature = (Temperature) data;
            currentTemperatureTv.setText(currentTemperature.getCurrentTemperatureCelcius() + "C");
            List<Forecast> forecasts = currentTemperature.getForecasts();
            if (forecasts != null && forecasts.size() > 3) {
                day1.setText(getDay(forecasts.get(0).getTimestamp()));
                temperature1.setText(forecasts.get(0).getTemperatureCelcius() + "C");

                day2.setText(getDay(forecasts.get(1).getTimestamp()));
                temperature2.setText(forecasts.get(1).getTemperatureCelcius() + "C");

                day3.setText(getDay(forecasts.get(2).getTimestamp()));
                temperature3.setText(forecasts.get(2).getTemperatureCelcius() + "C");

                day4.setText(getDay(forecasts.get(3).getTimestamp()));
                temperature4.setText(forecasts.get(3).getTemperatureCelcius() + "C");

                animateBottomSheet();
            }
        } else {
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            findViewById(R.id.empty_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.content_layout).setVisibility(View.GONE);
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        if(googleApiClient != null && googleApiClient.isConnected()){
            googleApiClient.disconnect();
        }
    }

    private String getDay (long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
        return dateFormat.format(date*1000);
    }

    private void startLocationCheck() {
        if (Build.VERSION.SDK_INT < 23) {
            sApplication.startLocationCheck();
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // add for location callbacks
            sApplication.startLocationCheck();
        } else
            showPermissionPrompt();
    }

    private void showPermissionPrompt() {
        ArrayList<String> permissionsList = new ArrayList<>();

        addPermissionToList(permissionsList,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionsList.size() > 0) {
            String[] permissions = new String[permissionsList.size()];
            for (int i=0; i<permissionsList.size(); i++) {
                permissions[i] = permissionsList.get(i);
            }
            CommonLib.requestMultiplePermissions(this, permissions, CommonLib.MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        }
    }

    private void addPermissionToList(ArrayList<String> permissionsList, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
            }
        }
    }

    @Override
    public void onCoordinatesIdentified(Location loc) {
        if (loc != null) {
            String params = "?key=2115afee114d45e6a3285650190609&q=" + loc.getLatitude() + "," + loc.getLongitude() + "&days=4";
            UploadManager.getInstance().apiCall(null, UploadManager.FORECAST, params, null);

            try {
                ((TextView) findViewById(R.id.state)).setText(CommonLib.getCity(loc.getLatitude(), loc.getLongitude(), this));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            toggleViews(false, null);
    }

    @Override
    public void onLocationIdentified() {
        CommonLib.VLog("loc", "identified");
    }

    @Override
    public void onLocationNotIdentified() {
        toggleViews(false, null);
        CommonLib.VLog("loc", "not identified");
    }

    @Override
    public void onDifferentCityIdentified() {
        toggleViews(false, null);
        CommonLib.VLog("loc", "different city identified");
    }

    @Override
    public void locationNotEnabled() {
        toggleViews(false, null);
        CommonLib.VLog("loc", "not enabled");
    }

    @Override
    public void onLocationTimedOut() {
        toggleViews(false, null);
        CommonLib.VLog("loc", "location timed out");
    }

    @Override
    public void onNetworkError() {
        toggleViews(false, null);
        CommonLib.VLog("loc", "network error");
    }

    private void locationCheck() {
        googleApiClient = new GoogleApiClient.Builder(HomeActivity.this)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        final LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result1) {
                handleLocationSettingResult(result1);
                final Status status = result1.getStatus();
                if (status != null) {
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            CommonLib.VLog("d", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                            break;
                    }
                }
            }
        });

    }

    private void handleLocationSettingResult(@Nullable LocationSettingsResult result) {
        if (!destroyed && result != null) {
            final Status status = result.getStatus();
            if (status != null) {
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        CommonLib.VLog("b", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                        try {
                            status.startResolutionForResult(HomeActivity.this, LOCATION_RESOLUTION_FIX);
                            return;
                        } catch (IntentSender.SendIntentException e) {
                            CommonLib.VLog("c", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SUCCESS:
                        startLocationCheck();
                        break;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == LOCATION_RESOLUTION_FIX) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    sApplication.zll.forced = true;
                    startLocationCheck();
                    break;
                case Activity.RESULT_CANCELED:
//                    emptyView.setVisibility(View.VISIBLE);
//                    findViewById(R.id.loader).setVisibility(View.GONE);
                    Toast.makeText(HomeActivity.this, "Please turn on location services to enable autodetect.", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}