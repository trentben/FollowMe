package com.teamawesome.followme.util;

import java.io.Serializable;

/**
 * Created by Trent Bennett on 4/23/2015.
 */
public class Friend implements Serializable{
    public String username;
    public double longitude;
    public double latitude;

    public Friend(String username){
        this.username = username;
    }
    public Friend(String username, double lat, double longi){
        this.username = username;
        latitude = lat;
        longitude = longi;
    }

}
