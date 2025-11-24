package mentcare.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Consultation {
    private String consultationId;
    private String patientId;
    private LocalDateTime dateTime;
    private List<String> staffIds;
    private String subjectiveImpressions;
    private List<String> diagnoses;
    private List<Prescription> prescriptions;
    private List<String> referrals;
    private boolean recordUpdated;
    
    public Consultation(String consultationId, String patientId, LocalDateTime dateTime) {
        this.consultationId = consultationId;
        this.patientId = patientId;
        this.dateTime = dateTime;
        this.staffIds = new ArrayList<>();
        this.diagnoses = new ArrayList<>();
        this.prescriptions = new ArrayList<>();
        this.referrals = new ArrayList<>();
        this.recordUpdated = false;
    }
    
    // Getters and setters
    public String getConsultationId() { return consultationId; }
    public String getPatientId() { return patientId; }
    public LocalDateTime getDateTime() { return dateTime; }
    public List<String> getStaffIds() { return staffIds; }
    public String getSubjectiveImpressions() { return subjectiveImpressions; }
    public void setSubjectiveImpressions(String subjectiveImpressions) { 
        this.subjectiveImpressions = subjectiveImpressions; 
    }
    public List<String> getDiagnoses() { return diagnoses; }
    public List<Prescription> getPrescriptions() { return prescriptions; }
    public List<String> getReferrals() { return referrals; }
    public boolean isRecordUpdated() { return recordUpdated; }
    public void setRecordUpdated(boolean recordUpdated) { this.recordUpdated = recordUpdated; }
    
    public void addStaffMember(String staffId) {
        if (!staffIds.contains(staffId)) {
            staffIds.add(staffId);
        }
    }
    
    public void addDiagnosis(String diagnosis) {
        diagnoses.add(diagnosis);
    }
    
    public void addPrescription(Prescription prescription) {
        prescriptions.add(prescription);
    }
    
    public void addReferral(String referral) {
        referrals.add(referral);
    }
}