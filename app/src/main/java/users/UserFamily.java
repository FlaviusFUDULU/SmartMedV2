package users;

import java.io.Serializable;

/**
 * Created by ffudulu on 02-Jun-17.
 */

public class UserFamily implements Serializable{

    private String email;
    private String firstName;
    private String lastName;
    private String pacientUID;
    private String uId;
    private String activated;


    public UserFamily(String email, String firstName, String lastName, String pacientUID, String uId) {

        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.pacientUID = pacientUID;
        this.uId = uId;
    }

    public UserFamily() {
    }

    public void setActivated(String activated) {
        this.activated = activated;
    }

    public String getActivated() {
        return activated;
    }

    public String getuId() {
        return uId;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPacientUID() {
        return pacientUID;
    }
}
