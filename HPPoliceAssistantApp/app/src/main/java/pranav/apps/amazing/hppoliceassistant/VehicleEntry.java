package pranav.apps.amazing.hppoliceassistant;

/**
 * Created by Pranav Gupta on 12/30/2016.
 */

public class VehicleEntry {

    private String EntryID;

    public String getEntryID() {
        return EntryID;
    }

    public void setEntryID(String entryID) {
        EntryID = entryID;
    }

    private String vehicle_number;
    private String phone_number;
    private String description;
    private String name_of_place;
    private String district;
    private String police_station;
    private String police_post;
    private String date;
    private String time;
    private String officer_name;
    private String Image;
    private int status;
    private long epoch;

    public long getEpoch() {
        return epoch;
    }

    public void setEpoch(long epoch) {
        this.epoch = epoch;
    }

    public VehicleEntry(){

    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getPolice_station() {
        return police_station;
    }

    public void setPolice_station(String police_station) {
        this.police_station = police_station;
    }

    public String getPolice_post() {
        return police_post;
    }

    public void setPolice_post(String police_post) {
        this.police_post = police_post;
    }

    public VehicleEntry(String EntryID,String vehicle_number, String phone_number, String description, String name_of_place, String date, String time, String officer_name, String Image) {
        this.EntryID = EntryID;
        this.vehicle_number = vehicle_number;
        this.phone_number = phone_number;
        this.description = description;
        this.name_of_place = name_of_place;
        this.date = date;
        this.time = time;
        this.officer_name = officer_name;
        this.Image=Image;

        this.status=0;
    }
    public VehicleEntry(String EntryId,String vehicle_number, String phone_number, String description, String name_of_place, String date, String time, String officer_name,String Image,int status) {
        this.EntryID = EntryId;
        this.vehicle_number = vehicle_number;
        this.phone_number = phone_number;
        this.description = description;
        this.name_of_place = name_of_place;
        this.date = date;
        this.time = time;
        this.officer_name = officer_name;
        this.Image=Image;
        this.status=status;
    }
    public VehicleEntry(String EntryID,String vehicle_number, String phone_number, String description, String name_of_place, String date, String time, String officer_name,String Image,String district,String police_station
            ,String police_post,int status) {
        this.EntryID = EntryID;
        this.vehicle_number = vehicle_number;
        this.phone_number = phone_number;
        this.description = description;
        this.name_of_place = name_of_place;
        this.date = date;
        this.time = time;
        this.officer_name = officer_name;
        this.Image=Image;
        this.status=status;
        this.police_station=police_station;
        this.police_post= police_post;
        this.district=district;
    }

    public String getVehicle_number() {
        return vehicle_number;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setVehicle_number(String vehicle_number) {
        this.vehicle_number = vehicle_number;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName_of_place() {
        return name_of_place;
    }

    public void setName_of_place(String name_of_place) {
        this.name_of_place = name_of_place;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOfficer_name() {
        return officer_name;
    }

    public void setOfficer_name(String officer_name) {
        this.officer_name = officer_name;
    }
}
