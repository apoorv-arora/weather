package io.gojek.weather.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import io.gojek.weather.BuildConfig;

/**
 * Created by apoorvarora on 21/04/17.
 */
public class CommonLib {
    public final static boolean VYOMLOG = true;

    // Font file def follows
    public static String FONT_MEDIUM = "fonts/home_medium.ttf";
    public static String FONT_LIGHT = "fonts/home_light.ttf";
    public static String FONT_REGULAR = "fonts/home_regular.ttf";
    public static String FONT_BOLD = "fonts/home_bold.ttf";
    public static String Icons = "fonts/home_icons.ttf";

    public static final String APP_CONFIG = "bundle/appConfig.json";
    public static final String EVENTS = "bundle/events.json";

    public static final int DIALOG_VANISH = 135;

    /** Preferences Files
     */
    public final static String APP_SETTINGS = "application_settings";
    public final static String ONE_LAUNCH_SETTINGS = "application_settings_once";

    public static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 201;

    public static final String HEADER_KEY_VERSION = "v";
    public static final String HEADER_KEY_ACCEPT_FORMAT = "Accept";
    public static final String HEADER_KEY_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_KEY_ENCODING ="Accept-Encoding";

    /**
     * API call and stuffs
     */
    public static final int CONNECTION_TIMEOUT = 15; // connection time out in seconds
    public static final String SERVER_URL = BuildConfig.HOST;

    // Hashtable for different type faces
    public static final Hashtable<String, Typeface> typefaces = new Hashtable<String, Typeface>();

    // Fetches the typeface to set for the text views
    public static Typeface getTypeface(Context c, String name) {
        synchronized (typefaces) {
            if (!typefaces.containsKey(name)) {
                try {
                    InputStream inputStream = c.getAssets().open(name);
                    File file = createFileFromInputStream(inputStream, name);
                    if (file == null) {
                        return Typeface.DEFAULT;
                    }
                    Typeface t = Typeface.createFromFile(file);
                    typefaces.put(name, t);
                } catch (Exception e) {
                    e.printStackTrace();
                    return Typeface.DEFAULT;
                }
            }
            return typefaces.get(name);
        }
    }

    // Creates the file from input stream
    public static File createFileFromInputStream(InputStream inputStream, String name) {

        try {
            File f = File.createTempFile("font", null);
            OutputStream outputStream = new FileOutputStream(f);
            byte buffer[] = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();
            return f;
        } catch (Exception e) {
            // Logging exception
            e.printStackTrace();
        }

        return null;
    }

    // Logging end points
    public static void VLog(String Tag, String Message) {
        if (VYOMLOG && Message != null)
            Log.i(Tag, Message);
    }

    public static void VLog(String Tag, float Message) {
        if (VYOMLOG)
            Log.i(Tag, Message + "");
    }

    public static void VLog(String Tag, boolean Message) {
        if (VYOMLOG)
            Log.i(Tag, Message + "");
    }

    public static void VLog(String Tag, int Message) {
        if (VYOMLOG)
            Log.i(Tag, Message + "");
    }

    // Checks if network is available
    public static boolean isNetworkAvailable(Context c) {
        ConnectivityManager connectivityManager = (ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String formatNumber(int n) {
        String formattedString = "";
        try {
            NumberFormat NF = NumberFormat.getInstance();
            formattedString =  NF.format(n);
        }catch (Exception exception){
            formattedString = String.valueOf(n);
        }
        return formattedString;
    }

    public static String constructFileName(String url) {
        return url.replaceAll("/", "_");
    }


    public static void requestMultiplePermissions(Context mContext, ArrayList<String> permissionsList, final int permission_code) {
        ActivityCompat.requestPermissions((Activity) mContext, permissionsList.toArray(new String[permissionsList.size()]), permission_code);
    }

    public static void requestMultiplePermissions(Context mContext, String[] permissionArr, final int permission_code) {
        ActivityCompat.requestPermissions((Activity) mContext, permissionArr, permission_code);
    }

    public static float distFrom(double lat1, double lng1, double lat2, double lng2) {
        float[] result = new float[3];
        Location.distanceBetween(lat1, lng1, lat2, lng2, result);
        //conversion to m
        result[0] = result[0] / 1000;

        return result[0];
    }

    public static String getCity (double lat, double lon, Context context) throws IOException {

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
        String output = "";
        if (addresses != null && addresses.size() > 0) {
            output = addresses.get(0).getAddressLine(0);
            if (output.equals("null"))
                output = "";
            else {
                if (output.contains(",")) {
                    String[] outputArr = output.split(",");
                    if (outputArr.length > 2)
                        output = outputArr[outputArr.length - 2];
                    else if (outputArr.length > 1)
                        output = outputArr[outputArr.length - 1];
                }
            }
        }
        return output;
    }

}
