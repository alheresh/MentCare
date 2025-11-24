package mentcare.dao;

import mentcare.models.Prescription;
import mentcare.utils.CSVUtils;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionDAO {
    private static final String PRESCRIPTIONS_CSV = "data/prescriptions.csv";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    
    public List<Prescription> getAllPrescriptions() {
        List<Prescription> prescriptions = new ArrayList<>();
        List<String[]> rows = CSVUtils.readCSV(PRESCRIPTIONS_CSV);
        
        // Skip header row if exists
        int startIndex = 0;
        if (!rows.isEmpty() && rows.get(0)[0].equals("prescriptionId")) {
            startIndex = 1;
        }
        
        for (int i = startIndex; i < rows.size(); i++) {
            String[] row = rows.get(i);
            if (row.length >= 7) {
                try {
                    Prescription prescription = new Prescription(
                        row[0], // prescriptionId
                        row[1], // patientId
                        row[2], // drugName
                        row[3], // dosage
                        row[4], // frequency
                        LocalDate.parse(row[5], DATE_FORMATTER), // startDate
                        row[7]  // prescriberId
                    );
                    
                    // End date
                    if (row.length > 6 && !row[6].isEmpty()) {
                        prescription.setEndDate(LocalDate.parse(row[6], DATE_FORMATTER));
                    }
                    
                    // Repeat status
                    if (row.length > 8 && !row[8].isEmpty()) {
                        prescription.setRepeat(Boolean.parseBoolean(row[8]));
                    }
                    
                    // Comments
                    if (row.length > 9 && !row[9].isEmpty()) {
                        prescription.setComments(row[9]);
                    }
                    
                    prescriptions.add(prescription);
                } catch (Exception e) {
                    System.err.println("Error parsing prescription row: " + String.join(",", row));
                    e.printStackTrace();
                }
            }
        }
        
        return prescriptions;
    }
    
    public Prescription getPrescriptionById(String prescriptionId) {
        return getAllPrescriptions().stream()
                .filter(p -> p.getPrescriptionId().equals(prescriptionId))
                .findFirst()
                .orElse(null);
    }
    
    public void savePrescription(Prescription prescription) {
        List<Prescription> prescriptions = getAllPrescriptions();
        
        // Remove existing prescription if present
        prescriptions.removeIf(p -> p.getPrescriptionId().equals(prescription.getPrescriptionId()));
        prescriptions.add(prescription);
        
        saveAllPrescriptions(prescriptions);
    }
    
    private void saveAllPrescriptions(List<Prescription> prescriptions) {
        List<String[]> rows = new ArrayList<>();
        
        // Add header
        rows.add(new String[]{
            "prescriptionId", "patientId", "drugName", "dosage", "frequency", 
            "startDate", "endDate", "prescriberId", "isRepeat", "comments"
        });
        
        for (Prescription prescription : prescriptions) {
            String[] row = {
                prescription.getPrescriptionId(),
                prescription.getPatientId(),
                prescription.getDrugName(),
                prescription.getDosage(),
                prescription.getFrequency(),
                prescription.getStartDate().format(DATE_FORMATTER),
                prescription.getEndDate() != null ? prescription.getEndDate().format(DATE_FORMATTER) : "",
                prescription.getPrescriberId(),
                String.valueOf(prescription.isRepeat()),
                prescription.getComments() != null ? prescription.getComments() : ""
            };
            rows.add(row);
        }
        
        CSVUtils.writeCSV(PRESCRIPTIONS_CSV, rows);
    }
    
    public void deletePrescription(String prescriptionId) {
        List<Prescription> prescriptions = getAllPrescriptions();
        prescriptions.removeIf(p -> p.getPrescriptionId().equals(prescriptionId));
        saveAllPrescriptions(prescriptions);
    }
    
    public List<Prescription> getPrescriptionsByPatient(String patientId) {
        return getAllPrescriptions().stream()
                .filter(p -> p.getPatientId().equals(patientId))
                .toList();
    }
}