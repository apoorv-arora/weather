package io.gojek.weather.utils.networking;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.gojek.weather.R;
import io.gojek.weather.WApplication;
import io.gojek.weather.utils.CommonLib;
import io.gojek.weather.utils.ParserJson;

/**
 * Created by apoorvarora on 06/10/16.
 */
public class UploadManager {

    private Context context;
    private SharedPreferences prefs, oneTimePrefs;
    private HashMap<Integer, ArrayList<UploadManagerCallback>> callbacks = new HashMap<Integer, ArrayList<UploadManagerCallback>>();
    private static volatile UploadManager sInstance;
    private WApplication zapp;
    private static final int THREAD_POOL_LENGTH = 10;

    // Request tags
    public static final int FORECAST = 101;

    // Request urls
    private static final String URL_CURRENT = CommonLib.SERVER_URL + "forecast.json";

    // priorities, cause you must know to say NO; and NO means NO
    public static final Request.Priority REQUEST_PRIORITY_LOW = Request.Priority.LOW;
    public static final Request.Priority REQUEST_PRIORITY_NORMAL = Request.Priority.NORMAL;
    public static final Request.Priority REQUEST_PRIORITY_HIGH = Request.Priority.HIGH;
    public static final Request.Priority REQUEST_PRIORITY_IMMEDIATE = Request.Priority.IMMEDIATE;

    // request type
    public static final int REQUEST_TYPE_GET = Request.Method.GET;
    public static final int REQUEST_TYPE_PUT = Request.Method.PUT;
    public static final int REQUEST_TYPE_POST = Request.Method.POST;
    public static final int REQUEST_TYPE_DELETE = Request.Method.DELETE;

    // Volley stuffs
    private RequestQueue mRequestQueue;
    private static Map<Integer,RequestInfo> requestMap = new HashMap<>();

    public static class RequestInfo {
        String url;
        int networkRequestType;

        public RequestInfo(String url, int networkRequestType){
            this.url = url;
            this.networkRequestType = networkRequestType;
        }
    }

    static {
        requestMap.put(UploadManager.FORECAST, new RequestInfo(URL_CURRENT, REQUEST_TYPE_GET));
    }

    public static UploadManager getInstance() {
        if (sInstance == null) {
            synchronized (UploadManager.class) {
                if (sInstance == null) {
                    sInstance = new UploadManager();
                }
            }
        }
        return sInstance;
    }

    public void setContext(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(CommonLib.APP_SETTINGS, 0);
        oneTimePrefs = context.getSharedPreferences(CommonLib.ONE_LAUNCH_SETTINGS, 0);
        if (context instanceof WApplication) {
            zapp = (WApplication) context;
        }
        mRequestQueue = Volley.newRequestQueue(zapp);
    }

    public void addCallback(UploadManagerCallback callback, Integer... requestTypes) {
        for (Integer requestType : requestTypes) {
            if (!callbacks.containsKey(requestType)) {
                ArrayList<UploadManagerCallback> tempList = new ArrayList<>();
                tempList.add(callback);
                callbacks.put(requestType, tempList);
            }

            if (!callbacks.get(requestType).contains(callback)) {
                callbacks.get(requestType).add(callback);
            }
        }
    }

    public void removeCallback(UploadManagerCallback callback, Integer... requestTypes) {
        for (Integer requestType : requestTypes) {
            if (callbacks.containsKey(requestType) && callbacks.get(requestType).contains(callback)) {
                callbacks.get(requestType).remove(callback);
            }
        }
    }

    public void apiCallWithPriority(Map<String, String> paramsMap, int requestType, String requestParams, Object internalRequestObject, Request.Priority priority, Object... urlFormatters) {
        handleApiCall(paramsMap, requestType, requestParams, internalRequestObject, priority, urlFormatters);
    }

    public void apiCall(Map<String, String> paramsMap, int requestType, String requestParams, Object internalRequestObject, Object... urlFormatters) {
        handleApiCall(paramsMap, requestType, requestParams, internalRequestObject, Request.Priority.HIGH, urlFormatters);
    }

