package pranav.apps.amazing.hppoliceassistant;

/**
 * Created by Administrator on 9/3/2015.
 */
public class Friend {

    private String vehicle_num;
    private String phone_num;
    private String place_num;
    private String naka_name;
    private String descrip;
    private String image;

    public Friend(String vehicle_num, String phone_num, String place_num,String naka_name,String descrip,String image) {
        this.vehicle_num = vehicle_num;
        this.phone_num = phone_num;
        this.place_num = place_num;
        this.naka_name = naka_name;
        this.descrip = descrip;
        this.image =image;
    }
    public String getImage(){
        return image;
    }
    public String getVehicle_num() {
        return vehicle_num;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public String getPlace_num() {
        return place_num;
    }
    public String getNaka_name() {
        return naka_name;
    }
    public String getDescrip() {
        return descrip;
    }
}
