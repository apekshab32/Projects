package com.hari.autotasx;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Hari on 27-Feb-15.
 */
public class GeoFence {

    private float radius;
    private String name;
    private LatLng point;
    private String remmsg;

    //getters and setters
    public GeoFence() {
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public String getNameLoc() {
        return name;
    }

    public void setNameLoc(String name) {
        this.name = name;
    }

    public LatLng getPoint() {
        return point;
    }

    public void setPoint(LatLng point) {
        this.point = point;
    }

    public String getRemmsg() {
        return remmsg;
    }

    public void setRemmsg(String remmsg) {
        this.remmsg = remmsg;
    }

    @Override
    public String toString() {
        return "GeoFence{" +
                "radius=" + radius +
                ", name='" + name + '\'' +
                ", point=" + point +
                '}';
    }
}
