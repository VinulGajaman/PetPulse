package lk.javainstitute.petpulse_v2.model;

import com.google.firebase.firestore.PropertyName;

public class Appointment {
    private String vetMobile;

    private String petOwnerMobile;

    private String currentDate;

    private String ImageURL;

    private String currentTime;
    private int appointmentNumber;

    private String status;

    public Appointment() {

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getVetMobile() {
        return vetMobile;
    }

    public void setVetMobile(String vetMobile) {
        this.vetMobile = vetMobile;
    }

    public String getPetOwnerMobile() {
        return petOwnerMobile;
    }

    public void setPetOwnerMobile(String petOwnerMobile) {
        this.petOwnerMobile = petOwnerMobile;
    }

    public int getAppointmentNumber() {
        return appointmentNumber;
    }

    public void setAppointmentNumber(int appointmentNumber) {
        this.appointmentNumber = appointmentNumber;
    }
}