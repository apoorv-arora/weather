package sanshinkan.org.warrior.utils;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sanshinkan.org.warrior.data.NewsFeed;

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

        int codeResponse;

        if (responseObject.has("code") && responseObject.get("code") instanceof Integer) {
            codeResponse = responseObject.getInt("code");

            response[1] = (codeResponse == 0);
        }

        if (responseObject.has("errorMessage") && responseObject.get("errorMessage") != null)
            response[2] = String.valueOf(responseObject.get("errorMessage"));

        switch (requestType) {
        }
        return response;
    }

    public static List<NewsFeed> parse_FeedResponse(JSONArray jsonResponse) throws JSONException {
        if (jsonResponse == null)
            return null;

        ArrayList<NewsFeed> feedItems = new ArrayList<>();
        for (int  i=0; i<jsonResponse.length(); i++) {
            Object feedJson = jsonResponse.get(i);
            if (feedJson instanceof JSONObject)
                feedItems.add(parse_NewsFeed((JSONObject) feedJson));
        }

        return feedItems;
    }

    public static NewsFeed parse_NewsFeed (JSONObject jsonResponse) throws  JSONException {
        if (jsonResponse == null)
            return null;
        NewsFeed feed = new NewsFeed();

        if(jsonResponse.has("id") && jsonResponse.get("id") instanceof Integer) {
            feed.setEventId(jsonResponse.getInt("id"));
        }

        if (jsonResponse.has("title"))
            feed.setTitle(String.valueOf(jsonResponse.get("title")));

        if (jsonResponse.has("cover_image"))
            feed.setCoverImage(String.valueOf(jsonResponse.get("cover_image")));

        if (jsonResponse.has("cover_image_fallback"))
            feed.setCoverImageFallback(String.valueOf(jsonResponse.get("cover_image_fallback")));

        if(jsonResponse.has("days_to_start") && jsonResponse.get("days_to_start") instanceof Integer) {
            feed.setDaysToStart(jsonResponse.getInt("days_to_start"));
        }

        if (jsonResponse.has("eventPeriod"))
            feed.setEventPeriod(String.valueOf(jsonResponse.get("eventPeriod")));

        if (jsonResponse.has("eventEnded") && jsonResponse.get("eventEnded") instanceof Boolean)
            feed.setEventEnded(jsonResponse.getBoolean("eventEnded"));

        if (jsonResponse.has("location_country"))
            feed.setLocationCountryId(String.valueOf(jsonResponse.get("location_country")));

        if (jsonResponse.has("location_country_human"))
            feed.setLocationCountryReadable(String.valueOf(jsonResponse.get("location_country_human")));

        if (jsonResponse.has("location_city"))
            feed.setLocationCity(String.valueOf(jsonResponse.get("location_city")));

        if (jsonResponse.has("location_lat")) {
            String lat = String.valueOf(jsonResponse.get("location_city"));
            try {
                feed.setLocationLat(Double.parseDouble(lat));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (jsonResponse.has("location_long")) {
            String lon = String.valueOf(jsonResponse.get("location_long"));
            try {
                feed.setLocationLon(Double.parseDouble(lon));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (jsonResponse.has("category_group_id") && jsonResponse.get("category_group_id") instanceof Integer)
            feed.setCategoryGroupId(jsonResponse.getInt("category_group_id"));

        return feed;
    }

}

