package mentcare.models;

import java.time.LocalDate;

public class Prescription {
    private String prescriptionId;
    private String patientId;
    private String drugName;
    private String dosage;
    private String frequency;
    private LocalDate startDate;
    private LocalDate endDate;
    private String prescriberId;
    private boolean isRepeat;
    private String comments;
    
    public Prescription(String prescriptionId, String patientId, String drugName, 
                       String dosage, String frequency, LocalDate startDate, 
                       String prescriberId) {
        this.prescriptionId = prescriptionId;
        this.patientId = patientId;
        this.drugName = drugName;
        this.dosage = dosage;
        this.frequency = frequency;
        this.startDate = startDate;
        this.prescriberId = prescriberId;
        this.isRepeat = false;
    }
    
    // Getters and setters
    public String getPrescriptionId() { return prescriptionId; }
    public String getPatientId() { return patientId; }
    public String getDrugName() { return drugName; }
    public String getDosage() { return dosage; }
    public String getFrequency() { return frequency; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getPrescriberId() { return prescriberId; }
    public boolean isRepeat() { return isRepeat; }
    public void setRepeat(boolean repeat) { isRepeat = repeat; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}