    private void handleApiCall(Map<String, String> paramsMap, int requestType, String requestParams, Object internalRequestObject, Request.Priority priority, Object... urlFormatters) {
        RequestInfo requestInfo = requestMap.get(requestType);
        String requestUrl = requestInfo.url;

        if (urlFormatters != null && urlFormatters.length > 0) {
            requestUrl = String.format(requestUrl, urlFormatters);
        }

        if (!TextUtils.isEmpty(requestParams)){
            requestUrl = requestUrl.concat(requestParams);
        }

        int type = requestInfo.networkRequestType;

        if (callbacks.get(requestType) != null) {
            for (UploadManagerCallback callback : callbacks.get(requestType)) {
                callback.uploadStarted(requestType, requestUrl, internalRequestObject);
            }
        }

        Request<?> volleyRequestObject = null;
        if (paramsMap instanceof Map && paramsMap != null)
            volleyRequestObject = getStringObjectRequest(paramsMap, type, requestUrl, requestType, priority, internalRequestObject);
        else if (paramsMap == null)
            volleyRequestObject = getStringObjectRequest(new HashMap<String, String>(), type, requestUrl, requestType, priority, internalRequestObject);

        mRequestQueue.add(volleyRequestObject);
    }

    public void cancelAllRequests(Object tag){
        mRequestQueue.cancelAll(tag);
    }

    private StringRequest getStringObjectRequest(final Map<String, String> paramsMap, int type, final String url, final int requestType, final Request.Priority priority, final Object reqObject) {
        CommonLib.VLog("Volley", "URL : " + url);
        CommonLib.VLog("Volley", "request object : " + paramsMap.toString());
        final long time = System.currentTimeMillis();

        StringRequest stringRequest = new StringRequest(type, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                handleResponse(response, requestType, url, time, reqObject);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleError(error, requestType, url, time, reqObject);
            }
        }) {
            @Override
            public Request.Priority getPriority() {
                return priority;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = UploadManager.getInstance().getHeaders();
                // remove the following header printing after debugging is done
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " <<<<<</>>>>>> " + entry.getValue());
                }
                return UploadManager.getInstance().getHeaders();
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(CommonLib.CONNECTION_TIMEOUT),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        stringRequest.setTag(type);
        return stringRequest;
    }

    public Map<String, String> getHeaders() {
        Map<String, String>  params = new HashMap<String, String>();
        params.put(CommonLib.HEADER_KEY_ACCEPT_FORMAT, "application/json");
        params.put(CommonLib.HEADER_KEY_ENCODING, "application/gzip");
        return params;
    }

    private void handleResponse (String response, int requestType, String url, long time, Object reqObject) {
        CommonLib.VLog("Volley", "Response : " + response.toString());

        Object result[] = new Object[]{"", false, ""};

        try {
            result = ParserJson.parseData(requestType, response);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result != null && result.length == 3 && result[1] instanceof Boolean && !(Boolean) result[1]) {
            if (CommonLib.isNetworkAvailable(context)) {
                if (!(result[2].equals("")))
                    Toast.makeText(context, (String) result[2], Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.no_internet_message), Toast.LENGTH_SHORT).show();
            }
        }

        if (callbacks.get(requestType) != null) {
            for (UploadManagerCallback callback : callbacks.get(requestType)) {
                callback.uploadFinished(requestType, result[0], (boolean) result[1], String.valueOf(result[2]), reqObject);
            }
        }

//        makeMixPanelCall(url, System.currentTimeMillis() - time);
    }

    private void handleError(VolleyError error, int requestType, String url, long time, Object reqObject) {
        CommonLib.VLog("Volley", "Error : " + error.toString());
        if (error.networkResponse != null) {
            int responseCode = error.networkResponse.statusCode;
//            if (responseCode == CommonLib.HTTP_DUDE_INVALID_TOKEN) {
//                zapp.logout();
//                Intent intent = new Intent(context, LoginActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);
//                return;
//            }
        }
        if (!CommonLib.isNetworkAvailable(context)) {
            Toast.makeText(context, context.getResources().getString(R.string.no_internet_message), Toast.LENGTH_SHORT).show();
        }

        if (callbacks.get(requestType) != null) {
            for (UploadManagerCallback callback : callbacks.get(requestType)) {
                callback.uploadFinished(requestType, null, false, "", reqObject);
            }
        }
    }
}