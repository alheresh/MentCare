package mentcare.dao;

import mentcare.models.Patient;
import mentcare.utils.CSVUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {
    private static final String PATIENTS_CSV = "data/patients.csv";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    
    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        List<String[]> rows = CSVUtils.readCSV(PATIENTS_CSV);
        
        // Skip header row if exists
        int startIndex = 0;
        if (!rows.isEmpty() && rows.get(0)[0].equals("patientId")) {
            startIndex = 1;
        }
        
        for (int i = startIndex; i < rows.size(); i++) {
            String[] row = rows.get(i);
            if (row.length >= 6) {
                try {
                    // Debug: Print the row to see what's being read
                    System.out.println("Reading patient row: " + String.join(",", row));
                    
                    String patientId = row[0];
                    String nationalHealthNumber = row[1];
                    String name = row[2];
                    
                    // Combine address fields (index 3 and 4) since they contain city names
                    String address = row[3];
                    if (row.length > 4 && !row[4].isEmpty()) {
                        address += ", " + row[4]; // Add city to address
                    }
                    
                    // Date of birth is now at index 5
                    String dobStr = row[5];
                    String contactDetails = row[6];
                    
                    // Parse date of birth
                    LocalDate dateOfBirth = LocalDate.parse(dobStr, DATE_FORMATTER);
                    
                    Patient patient = new Patient(patientId, nationalHealthNumber, name, address, dateOfBirth, contactDetails);
                    
                    // Set additional fields if available
                    if (row.length > 7 && !row[7].isEmpty()) {
                        try {
                            patient.setRiskAssessment(Patient.RiskLevel.valueOf(row[7]));
                        } catch (IllegalArgumentException e) {
                            System.err.println("Invalid risk level: " + row[7] + ". Using LOW as default.");
                            patient.setRiskAssessment(Patient.RiskLevel.LOW);
                        }
                    }
                    
                    if (row.length > 8 && !row[8].isEmpty()) {
                        patient.setSectioned(Boolean.parseBoolean(row[8]));
                    }
                    
                    if (row.length > 9 && !row[9].isEmpty()) {
                        patient.setSectionedDate(LocalDate.parse(row[9], DATE_FORMATTER));
                    }
                    
                    if (row.length > 10 && !row[10].isEmpty()) {
                        patient.setReviewDate(LocalDate.parse(row[10], DATE_FORMATTER));
                    }
                    
                    patients.add(patient);
                    System.out.println("Successfully loaded patient: " + name);
                    
                } catch (Exception e) {
                    System.err.println("Error parsing patient row: " + String.join(",", row));
                    System.err.println("Error message: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        
        System.out.println("Total patients loaded: " + patients.size());
        return patients;
    }
    
    public void savePatient(Patient patient) {
        List<Patient> patients = getAllPatients();
        
        // Remove existing patient if present
        patients.removeIf(p -> p.getPatientId().equals(patient.getPatientId()));
        patients.add(patient);
        
        saveAllPatients(patients);
    }
    
    private void saveAllPatients(List<Patient> patients) {
        List<String[]> rows = new ArrayList<>();
        
        // Add header - updated to match the actual CSV structure
        rows.add(new String[]{
            "patientId", "nhNumber", "name", "address", "city", "dob", "contact", 
            "risk", "sectioned", "sectionedDate", "reviewDate"
        });
        
        for (Patient patient : patients) {
            // Split address into street and city for CSV storage
            String[] addressParts = patient.getAddress().split(", ", 2);
            String streetAddress = addressParts[0];
            String city = addressParts.length > 1 ? addressParts[1] : "";
            
            String[] row = {
                patient.getPatientId(),
                patient.getNationalHealthNumber(),
                patient.getName(),
                streetAddress,
                city,
                patient.getDateOfBirth().format(DATE_FORMATTER),
                patient.getContactDetails(),
                patient.getRiskAssessment().name(),
                String.valueOf(patient.isSectioned()),
                patient.getSectionedDate() != null ? patient.getSectionedDate().format(DATE_FORMATTER) : "",
                patient.getReviewDate() != null ? patient.getReviewDate().format(DATE_FORMATTER) : ""
            };
            rows.add(row);
        }
        
        CSVUtils.writeCSV(PATIENTS_CSV, rows);
    }
    
    public Patient findPatientById(String patientId) {
        return getAllPatients().stream()
                .filter(p -> p.getPatientId().equals(patientId))
                .findFirst()
                .orElse(null);
    }
}