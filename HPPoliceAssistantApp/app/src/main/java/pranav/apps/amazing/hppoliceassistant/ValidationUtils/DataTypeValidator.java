package pranav.apps.amazing.hppoliceassistant;

/**
 * Created by Gopal on 30-01-2017.
 */

public class DataTypeValidator {

    /**
     * This method returns whether the vehicle number entered by user is of valid format one
     * As of now, it simply checks if the argument is an alphanumeric string
     * @param vehicleNumber whose format is to be checked
     * @return true if correct format else false
     */
    static public boolean validateVehicleNumberFormat(String vehicleNumber) {
        return vehicleNumber.matches("[A-Za-z0-9]+");
    }

    /**
     * This method returns whether the phone number entered by user is of valid format one
     * As of now, it simply check if the argument String contains only numbers and is of length 10
     * @param phoneNumber whose format is be checked
     * @return true if correct format else false
     */
    static public boolean validatePhoneNumberFormat(String phoneNumber) {
        if(!phoneNumber.contentEquals(""))
            return phoneNumber.matches("[0-9]+") && phoneNumber.length() == 10;
        else
            return true;
    }

    /**
     * This method returns whether the name of Person entered by user is of valid format one
     * It checks if the argument String contains only letters and spaces
     * @param nameOfPerson whose format is be checked
     * @return true if correct format else false
     */
    static public boolean validateNameOfPersonFormat(String nameOfPerson) {
        if(!nameOfPerson.contentEquals(""))
            return nameOfPerson.matches("[a-zA-Z ]+");
        else
            return true;
    }

    /**
     * This method returns whether the license number entered by user is of valid format one
     * As of now, it simply checks if the argument is an alphanumeric string
     * @param licenseNumber whose format is to be checked
     * @return true if correct format else false
     */
    static public boolean validateLicenseNumberFormat(String licenseNumber) {
        if(!licenseNumber.contentEquals(""))
            return licenseNumber.matches("[A-Za-z0-9]+");
        else
            return true;
    }

}
