package pranav.apps.amazing.hppoliceassistant;

/**
 * Created by Pranav Gupta on 12/30/2016.
 */

public class ChallanDetails {

    private String ChallanID;
    private String offences;
    private String violator_name;
    private String violator_number;
    private String owner_name;
    private String violator_address;
    private String vehicle_number;
    private String name_of_place;
    private String offences_section;
    private String challan_amount;
    private String license_number;
    private String other_remarks;
    private String police_officer_name;
    private String district;
    private String police_station;
    private String Image;
    private String date;
    private String time;
    private int status;
    private long epoch;

    public long getEpoch() {
        return epoch;
    }

    public void setEpoch(long epoch) {
        this.epoch = epoch;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getViolator_number() {
        return violator_number;
    }

    public void setViolator_number(String violator_number) {
        this.violator_number = violator_number;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getOffences() {
        return offences;
    }

    public void setOffences(String offences) {
        this.offences = offences;
    }

    public String getViolator_name() {
        return violator_name;
    }

    public void setViolator_name(String violator_name) {
        this.violator_name = violator_name;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getViolator_address() {
        return violator_address;
    }

    public void setViolator_address(String violator_address) {
        this.violator_address = violator_address;
    }

    public String getVehicle_number() {
        return vehicle_number;
    }

    public void setVehicle_number(String vehicle_number) {
        this.vehicle_number = vehicle_number;
    }

    public String getName_of_place() {
        return name_of_place;
    }

    public void setName_of_place(String name_of_place) {
        this.name_of_place = name_of_place;
    }

    public String getOffences_section() {
        return offences_section;
    }

    public void setOffences_section(String offences_section) {
        this.offences_section = offences_section;
    }

    public String getChallan_amount() {
        return challan_amount;
    }

    public void setChallan_amount(String challan_amount) {
        this.challan_amount = challan_amount;
    }

    public String getLicense_number() {
        return license_number;
    }

    public void setLicense_number(String license_number) {
        this.license_number = license_number;
    }

    public String getOther_remarks() {
        return other_remarks;
    }

    public void setOther_remarks(String other_remarks) {
        this.other_remarks = other_remarks;
    }

    public String getPolice_officer_name() {
        return police_officer_name;
    }

    public void setPolice_officer_name(String police_officer_name) {
        this.police_officer_name = police_officer_name;
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
    public ChallanDetails(){

    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    //class holding multiple contructors

    //constructor for adding challan details in Challan Fragment
    public ChallanDetails(String ChallanID,String violator_name, String offences, String owner_name,
                          String violator_address, String vehicle_number, String name_of_place,
                          String offences_section, String challan_amount, String license_number,
                          String police_officer_name, String district, String police_station,
                          String other_remarks, String Image, String violator_number, String date, String time) {
        this.ChallanID=ChallanID;
        this.violator_name = violator_name;
        this.offences = offences;
        this.owner_name = owner_name;
        this.violator_address = violator_address;
        this.vehicle_number = vehicle_number;
        this.name_of_place = name_of_place;
        this.offences_section = offences_section;
        this.challan_amount = challan_amount;
        this.license_number = license_number;
        this.police_officer_name = police_officer_name;
        this.district = district;
        this.police_station = police_station;
        this.other_remarks = other_remarks;
        this.Image=Image;
        this.violator_number=violator_number;
        this.date=date;
        this.time=time;
        this.status=1;
    }

    public String getChallanID() {
        return ChallanID;
    }

    public void setChallanID(String challanID) {
        ChallanID = challanID;
    }

    //constructor to show offline status
    public ChallanDetails(String ChallanID,String violator_name, String offences, String owner_name,
                          String violator_address, String vehicle_number, String name_of_place,
                          String offences_section, String challan_amount, String license_number,
                          String police_officer_name, String district, String police_station,
                          String other_remarks, String Image, String violator_number, String date, String time,int status) {

        this.ChallanID =ChallanID;

        this.violator_name = violator_name;
        this.offences = offences;
        this.owner_name = owner_name;
        this.violator_address = violator_address;
        this.vehicle_number = vehicle_number;
        this.name_of_place = name_of_place;
        this.offences_section = offences_section;
        this.challan_amount = challan_amount;
        this.license_number = license_number;
        this.police_officer_name = police_officer_name;
        this.district = district;
        this.police_station = police_station;
        this.other_remarks = other_remarks;
        this.Image=Image;
        this.violator_number=violator_number;
        this.date=date;
        this.time=time;
        this.status=status;
    }

}
