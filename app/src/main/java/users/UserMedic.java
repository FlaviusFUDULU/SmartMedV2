package users;

import java.io.Serializable;

/**
 * Created by ffudulu on 22-Apr-17.
 */

public class UserMedic implements Serializable{

    private String uId;
    private String email;
    private String firstName;
    private String hospitalName;
    private String lastName;
    private String rank;
    private String sectionName;
    private String cnp;
    private String id;
    private String address;
    private String phoneNumber;
    private String dateOfBirth;
    private String photoUrl;

    public UserMedic(String uId, String email, String firstName, String hospitalName,
                     String lastName, String rank, String sectionName, String cnp, String id,
                     String address, String phoneNumber, String dateOfBirth, String photoUrl) {
        this.uId = uId;
        this.email = email;
        this.firstName = firstName;
        this.hospitalName = hospitalName;
        this.lastName = lastName;
        this.rank = rank;
        this.sectionName = sectionName;
        this.cnp = cnp;
        this.id = id;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.photoUrl = photoUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public UserMedic() {
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }


    public String getuId() {
        return uId;
    }


    public String getCnp() {
        return cnp;
    }

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getRank() {
        return rank;
    }

    public String getSectionName() {
        return sectionName;
    }
}
