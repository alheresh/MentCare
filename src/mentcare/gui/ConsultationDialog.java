package mentcare.gui;

import mentcare.models.Consultation;
import mentcare.models.Patient;
import mentcare.models.User;
import mentcare.dao.ConsultationDAO;
import mentcare.dao.PatientDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ConsultationDialog extends JDialog {
    private Consultation consultation;
    private User currentUser;
    private ConsultationPanel parentPanel;
    private ConsultationDAO consultationDAO;
    private PatientDAO patientDAO;
    
    private JComboBox<Patient> patientComboBox;
    private JTextField dateField;
    private JTextField timeField;
    private JTextArea impressionsArea;
    private JTextArea diagnosesArea;
    private JTextArea referralsArea;
    private JCheckBox updatedCheckBox;
    
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    
    public ConsultationDialog(Consultation consultation, User user, ConsultationPanel parentPanel) {
        super((Frame) null, 
              consultation == null ? "Add Consultation" : "Edit Consultation", 
              true);
        this.consultation = consultation;
        this.currentUser = user;
        this.parentPanel = parentPanel;
        this.consultationDAO = new ConsultationDAO();
        this.patientDAO = new PatientDAO();
        
        initializeUI();
        if (consultation != null) {
            populateFields();
        }
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setSize(500, 600);
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
        
        // Date and time
        formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField(LocalDate.now().format(dateFormatter));
        formPanel.add(dateField);
        
        formPanel.add(new JLabel("Time (HH:MM):"));
        timeField = new JTextField(LocalTime.now().format(timeFormatter));
        formPanel.add(timeField);
        
        // Subjective impressions
        formPanel.add(new JLabel("Subjective Impressions:"));
        impressionsArea = new JTextArea(3, 20);
        impressionsArea.setLineWrap(true);
        impressionsArea.setWrapStyleWord(true);
        JScrollPane impressionsScroll = new JScrollPane(impressionsArea);
        formPanel.add(impressionsScroll);
        
        // Diagnoses
        formPanel.add(new JLabel("Diagnoses (separate with commas):"));
        diagnosesArea = new JTextArea(3, 20);
        diagnosesArea.setLineWrap(true);
        diagnosesArea.setWrapStyleWord(true);
        JScrollPane diagnosesScroll = new JScrollPane(diagnosesArea);
        formPanel.add(diagnosesScroll);
        
        // Referrals
        formPanel.add(new JLabel("Referrals (separate with commas):"));
        referralsArea = new JTextArea(2, 20);
        referralsArea.setLineWrap(true);
        referralsArea.setWrapStyleWord(true);
        JScrollPane referralsScroll = new JScrollPane(referralsArea);
        formPanel.add(referralsScroll);
        
        // Record updated
        formPanel.add(new JLabel("Record Updated:"));
        updatedCheckBox = new JCheckBox();
        updatedCheckBox.setSelected(true);
        formPanel.add(updatedCheckBox);
        
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
        
        // Set patient to current selection if editing
        if (consultation != null) {
            Patient patient = patientDAO.findPatientById(consultation.getPatientId());
            if (patient != null) {
                patientComboBox.setSelectedItem(patient);
            }
        }
    }
    
    private void loadPatients() {
        List<Patient> patients = patientDAO.getAllPatients();
        for (Patient patient : patients) {
            patientComboBox.addItem(patient);
        }
        
        // If no patients, disable the dialog
        if (patients.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No patients available. Please add patients first.",
                "No Patients", JOptionPane.WARNING_MESSAGE);
            patientComboBox.setEnabled(false);
        }
    }
    
    private void populateFields() {
        if (consultation != null) {
            // Date and time are already set in constructor via patient selection
            
            // Set impressions
            if (consultation.getSubjectiveImpressions() != null) {
                impressionsArea.setText(consultation.getSubjectiveImpressions());
            }
            
            // Set diagnoses
            if (!consultation.getDiagnoses().isEmpty()) {
                diagnosesArea.setText(String.join(", ", consultation.getDiagnoses()));
            }
            
            // Set referrals
            if (!consultation.getReferrals().isEmpty()) {
                referralsArea.setText(String.join(", ", consultation.getReferrals()));
            }
            
            // Set updated status
            updatedCheckBox.setSelected(consultation.isRecordUpdated());
        }
    }
    
    private class SaveHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Validate patient selection
                Patient selectedPatient = (Patient) patientComboBox.getSelectedItem();
                if (selectedPatient == null) {
                    JOptionPane.showMessageDialog(ConsultationDialog.this,
                        "Please select a patient", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Validate date
                LocalDate date;
                try {
                    date = LocalDate.parse(dateField.getText().trim(), dateFormatter);
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(ConsultationDialog.this,
                        "Invalid date format. Please use YYYY-MM-DD format.",
                        "Date Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Validate time
                LocalTime time;
                try {
                    time = LocalTime.parse(timeField.getText().trim(), timeFormatter);
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(ConsultationDialog.this,
                        "Invalid time format. Please use HH:MM format (24-hour).",
                        "Time Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Combine date and time
                LocalDateTime dateTime = LocalDateTime.of(date, time);
                
                // Create or update consultation
                Consultation consultationToSave;
                if (consultation == null) {
                    // New consultation - generate ID
                    String consultationId = "CONS" + System.currentTimeMillis();
                    consultationToSave = new Consultation(consultationId, 
                        selectedPatient.getPatientId(), dateTime);
                } else {
                    // Existing consultation
                    consultationToSave = consultation;
                }
                
                // Update consultation details
                consultationToSave.getStaffIds().clear();
                consultationToSave.getStaffIds().add(currentUser.getUserId());
                
                consultationToSave.setSubjectiveImpressions(impressionsArea.getText().trim());
                consultationToSave.setRecordUpdated(updatedCheckBox.isSelected());
                
                // Update diagnoses
                consultationToSave.getDiagnoses().clear();
                String diagnosesText = diagnosesArea.getText().trim();
                if (!diagnosesText.isEmpty()) {
                    String[] diagnoses = diagnosesText.split(",");
                    for (String diagnosis : diagnoses) {
                        String trimmed = diagnosis.trim();
                        if (!trimmed.isEmpty()) {
                            consultationToSave.addDiagnosis(trimmed);
                        }
                    }
                }
                
                // Update referrals
                consultationToSave.getReferrals().clear();
                String referralsText = referralsArea.getText().trim();
                if (!referralsText.isEmpty()) {
                    String[] referrals = referralsText.split(",");
                    for (String referral : referrals) {
                        String trimmed = referral.trim();
                        if (!trimmed.isEmpty()) {
                            consultationToSave.addReferral(trimmed);
                        }
                    }
                }
                
                // Save consultation
                consultationDAO.saveConsultation(consultationToSave);
                
                // Refresh parent panel
                parentPanel.refreshConsultationData();
                
                JOptionPane.showMessageDialog(ConsultationDialog.this,
                    "Consultation saved successfully!", "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                dispose();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ConsultationDialog.this,
                    "Error saving consultation: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}