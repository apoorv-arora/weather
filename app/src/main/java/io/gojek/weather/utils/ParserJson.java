package io.gojek.weather.utils;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.gojek.weather.data.Forecast;
import io.gojek.weather.data.Temperature;
import io.gojek.weather.utils.networking.UploadManager;

/**
 * Created by apoorvarora on 03/10/16.
 */
public class ParserJson {

    public static final Object[] parseData(int requestType, String responseJson) throws JSONException {
        final Object[] response = new Object[]{null, false, ""};

        if (responseJson == null || responseJson.isEmpty())
            return response;

        JSONObject responseObject = null;

        if (!TextUtils.isEmpty(responseJson)) {
            responseObject = new JSONObject(responseJson);
        }

        if (responseObject == null)
            return response;

        response[1] = true;
        if (requestType == UploadManager.FORECAST)
            response[0] = parse_FeedResponse(responseObject);

        return response;
    }

    public static Temperature parse_FeedResponse(JSONObject jsonResponse) throws JSONException {
        if (jsonResponse == null)
            return null;

        Temperature temperature = new Temperature();

        if (jsonResponse.has("current") && jsonResponse.get("current") instanceof JSONObject) {
            JSONObject currentJsonObject = jsonResponse.getJSONObject("current");
            if (currentJsonObject.has("temp_c") && currentJsonObject.get("temp_c") instanceof Double)
                temperature.setCurrentTemperatureCelcius(currentJsonObject.getDouble("temp_c"));

        }

        if (jsonResponse.has("forecast") && jsonResponse.get("forecast") instanceof JSONObject
                && jsonResponse.getJSONObject("forecast").has("forecastday")
                && jsonResponse.getJSONObject("forecast").get("forecastday") instanceof JSONArray) {
            List<Forecast> forecasts = new ArrayList<>();
            JSONArray forecastDay = jsonResponse.getJSONObject("forecast").getJSONArray("forecastday");
            for (int i=0; i<forecastDay.length(); i++) {
                Forecast forecast = new Forecast();
                if (forecastDay.get(i) instanceof JSONObject) {
                    JSONObject forecastJson = forecastDay.getJSONObject(i);
                    if (forecastJson.has("date_epoch") && forecastJson.get("date_epoch") instanceof Integer)
                        forecast.setTimestamp(forecastJson.getLong("date_epoch"));
                    if (forecastJson.has("day") && forecastJson.get("day") instanceof JSONObject
                        && forecastJson.getJSONObject("day").has("avgtemp_c")
                        && forecastJson.getJSONObject("day").get("avgtemp_c") instanceof Double)
                        forecast.setTemperatureCelcius(forecastJson.getJSONObject("day").getDouble("avgtemp_c"));
                    forecasts.add(forecast);
                }
            }
            temperature.setForecasts(forecasts);
        }

        return temperature;
    }

}

