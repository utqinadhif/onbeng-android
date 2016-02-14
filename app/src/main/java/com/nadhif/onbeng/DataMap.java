package com.nadhif.onbeng;

/**
 * Created by nadhif on 08/12/2015.
 */
public class DataMap {
    String id, id_marker, name, company, contact, email, location, lat, lng;

    public DataMap(String id, String id_marker, String name, String company, String contact, String email, String location, String lat, String lng) {
        this.id = id;
        this.id_marker = id_marker;
        this.name = name;
        this.company = company;
        this.contact = contact;
        this.email = email;
        this.location = location;
        this.lat = lat;
        this.lng = lng;
    }

    public String getId() {
        return id;
    }

    public String getId_marker() {
        return id_marker;
    }

    public String getName() {
        return name;
    }

    public String getCompany() {
        return company;
    }

    public String getContact() {
        return contact;
    }

    public String getEmail() {
        return email;
    }

    public String getLocation() {
        return location;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }
}
