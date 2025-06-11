import java.sql.Timestamp;

// Pastikan Anda sudah punya kelas DatabaseUtil.java dengan konfigurasi koneksi yang benar
// Pastikan Anda sudah punya kelas User.java

// --- Measurement.java (Kelas Model) ---
// Kelas ini harus ada terpisah di file Measurement.java
// Isinya sama dengan yang sudah kita sepakati sebelumnya.
public class Measurement {
    private int measurementId;
    private int participantId;
    private int criterionId;
    private Timestamp measurementDatetime;
    private String jobPosition;
    private String severityLevel;
    private int severityScore;
    private String measurementLocation;
    private double[] measuredScores;

    // Constructor untuk data yang akan dikirim (tanpa ID dari DB)
    public Measurement(int participantId, int criterionId, Timestamp measurementDatetime,
                       String jobPosition, String severityLevel, int severityScore,
                       String measurementLocation, double[] measuredScores) {
        this.participantId = participantId;
        this.criterionId = criterionId;
        this.measurementDatetime = measurementDatetime;
        this.jobPosition = jobPosition;
        this.severityLevel = severityLevel;
        this.severityScore = severityScore;
        this.measurementLocation = measurementLocation;
        this.measuredScores = measuredScores;
    }

    // Constructor lengkap (misal jika membaca dari DB)
    public Measurement(int measurementId, int participantId, int criterionId, Timestamp measurementDatetime,
                       String jobPosition, String severityLevel, int severityScore,
                       String measurementLocation, double[] measuredScores) {
        this(participantId, criterionId, measurementDatetime, jobPosition, severityLevel, severityScore, measurementLocation, measuredScores);
        this.measurementId = measurementId;
    }

    // Getters
    public int getMeasurementId() { return measurementId; }
    public int getParticipantId() { return participantId; }
    public int getCriterionId() { return criterionId; }
    public Timestamp getMeasurementDatetime() { return measurementDatetime; }
    public String getJobPosition() { return jobPosition; }
    public String getSeverityLevel() { return severityLevel; }
    public int getSeverityScore() { return severityScore; }
    public String getMeasurementLocation() { return measurementLocation; }
    public double[] getMeasuredScores() { return measuredScores; }
}