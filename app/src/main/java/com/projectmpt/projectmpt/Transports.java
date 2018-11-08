package com.projectmpt.projectmpt;

import com.google.firebase.database.Exclude;

public class Transports {

        @Exclude
        public String key;
        public String type;
        public String needkey;
        public String providelocationdetails;
        public String provideowner;
        public double providelatitude;
        public double providelongitude;
        public long providetimefrom;
        public long providetimeto;
        public String transportowner;
        public String heading;
        public String description;
        public String locationdetails;
        public String owner;
        public double latitude;
        public double longitude;
        public long timefrom;
        public long timeto;
        public String distanceto;



        //required empty constructor
        public Transports() {
        }

        public Transports(String needkey, String type, String providelocationdetails, String provideowner, double providelatitude, double providelongitude, long providetimefrom,
                          long providetimeto, String transportowner,  String heading, String description, String locationdetails, String owner, double latitude, double longitude, long timefrom, long timeto,
                          String distanceto) {

            this.needkey = needkey;
            this.type = type;
            this.providelocationdetails = providelocationdetails;
            this.provideowner = provideowner;
            this.providelatitude = providelatitude;
            this.providelongitude = providelongitude;
            this.providetimefrom = providetimefrom;
            this.providetimeto = providetimeto;
            this.transportowner = transportowner;
            this.heading = heading;
            this.description = description;
            this.locationdetails = locationdetails;
            this.owner = owner;
            this.latitude = latitude;
            this.longitude = longitude;
            this.timefrom = timefrom;
            this.timeto = timeto;
            this.distanceto = distanceto;


        }

        public String getKey() {return key;}

        public void setKey(String key) {
            this.key= key;
        }

        public String getType() {return type;}

        public void setType(String type) {
        this.type= type;
    }

        public String getNeedkey() {return needkey;}

        public void setNeedkey(String needkey) {
        this.needkey= needkey;
    }

        public String getProvidelocationdetails() {
        return providelocationdetails;
    }

        public void setProvidelocationdetails(String providelocationdetails) {
            this.providelocationdetails = providelocationdetails;
        }

        public String getProvideowner() {
            return provideowner;
        }

        public void setProvideowner(String provideowner) {
            this.provideowner = provideowner;
        }

        public double getProvidelongitude() {
            return providelongitude;
        }

        public void setProvidelongitude(double providelongitude) {
            this.providelongitude = providelongitude;
        }

        public double getProvidelatitude() {
            return providelatitude;
        }

        public void setProvidelatitude(double providelatitude) {
            this.providelatitude = providelatitude;
        }

        public long getProvidetimefrom() {
            return providetimefrom;
        }

        public void setProvidetimefrom(long providetimefrom) {
            this.providetimefrom = providetimefrom;
        }

        public long getProvidetimeto() {
            return providetimeto;
        }

        public void setProvidetimeto(long providetimeto) {
            this.providetimeto = providetimeto;
        }

        public String getTransportowner() {
        return transportowner;
    }

        public void setTransportowner(String transportowner) {
        this.transportowner = transportowner;
    }

        public String getHeading() {
            return heading;
        }

        public void setHeading(String heading) {
            this.heading = heading;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLocationdetails() {
            return locationdetails;
        }

        public void setLocationdetails(String locationdetails) {
            this.locationdetails = locationdetails;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public long getTimefrom() {
            return timefrom;
        }

        public void setTimefrom(long timefrom) {
            this.timefrom = timefrom;
        }

        public long getTimeto() {
            return timeto;
        }

        public void setTimeto(long timeto) {
            this.timeto = timeto;
        }

        public String getDistanceto() {
            return distanceto;
        }

        public void setDistanceto(String distanceto) {
            this.distanceto = distanceto;
        }


        @Override
        public String toString() {

            return getDescription();

        }

    }
