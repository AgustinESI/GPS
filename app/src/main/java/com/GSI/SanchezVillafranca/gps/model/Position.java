package com.GSI.SanchezVillafranca.gps.model;

public class Position {
    public double lat;
    public double lng;

    public Position() {
    }

    public Position(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "MapView{" +
                "lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
