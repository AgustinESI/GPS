package com.GSI.SanchezVillafranca.gps.model;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationManager;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class LocationThread extends Thread {
    private LocationManager locationManager;

    private ArrayList<GeoPoint> geoPoints;
    private boolean isRunning = true;

    public LocationThread(LocationManager locationManager, ArrayList<GeoPoint> geoPoints) {
        this.locationManager = locationManager;
        this.geoPoints = geoPoints;
    }


    @Override
    @SuppressLint("MissingPermission")
    public void run() {
        while (isRunning) {
            try {
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                GeoPoint gp = new GeoPoint(location.getLatitude(), location.getLongitude());
                geoPoints.add(gp);
                Thread.sleep(3000); // espera 30 segundos
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopThread() {
        isRunning = false;
    }

    public ArrayList<GeoPoint> getGeoPoints() {
        return geoPoints;
    }

}
