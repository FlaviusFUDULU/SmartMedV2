package users;

import java.io.Serializable;
import java.util.Vector;

/**
 * Created by ffudulu on 06-Apr-17.
 */
public class UserPersonalData implements Serializable {
    private String uId;
    private String firstName;
    private String lastName;
    private String email;
    private String cnp;
    private String id;
    private String age;
    private String photoUrl;
    private String sex;
    private String address;
    private String phoneNumber;
    private String assuranceCode;
    private String room;

    private Vector<String> analisys;
    private String dateOfAddmitance;
    private String diagnostic;
    private Vector<String> treatment;

    public UserPersonalData(String uId, String firstName, String lastName, String email, String cnp,
                            String id, String age, String photoUrl, String address,
                            String phoneNumber, String assuranceCode, String dateOfAddmitance,
                            String sex, String room) {
        this.uId = uId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.cnp = cnp;
        this.id = id;
        this.age = age;
        this.photoUrl = photoUrl;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.assuranceCode = assuranceCode;
        this.dateOfAddmitance = dateOfAddmitance;
        this.sex = sex;
        this.room = room;
    }

    public UserPersonalData(String uId, String firstName, String lastName, String email, String cnp,
                            String id, String age, String photoUrl, String address,
                            String phoneNumber, String assuranceCode, String dateOfAddmitance,
                            String diagnostic, Vector<String> treatment, String sex, String room,
                            Vector<String> analisys) {
        this.uId = uId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.cnp = cnp;
        this.id = id;
        this.age = age;
        this.photoUrl = photoUrl;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.assuranceCode = assuranceCode;
        this.dateOfAddmitance = dateOfAddmitance;
        this.diagnostic = diagnostic;
        this.treatment = treatment;
        this.sex = sex;
        this.room = room;
        this.analisys = analisys;
    }


    public UserPersonalData(String uId, String email) {
        this.uId = uId;
        this.email = email;
    }


    public UserPersonalData() {

    }

    public String getRoom() {
        return room;
    }

    public Vector<String> getAnalisys() {
        return analisys;
    }

    public String getSex() {
        return sex;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAssuranceCode() {
        return assuranceCode;
    }

    public String getDateOfAddmitance() {
        return dateOfAddmitance;
    }

    public String getDiagnostic() {
        return diagnostic;
    }

    public Vector<String> getTreatment() {
        return treatment;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getuId() {
        return uId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getCnp() {
        return cnp;
    }

    public String getId() {
        return id;
    }

    public String getAge() {
        return age;
    }
}
