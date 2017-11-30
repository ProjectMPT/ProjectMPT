package com.projectmpt.projectmpt;

public class Needs {

        public String description;
        public String locationdetails;
        public String owner;
        public double latitude;
        public double longitude;
        public long timefrom;
        public long timeto;


        //required empty constructor
        public Needs() {
        }

        public Needs(String description, String locationdetails, String owner, double latitude, double longitude, long timefrom, long timeto) {
                this.description = description;
                this.locationdetails = locationdetails;
                this.owner = owner;
                this.latitude = latitude;
                this.longitude = longitude;
                this.timefrom = timefrom;
                this.timeto = timeto;
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

}