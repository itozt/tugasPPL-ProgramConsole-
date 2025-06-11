// Observer.java

// package com.pvt.system; // Sesuaikan dengan paket Anda

public class Observer {
    private int observerId;
    private int userId;
    private int companyId;
    private String name;
    private String address;
    private String mobilePhone;
    private String emailAddress;
    private String gender;   // Atribut baru
    private String position; // Atribut baru

    // Constructor yang diperbarui untuk mencakup atribut baru
    public Observer(int observerId, int userId, int companyId, String name, String address, String mobilePhone, String emailAddress, String gender, String position) {
        this.observerId = observerId;
        this.userId = userId;
        this.companyId = companyId;
        this.name = name;
        this.address = address;
        this.mobilePhone = mobilePhone;
        this.emailAddress = emailAddress;
        this.gender = gender;
        this.position = position;
    }

    // Getters
    public int getObserverId() { return observerId; }
    public int getUserId() { return userId; }
    public int getCompanyId() { return companyId; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getMobilePhone() { return mobilePhone; }
    public String getEmailAddress() { return emailAddress; }
    public String getGender() { return gender; }         // Getter untuk gender
    public String getPosition() { return position; }   // Getter untuk position

    // Setters (penting jika Anda ingin mengedit ini nanti)
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setMobilePhone(String mobilePhone) { this.mobilePhone = mobilePhone; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }
    public void setGender(String gender) { this.gender = gender; }     // Setter untuk gender
    public void setPosition(String position) { this.position = position; } // Setter untuk position
}