import java.sql.Timestamp;

public class Participant {
    private int participantId;
    private int userId;
    private int companyId;
    private String name;
    private String department;
    private String jobPosition;
    private String emailAddress;
    private String mobilePhone;
    private Timestamp dateOfLastMeasurement; // Perhatikan tipe data: Timestamp untuk tanggal dan waktu
    private Integer lastFatiqueLevel; // Integer bisa null, int tidak

    // Constructor lengkap sesuai semua kolom di database
    public Participant(int participantId, int userId, int companyId, String name, String department, String jobPosition,
                       String emailAddress, String mobilePhone, Timestamp dateOfLastMeasurement, Integer lastFatiqueLevel) {
        this.participantId = participantId;
        this.userId = userId;
        this.companyId = companyId;
        this.name = name;
        this.department = department;
        this.jobPosition = jobPosition;
        this.emailAddress = emailAddress;
        this.mobilePhone = mobilePhone;
        this.dateOfLastMeasurement = dateOfLastMeasurement;
        this.lastFatiqueLevel = lastFatiqueLevel;
    }

    // Constructor untuk data baru (tanpa ID, atau dengan subset data jika diperlukan)
    public Participant(int userId, int companyId, String name, String department, String jobPosition,
                       String emailAddress, String mobilePhone) {
        this.userId = userId;
        this.companyId = companyId;
        this.name = name;
        this.department = department;
        this.jobPosition = jobPosition;
        this.emailAddress = emailAddress;
        this.mobilePhone = mobilePhone;
        this.dateOfLastMeasurement = null;
        this.lastFatiqueLevel = null;
    }

    // Getters
    public int getParticipantId() { return participantId; }
    public int getUserId() { return userId; }
    public int getCompanyId() { return companyId; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public String getJobPosition() { return jobPosition; }
    public String getEmailAddress() { return emailAddress; }
    public String getMobilePhone() { return mobilePhone; }
    public Timestamp getDateOfLastMeasurement() { return dateOfLastMeasurement; }
    public Integer getLastFatiqueLevel() { return lastFatiqueLevel; }
    

    // Setters (penting untuk operasi edit)
    public void setParticipantId(int participantId) { this.participantId = participantId; } // Hanya jika ID perlu diatur setelah insert
    public void setLastFatiqueLevel(Integer lastFatiqueLevel) { this.lastFatiqueLevel = lastFatiqueLevel; }
    public void setName(String name) { this.name = name; }
    public void setDepartment(String department) { this.department = department; }
    public void setJobPosition(String jobPosition) { this.jobPosition = jobPosition; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }
    public void setMobilePhone(String mobilePhone) { this.mobilePhone = mobilePhone; }
    public void setDateOfLastMeasurement(Timestamp dateOfLastMeasurement) { this.dateOfLastMeasurement = dateOfLastMeasurement; }

}