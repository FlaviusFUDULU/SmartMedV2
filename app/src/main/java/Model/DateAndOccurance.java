package Model;

import java.io.Serializable;

/**
 * Created by ffudulu on 02-Jun-17.
 */

public class DateAndOccurance implements Serializable{

    private String date;
    private int occurance;
    private int duration;
    private float medium;

    public float getMedium() {
        return medium;
    }

    public void setMedium(float medium) {
        this.medium = medium;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }



    public DateAndOccurance(String date, int occurance, int duration, float medium) {
        this.date = date;
        this.occurance = occurance;
        this.duration = duration;
        this.medium = medium;
    }

    public DateAndOccurance(){}

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getOccurance() {
        return occurance;
    }

    public void setOccurance(int occurance) {
        this.occurance = occurance;
    }
}
