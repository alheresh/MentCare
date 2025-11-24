package mentcare.gui;

import mentcare.models.Prescription;
import mentcare.models.Patient;
import mentcare.models.User;
import mentcare.dao.PrescriptionDAO;
import mentcare.dao.PatientDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

public class PrescriptionDialog extends JDialog {
    private Prescription prescription;
    private User currentUser;
    private PrescriptionPanel parentPanel;
    private PrescriptionDAO prescriptionDAO;
    private PatientDAO patientDAO;
    
    private JComboBox<Patient> patientComboBox;
    private JTextField drugNameField;
    private JTextField dosageField;
    private JTextField frequencyField;
    private JTextField startDateField;
    private JTextField endDateField;
    private JCheckBox repeatCheckBox;
    private JTextArea commentsArea;
    
    public PrescriptionDialog(Prescription prescription, User user, PrescriptionPanel parentPanel) {
        super((Frame) null, 
              prescription == null ? "Add Prescription" : "Edit Prescription", 
              true);
        this.prescription = prescription;
        this.currentUser = user;
        this.parentPanel = parentPanel;
        this.prescriptionDAO = new PrescriptionDAO();
        this.patientDAO = new PatientDAO();
        
        initializeUI();
        if (prescription != null) {
            populateFields();
        }
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setSize(500, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Patient selection
        formPanel.add(new JLabel("Patient:"));
        patientComboBox = new JComboBox<>();
        loadPatients();
        formPanel.add(patientComboBox);
        
        // Drug name
        formPanel.add(new JLabel("Drug Name:"));
        drugNameField = new JTextField();
        formPanel.add(drugNameField);
        
        // Dosage
        formPanel.add(new JLabel("Dosage:"));
        dosageField = new JTextField();
        formPanel.add(dosageField);
        
        // Frequency
        formPanel.add(new JLabel("Frequency:"));
        frequencyField = new JTextField();
        formPanel.add(frequencyField);
        
        // Start date
        formPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        startDateField = new JTextField(LocalDate.now().toString());
        formPanel.add(startDateField);
        
        // End date
        formPanel.add(new JLabel("End Date (YYYY-MM-DD):"));
        endDateField = new JTextField(LocalDate.now().plusWeeks(2).toString());
        formPanel.add(endDateField);
        
        // Repeat prescription
        formPanel.add(new JLabel("Repeat Prescription:"));
        repeatCheckBox = new JCheckBox();
        formPanel.add(repeatCheckBox);
        
        // Comments
        formPanel.add(new JLabel("Comments:"));
        commentsArea = new JTextArea(3, 20);
        commentsArea.setLineWrap(true);
        commentsArea.setWrapStyleWord(true);
        JScrollPane commentsScroll = new JScrollPane(commentsArea);
        formPanel.add(commentsScroll);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Event handlers
        saveButton.addActionListener(new SaveHandler());
        cancelButton.addActionListener(e -> dispose());
    }
    
    private void loadPatients() {
        List<Patient> patients = patientDAO.getAllPatients();
        for (Patient patient : patients) {
            patientComboBox.addItem(patient);
        }
        
        if (patients.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No patients available. Please add patients first.",
                "No Patients", JOptionPane.WARNING_MESSAGE);
            patientComboBox.setEnabled(false);
        }
    }
    
    private void populateFields() {
        if (prescription != null) {
            // Set patient
            Patient patient = patientDAO.findPatientById(prescription.getPatientId());
            if (patient != null) {
                patientComboBox.setSelectedItem(patient);
            }
            
            // Set prescription details
            drugNameField.setText(prescription.getDrugName());
            dosageField.setText(prescription.getDosage());
            frequencyField.setText(prescription.getFrequency());
            startDateField.setText(prescription.getStartDate().toString());
            
            if (prescription.getEndDate() != null) {
                endDateField.setText(prescription.getEndDate().toString());
            }
            
            repeatCheckBox.setSelected(prescription.isRepeat());
            
            if (prescription.getComments() != null) {
                commentsArea.setText(prescription.getComments());
            }
        }
    }
    
    private class SaveHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Validate patient selection
                Patient selectedPatient = (Patient) patientComboBox.getSelectedItem();
                if (selectedPatient == null) {
                    JOptionPane.showMessageDialog(PrescriptionDialog.this,
                        "Please select a patient", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Validate required fields
                if (drugNameField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(PrescriptionDialog.this,
                        "Please enter a drug name", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (dosageField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(PrescriptionDialog.this,
                        "Please enter a dosage", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Parse dates
                LocalDate startDate = LocalDate.parse(startDateField.getText().trim());
                LocalDate endDate = endDateField.getText().trim().isEmpty() ? 
                    null : LocalDate.parse(endDateField.getText().trim());
                
                // Create or update prescription
                Prescription prescriptionToSave;
                if (prescription == null) {
                    // New prescription
                    String prescriptionId = "PRES" + System.currentTimeMillis();
                    prescriptionToSave = new Prescription(
                        prescriptionId,
                        selectedPatient.getPatientId(),
                        drugNameField.getText().trim(),
                        dosageField.getText().trim(),
                        frequencyField.getText().trim(),
                        startDate,
                        currentUser.getUserId()
                    );
                } else {
                    // Existing prescription
                    prescriptionToSave = prescription;
                   // prescriptionToSave.setDrugName(drugNameField.getText().trim());
                    //prescriptionToSave.setDosage(dosageField.getText().trim());
                    //prescriptionToSave.setFrequency(frequencyField.getText().trim());
                    //prescriptionToSave.setStartDate(startDate);
                }
                
                // Set additional fields
                prescriptionToSave.setEndDate(endDate);
                prescriptionToSave.setRepeat(repeatCheckBox.isSelected());
                prescriptionToSave.setComments(commentsArea.getText().trim());
                
                // Save prescription
                prescriptionDAO.savePrescription(prescriptionToSave);
                
                // Refresh parent panel
                parentPanel.refreshPrescriptionData();
                
                JOptionPane.showMessageDialog(PrescriptionDialog.this,
                    "Prescription saved successfully!", "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                dispose();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(PrescriptionDialog.this,
                    "Error saving prescription: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}