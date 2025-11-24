package mentcare;

import mentcare.gui.LoginFrame;
import javax.swing.SwingUtilities;
//import javax.swing.UIManager;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Set system look and feel
        try {
           // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Create data directory and sample files if they don't exist
        initializeDataFiles();
        
        // Start the application
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
    
    private static void initializeDataFiles() {
        java.io.File dataDir = new java.io.File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        
        // Create sample users file
        createFileIfNotExists("data/users.csv", 
            new String[]{"userId", "username", "password", "role", "fullName", "contactInfo"});
        
        // Create patients file with corrected structure
        createFileIfNotExists("data/patients.csv", 
            new String[]{"patientId", "nhNumber", "name", "address", "city", "dob", "contact", "risk", "sectioned", "sectionedDate", "reviewDate"});
        
        // Create other data files
        createFileIfNotExists("data/consultations.csv",
            new String[]{"consultationId", "patientId", "dateTime", "staffIds", "impressions", "diagnoses", "prescriptions", "referrals", "updated"});
        
        createFileIfNotExists("data/prescriptions.csv",
            new String[]{"prescriptionId", "patientId", "drugName", "dosage", "frequency", "startDate", "endDate", "prescriberId", "isRepeat", "comments"});
        
        // Add sample data if files are empty or only have headers
        addSampleData();
    }
    
    private static void createFileIfNotExists(String filename, String[] header) {
        java.io.File file = new java.io.File(filename);
        if (!file.exists() || file.length() == 0) {
            List<String[]> initialData = new ArrayList<>();
            initialData.add(header);
            mentcare.utils.CSVUtils.writeCSV(filename, initialData);
            System.out.println("Created file: " + filename);
        }
    }
    
    private static void addSampleData() {
        // Check if users.csv only has header
        List<String[]> users = mentcare.utils.CSVUtils.readCSV("data/users.csv");
        if (users.size() <= 1) {
            System.out.println("Adding sample users to users.csv");
            List<String[]> sampleUsers = new ArrayList<>();
            
            // Add header first
            sampleUsers.add(new String[]{"userId", "username", "password", "role", "fullName", "contactInfo"});
            
            // Add sample users
            sampleUsers.add(new String[]{"USER001", "doctor1", "password123", "CLINICAL_STAFF", "Dr. John Smith", "john.smith@hospital.com"});
            sampleUsers.add(new String[]{"USER002", "admin1", "password123", "ADMINISTRATOR", "Admin User", "admin@hospital.com"});
            sampleUsers.add(new String[]{"USER003", "mha1", "password123", "MHA_ADMINISTRATOR", "MHA Manager", "mha@hospital.com"});
            sampleUsers.add(new String[]{"USER004", "sysadmin", "password123", "SYSTEM_ADMIN", "System Administrator", "sysadmin@hospital.com"});
            
            mentcare.utils.CSVUtils.writeCSV("data/users.csv", sampleUsers);
            System.out.println("Sample users added successfully");
        }
        
        // Check if patients.csv only has header
        List<String[]> patients = mentcare.utils.CSVUtils.readCSV("data/patients.csv");
        if (patients.size() <= 1) {
            System.out.println("Adding sample patients to patients.csv");
            List<String[]> samplePatients = new ArrayList<>();
            
            // Add header first
            samplePatients.add(new String[]{"patientId", "nhNumber", "name", "address", "city", "dob", "contact", "risk", "sectioned", "sectionedDate", "reviewDate"});
            
            // Add sample patients with corrected structure
            samplePatients.add(new String[]{"PAT001", "NH123456789", "John Doe", "123 Main St", "Edinburgh", "1980-05-15", "555-0123", "LOW", "false", "", ""});
            samplePatients.add(new String[]{"PAT002", "NH987654321", "Jane Smith", "456 Oak Ave", "Glasgow", "1975-08-22", "555-0456", "HIGH", "true", "2024-01-15", "2024-04-15"});
            samplePatients.add(new String[]{"PAT003", "NH456789123", "Robert Brown", "789 Pine Rd", "Aberdeen", "1990-12-10", "555-0789", "MEDIUM", "false", "", ""});
            
            mentcare.utils.CSVUtils.writeCSV("data/patients.csv", samplePatients);
            System.out.println("Sample patients added successfully");
        }
        
        // Add sample consultations if needed
        List<String[]> consultations = mentcare.utils.CSVUtils.readCSV("data/consultations.csv");
        if (consultations.size() <= 1) {
            System.out.println("Adding sample consultations to consultations.csv");
            List<String[]> sampleConsultations = new ArrayList<>();
            
            // Add header first
            sampleConsultations.add(new String[]{"consultationId", "patientId", "dateTime", "staffIds", "impressions", "diagnoses", "prescriptions", "referrals", "updated"});
            
            // Add sample consultations
            sampleConsultations.add(new String[]{"CONS001", "PAT001", "2024-01-10T10:30:00", "USER001", "Patient appears stable and responsive", "Anxiety;Depression", "", "Social Services", "true"});
            sampleConsultations.add(new String[]{"CONS002", "PAT002", "2024-01-12T14:15:00", "USER001", "Patient shows signs of improvement but requires monitoring", "Bipolar Disorder", "", "Psychiatric Ward", "true"});
            
            mentcare.utils.CSVUtils.writeCSV("data/consultations.csv", sampleConsultations);
            System.out.println("Sample consultations added successfully");
        }
        
        // Add sample prescriptions if needed
        List<String[]> prescriptions = mentcare.utils.CSVUtils.readCSV("data/prescriptions.csv");
        if (prescriptions.size() <= 1) {
            System.out.println("Adding sample prescriptions to prescriptions.csv");
            List<String[]> samplePrescriptions = new ArrayList<>();
            
            // Add header first
            samplePrescriptions.add(new String[]{"prescriptionId", "patientId", "drugName", "dosage", "frequency", "startDate", "endDate", "prescriberId", "isRepeat", "comments"});
            
            // Add sample prescriptions
            samplePrescriptions.add(new String[]{"PRES001", "PAT001", "Sertraline", "50mg", "Once daily", "2024-01-10", "2024-04-10", "USER001", "false", "Monitor for side effects"});
            samplePrescriptions.add(new String[]{"PRES002", "PAT002", "Lithium", "300mg", "Twice daily", "2024-01-12", "2024-04-12", "USER001", "true", "Regular blood tests required"});
            
            mentcare.utils.CSVUtils.writeCSV("data/prescriptions.csv", samplePrescriptions);
            System.out.println("Sample prescriptions added successfully");
        }
    }
}