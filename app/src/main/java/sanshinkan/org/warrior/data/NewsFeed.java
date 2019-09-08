package sanshinkan.org.warrior.data;

import java.io.Serializable;

/**
 * Created by apoorvarora on 11/02/19.
 */

public class NewsFeed implements Serializable {
    private int eventId;
    private String title;
    private String coverImage;
    private String coverImageFallback;
    private int daysToStart;
    private String eventPeriod;
    private boolean eventEnded;
    private String locationCountryId;
    private String locationCountryReadable;
    private String locationCity;
    private double locationLat;
    private double locationLon;
    private int categoryGroupId;

    public NewsFeed() {
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getCoverImageFallback() {
        return coverImageFallback;
    }

    public void setCoverImageFallback(String coverImageFallback) {
        this.coverImageFallback = coverImageFallback;
    }

    public int getDaysToStart() {
        return daysToStart;
    }

    public void setDaysToStart(int daysToStart) {
        this.daysToStart = daysToStart;
    }

    public String getEventPeriod() {
        return eventPeriod;
    }

    public void setEventPeriod(String eventPeriod) {
        this.eventPeriod = eventPeriod;
    }

    public boolean getEventEnded() {
        return eventEnded;
    }

    public void setEventEnded(boolean eventEnded) {
        this.eventEnded = eventEnded;
    }

    public String getLocationCountryId() {
        return locationCountryId;
    }

    public void setLocationCountryId(String locationCountryId) {
        this.locationCountryId = locationCountryId;
    }

    public String getLocationCountryReadable() {
        return locationCountryReadable;
    }

    public void setLocationCountryReadable(String locationCountryReadable) {
        this.locationCountryReadable = locationCountryReadable;
    }

    public String getLocationCity() {
        return locationCity;
    }

    public void setLocationCity(String locationCity) {
        this.locationCity = locationCity;
    }

    public double getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(double locationLat) {
        this.locationLat = locationLat;
    }

    public double getLocationLon() {
        return locationLon;
    }

    public void setLocationLon(double locationLon) {
        this.locationLon = locationLon;
    }

    public int getCategoryGroupId() {
        return categoryGroupId;
    }

    public void setCategoryGroupId(int categoryGroupId) {
        this.categoryGroupId = categoryGroupId;
    }
}
