package mentcare.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Patient {
    private String patientId;
    private String nationalHealthNumber;
    private String name;
    private String address;
    private LocalDate dateOfBirth;
    private String contactDetails;
    private String registeredPractice;
    private String nextOfKin;
    private RiskLevel riskAssessment;
    private List<String> conditions;
    private boolean isSectioned;
    private LocalDate sectionedDate;
    private LocalDate reviewDate;
    
    public enum RiskLevel {
        LOW, MEDIUM, HIGH, CRITICAL
    }
    
    public Patient(String patientId, String nationalHealthNumber, String name, 
                  String address, LocalDate dateOfBirth, String contactDetails) {
        this.patientId = patientId;
        this.nationalHealthNumber = nationalHealthNumber;
        this.name = name;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.contactDetails = contactDetails;
        this.conditions = new ArrayList<>();
        this.riskAssessment = RiskLevel.LOW;
        this.isSectioned = false;
    }
    
    // Getters and setters
    public String getPatientId() { return patientId; }
    public String getNationalHealthNumber() { return nationalHealthNumber; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getContactDetails() { return contactDetails; }
    public RiskLevel getRiskAssessment() { return riskAssessment; }
    public void setRiskAssessment(RiskLevel riskAssessment) { this.riskAssessment = riskAssessment; }
    public List<String> getConditions() { return conditions; }
    public boolean isSectioned() { return isSectioned; }
    public void setSectioned(boolean sectioned) { isSectioned = sectioned; }
    public LocalDate getSectionedDate() { return sectionedDate; }
    public void setSectionedDate(LocalDate sectionedDate) { this.sectionedDate = sectionedDate; }
    public LocalDate getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDate reviewDate) { this.reviewDate = reviewDate; }
    
    public void addCondition(String condition) {
        if (!conditions.contains(condition)) {
            conditions.add(condition);
        }
    }
    
    public void removeCondition(String condition) {
        conditions.remove(condition);
    }
    
    public int getAge() {
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }
}