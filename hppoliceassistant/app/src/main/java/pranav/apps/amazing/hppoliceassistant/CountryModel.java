package pranav.apps.amazing.hppoliceassistant;

/**
 * Created by Pranav Gupta
 */
public class CountryModel {

    private String name;
    private String isocode;
    private String place;
    private String naka;
    private String d;


    CountryModel(String name, String isocode,String place,String naka,String d){
        this.name=name;
        this.isocode=isocode;
        this.place =place;
        this.naka = naka;
        this.d = d;
    }

    public String getName() {
        return name;
    }

    public String getisoCode() {
        return isocode;
    }

    public String getPlace() {
        return place;
    }

    public String getNaka() {
        return naka;
    }

    public String getD() {
        return d;
    }
}
