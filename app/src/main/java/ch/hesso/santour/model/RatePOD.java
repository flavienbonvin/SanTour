package ch.hesso.santour.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by flavien on 11/21/17.
 */

@IgnoreExtraProperties
public class RatePOD {
    private String podCatID;
    private int rate;

    public RatePOD() {
    }

    public RatePOD(String podCatID, int rate) {
        this.podCatID = podCatID;
        this.rate = rate;
    }

    public String getPodCatID() {
        return podCatID;
    }

    public void setPodCatID(String podCatID) {
        this.podCatID = podCatID;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "RatePOD{" +
                "podID='" + podCatID + '\'' +
                ", rate=" + rate +
                '}';
    }
}
