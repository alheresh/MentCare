package mentcare.dao;

import mentcare.models.Consultation;
import mentcare.models.Prescription;
import mentcare.utils.CSVUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConsultationDAO {
    private static final String CONSULTATIONS_CSV = "data/consultations.csv";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    public List<Consultation> getAllConsultations() {
        List<Consultation> consultations = new ArrayList<>();
        List<String[]> rows = CSVUtils.readCSV(CONSULTATIONS_CSV);
        
        // Skip header row if exists
        int startIndex = 0;
        if (!rows.isEmpty() && rows.get(0)[0].equals("consultationId")) {
            startIndex = 1;
        }
        
        for (int i = startIndex; i < rows.size(); i++) {
            String[] row = rows.get(i);
            if (row.length >= 3) {
                try {
                    Consultation consultation = new Consultation(
                        row[0], // consultationId
                        row[1], // patientId
                        LocalDateTime.parse(row[2], DATE_FORMATTER) // dateTime
                    );
                    
                    // Staff IDs
                    if (row.length > 3 && !row[3].isEmpty()) {
                        consultation.getStaffIds().addAll(Arrays.asList(row[3].split(";")));
                    }
                    
                    // Subjective impressions
                    if (row.length > 4 && !row[4].isEmpty()) {
                        consultation.setSubjectiveImpressions(row[4]);
                    }
                    
                    // Diagnoses
                    if (row.length > 5 && !row[5].isEmpty()) {
                        consultation.getDiagnoses().addAll(Arrays.asList(row[5].split(";")));
                    }
                    
                    // Record updated status
                    if (row.length > 8 && !row[8].isEmpty()) {
                        consultation.setRecordUpdated(Boolean.parseBoolean(row[8]));
                    }
                    
                    consultations.add(consultation);
                } catch (Exception e) {
                    System.err.println("Error parsing consultation row: " + Arrays.toString(row));
                    e.printStackTrace();
                }
            }
        }
        
        return consultations;
    }
    
    public List<Consultation> getConsultationsByPatient(String patientId) {
        return getAllConsultations().stream()
                .filter(consultation -> consultation.getPatientId().equals(patientId))
                .collect(Collectors.toList());
    }
    
    public Consultation getConsultationById(String consultationId) {
        return getAllConsultations().stream()
                .filter(consultation -> consultation.getConsultationId().equals(consultationId))
                .findFirst()
                .orElse(null);
    }
    
    public void saveConsultation(Consultation consultation) {
        List<Consultation> consultations = getAllConsultations();
        
        // Remove existing consultation if present
        consultations.removeIf(c -> c.getConsultationId().equals(consultation.getConsultationId()));
        consultations.add(consultation);
        
        saveAllConsultations(consultations);
    }
    
    private void saveAllConsultations(List<Consultation> consultations) {
        List<String[]> rows = new ArrayList<>();
        
        // Add header
        rows.add(new String[]{
            "consultationId", "patientId", "dateTime", "staffIds", 
            "impressions", "diagnoses", "prescriptions", "referrals", "updated"
        });
        
        for (Consultation consultation : consultations) {
            String[] row = {
                consultation.getConsultationId(),
                consultation.getPatientId(),
                consultation.getDateTime().format(DATE_FORMATTER),
                String.join(";", consultation.getStaffIds()),
                consultation.getSubjectiveImpressions() != null ? consultation.getSubjectiveImpressions() : "",
                String.join(";", consultation.getDiagnoses()),
                "", // prescriptions - would need separate handling
                String.join(";", consultation.getReferrals()),
                String.valueOf(consultation.isRecordUpdated())
            };
            rows.add(row);
        }
        
        CSVUtils.writeCSV(CONSULTATIONS_CSV, rows);
    }
    
    public void deleteConsultation(String consultationId) {
        List<Consultation> consultations = getAllConsultations();
        consultations.removeIf(c -> c.getConsultationId().equals(consultationId));
        saveAllConsultations(consultations);
    }
}