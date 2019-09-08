package sanshinkan.org.warrior;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import io.fabric.sdk.android.Fabric;
import sanshinkan.org.warrior.data.NameValuePair;
import sanshinkan.org.warrior.data.NewsFeed;
import sanshinkan.org.warrior.utils.CommonLib;
import sanshinkan.org.warrior.utils.ParserJson;
import sanshinkan.org.warrior.utils.VPrefsReader;
import sanshinkan.org.warrior.utils.cache.LruCache;
import sanshinkan.org.warrior.utils.location.ZLocationListener;
import sanshinkan.org.warrior.utils.networking.UploadManager;
import sanshinkan.org.warrior.utils.networking.UploadManagerCallback;

/**
 * Created by apoorvarora on 11/02/19.
 */

public class SApplication extends Application implements UploadManagerCallback {
    public LruCache<String, Bitmap> cache;
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

        // add callbacks
//        UploadManager.getInstance().addCallback(this
//                , UploadManager.APP_CONFIG);

        // init our insta
        cache = new LruCache<String, Bitmap>(30);

//        new ThirdPartyInitAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class ThirdPartyInitAsync extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            try {
                Fabric.with(getApplicationContext(), new Crashlytics());
            } catch (Exception e) {
                if (!CommonLib.VYOMLOG)
                    Crashlytics.logException(e);
                e.printStackTrace();
            }
            return null;
        }
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private class DeleteTokenAsync extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            try {
//                FirebaseInstanceId.getInstance().deleteInstanceId();
            } catch (Exception e) {
                if (!CommonLib.VYOMLOG) Crashlytics.logException(e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            onTokenRefresh();
        }
    }

    public void onTokenRefresh() {
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        CommonLib.VLog("Token", "Refreshed token: " + refreshedToken);
//        prefs.setPref(CommonLib.PROPERTY_REG_ID, refreshedToken);
//        if (prefs.getPref(CommonLib.PROPERTY_USER_ID, 0) > 0)
//            registerInBackground(refreshedToken);
    }

    public void registerInBackground(String regId) {
        if (regId != null && !regId.isEmpty()) {
            // Getting registration token
            String requestUrl = CommonLib.SERVER_URL + "user/notification";
            JSONObject requestJson = new JSONObject();
            try {
                requestJson.put("registrationId", regId);
            } catch (JSONException e) {
                if (!CommonLib.VYOMLOG) Crashlytics.logException(e);
                e.printStackTrace();
            }
            if (CommonLib.isNetworkAvailable(getApplicationContext())) {
//                UploadManager.getInstance().apiCallWithPriority(UploadManager.FCM_REGISTER, "", requestJson, null, UploadManager.REQUEST_PRIORITY_NORMAL);
            }
        }
    }

    @Override
    public void onLowMemory() {
        cache.clear();
        super.onLowMemory();
    }

    public void onTrimLevel(int i) {
        cache.clear();
        super.onTrimMemory(i);
    }

    public void logout() {
        prefs.clearPrefs();

        try {
            new DeleteTokenAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (RejectedExecutionException exception) {
            if (!CommonLib.VYOMLOG)
                Crashlytics.logException(exception);

            CommonLib.VLog(this.getClass().getSimpleName(), exception.getMessage());
        }
    }

    public void savePrefs() {

        // save prefs
        String var1 = prefs.getPref(CommonLib.PROPERTY_ACCESS_TOKEN, "");
        int var3 = prefs.getPref(CommonLib.PROPERTY_USER_ID, 0);
        String var4 = prefs.getPref(CommonLib.PROPERTY_USER_NAME, "");
        String var5 = prefs.getPref(CommonLib.PROPERTY_USER_PHONE_NUMBER, "");
        String var8 = prefs.getPref(CommonLib.PROPERTY_USER_PROFILE_PIC, "");
        String var17 = prefs.getPref(CommonLib.PROPERTY_REG_ID, "");

        // clear prefs
        prefs.clearPrefs();

        // update prefs
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new NameValuePair(CommonLib.PROPERTY_ACCESS_TOKEN, var1));
        pairs.add(new NameValuePair(CommonLib.PROPERTY_USER_ID, var3));
        pairs.add(new NameValuePair(CommonLib.PROPERTY_USER_PHONE_NUMBER,var5));
        pairs.add(new NameValuePair(CommonLib.PROPERTY_USER_PROFILE_PIC, var8));
        pairs.add(new NameValuePair(CommonLib.PROPERTY_REG_ID, var17));

        prefs.setPrefs(pairs);
    }

    @Override
    public void uploadStarted(int requestType, Object data, Object requestData) {

    }

    @Override
    public void uploadFinished(int requestType, Object data, boolean status, String errorMessage, Object requestData) {
    }

    public void interruptLocationTimeout() {
        // checkLocationTimeoutThread.interrupt();
        if (checkLocationTimeoutThread != null)
            checkLocationTimeoutThread.interrupt = false;
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

//            if (isGpsProviderEnabled)
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 500.0f, zll);
//            if (isNetworkProviderEnabled)
//                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000L, 500.0f, zll);
//
//            if (checkLocationTimeoutThread != null) {
//                checkLocationTimeoutThread.interrupt = false;
//            }

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

    @NonNull
    public List<NewsFeed> getNewsFeed() {
        List<NewsFeed> newsFeedList = new ArrayList<>();
        // read from file
        InputStream inputStream;
        File file = null;
        try {
            inputStream = getAssets().open(CommonLib.EVENTS);
            file = CommonLib.createFileFromInputStream(inputStream, CommonLib.EVENTS);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (file != null) {
            //Read text from file
            StringBuilder text = new StringBuilder();

            FileReader fileReader = null;
            try {
                fileReader = new FileReader(file);
                BufferedReader br = new BufferedReader(fileReader);
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fileReader != null) {
                        fileReader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                newsFeedList.addAll(ParserJson.parse_FeedResponse(new JSONArray(text.toString())));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return newsFeedList;
    }

}