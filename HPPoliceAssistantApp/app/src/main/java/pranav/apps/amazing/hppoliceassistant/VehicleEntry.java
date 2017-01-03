package pranav.apps.amazing.hppoliceassistant;

/**
 * Created by Pranav Gupta on 12/30/2016.
 */

public class VehicleEntry {

    private String vehicle_number;
    private String phone_number;
    private String description;
    private String name_of_place;
    private String naka_name;
    private String date;
    private String time;
    private String officer_name;
    private String Image;
    private int status;
    public VehicleEntry(){

    }
    public VehicleEntry(String vehicle_number, String phone_number, String description, String name_of_place,
                        String naka_name, String date, String time, String officer_name,String Image) {
        this.vehicle_number = vehicle_number;
        this.phone_number = phone_number;
        this.description = description;
        this.name_of_place = name_of_place;
        this.naka_name = naka_name;
        this.date = date;
        this.time = time;
        this.officer_name = officer_name;
        this.Image=Image;
        this.status=0;
    }
    public VehicleEntry(String vehicle_number, String phone_number, String description, String name_of_place,
                        String naka_name, String date, String time, String officer_name,String Image,int status) {
        this.vehicle_number = vehicle_number;
        this.phone_number = phone_number;
        this.description = description;
        this.name_of_place = name_of_place;
        this.naka_name = naka_name;
        this.date = date;
        this.time = time;
        this.officer_name = officer_name;
        this.Image=Image;
        this.status=status;
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

    public String getNaka_name() {
        return naka_name;
    }

    public void setNaka_name(String naka_name) {
        this.naka_name = naka_name;
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
