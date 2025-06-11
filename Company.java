// Company.java

// package com.pvt.system; // Sesuaikan dengan paket Anda

import java.sql.Date; // Untuk date_registered

public class Company {
    private int companyId;
    private String name;
    private String address;
    private Date dateRegistered; // java.sql.Date
    private String contactPerson;
    private String mobilePhone;
    private String emailAddress;

    // Constructor lengkap
    public Company(int companyId, String name, String address, Date dateRegistered, String contactPerson, String mobilePhone, String emailAddress) {
        this.companyId = companyId;
        this.name = name;
        this.address = address;
        this.dateRegistered = dateRegistered;
        this.contactPerson = contactPerson;
        this.mobilePhone = mobilePhone;
        this.emailAddress = emailAddress;
    }

    // Constructor untuk data baru (tanpa ID)
    public Company(String name, String address, Date dateRegistered, String contactPerson, String mobilePhone, String emailAddress) {
        this.name = name;
        this.address = address;
        this.dateRegistered = dateRegistered;
        this.contactPerson = contactPerson;
        this.mobilePhone = mobilePhone;
        this.emailAddress = emailAddress;
    }

    // Getters
    public int getCompanyId() { return companyId; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public Date getDateRegistered() { return dateRegistered; }
    public String getContactPerson() { return contactPerson; }
    public String getMobilePhone() { return mobilePhone; }
    public String getEmailAddress() { return emailAddress; }

    // Setters (penting untuk edit)
    public void setCompanyId(int companyId) { this.companyId = companyId; } // Mungkin tidak akan dipakai untuk edit, tapi berguna
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setDateRegistered(Date dateRegistered) { this.dateRegistered = dateRegistered; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    public void setMobilePhone(String mobilePhone) { this.mobilePhone = mobilePhone; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }
}