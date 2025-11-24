package mentcare.gui;

import mentcare.models.Patient;
import mentcare.dao.PatientDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class PatientEditDialog extends JDialog {
    private Patient patient;
    private PatientManagementPanel parentPanel;
    private PatientDAO patientDAO;
    
    private JTextField nameField;
    private JTextField addressField;
    private JTextField dobField;
    private JTextField contactField;
    private JComboBox<Patient.RiskLevel> riskComboBox;
    private JCheckBox sectionedCheckBox;
    private JTextField reviewDateField;
    
    public PatientEditDialog(Patient patient, PatientManagementPanel parentPanel) {
        super((Frame) null, "Edit Patient", true);
        this.patient = patient;
        this.parentPanel = parentPanel;
        this.patientDAO = new PatientDAO();
        
        initializeUI();
        populateFields();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setSize(500, 400);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Patient ID (read-only)
        formPanel.add(new JLabel("Patient ID:"));
        JTextField idField = new JTextField(patient.getPatientId());
        idField.setEditable(false);
        formPanel.add(idField);
        
        // Name
        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);
        
        // Address
        formPanel.add(new JLabel("Address:"));
        addressField = new JTextField();
        formPanel.add(addressField);
        
        // Date of Birth
        formPanel.add(new JLabel("Date of Birth (YYYY-MM-DD):"));
        dobField = new JTextField();
        formPanel.add(dobField);
        
        // Contact Details
        formPanel.add(new JLabel("Contact Details:"));
        contactField = new JTextField();
        formPanel.add(contactField);
        
        // Risk Assessment
        formPanel.add(new JLabel("Risk Assessment:"));
        riskComboBox = new JComboBox<>(Patient.RiskLevel.values());
        formPanel.add(riskComboBox);
        
        // Sectioned status
        formPanel.add(new JLabel("Sectioned:"));
        sectionedCheckBox = new JCheckBox();
        formPanel.add(sectionedCheckBox);
        
        // Review Date
        formPanel.add(new JLabel("Review Date (YYYY-MM-DD):"));
        reviewDateField = new JTextField();
        formPanel.add(reviewDateField);
        
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
    
    private void populateFields() {
        nameField.setText(patient.getName());
        addressField.setText(patient.getAddress());
        dobField.setText(patient.getDateOfBirth().toString());
        contactField.setText(patient.getContactDetails());
        riskComboBox.setSelectedItem(patient.getRiskAssessment());
        sectionedCheckBox.setSelected(patient.isSectioned());
        
        if (patient.getReviewDate() != null) {
            reviewDateField.setText(patient.getReviewDate().toString());
        }
    }
    
    private class SaveHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Validate and update patient
                //patient.setName(nameField.getText().trim());
                //patient.setAddress(addressField.getText().trim());
                //patient.setContactDetails(contactField.getText().trim());
                
                // Parse date of birth
                LocalDate dob = LocalDate.parse(dobField.getText().trim());
                // Note: In real implementation, you'd need to handle date changes carefully
                
                patient.setRiskAssessment((Patient.RiskLevel) riskComboBox.getSelectedItem());
                patient.setSectioned(sectionedCheckBox.isSelected());
                
                // Parse review date if provided
                String reviewDateText = reviewDateField.getText().trim();
                if (!reviewDateText.isEmpty()) {
                    LocalDate reviewDate = LocalDate.parse(reviewDateText);
                    patient.setReviewDate(reviewDate);
                } else {
                    patient.setReviewDate(null);
                }
                
                // Save to CSV
                patientDAO.savePatient(patient);
                
                // Refresh parent panel
                parentPanel.refreshPatientData();
                
                JOptionPane.showMessageDialog(PatientEditDialog.this,
                    "Patient updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                dispose();
                
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(PatientEditDialog.this,
                    "Invalid date format. Please use YYYY-MM-DD format.",
                    "Date Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(PatientEditDialog.this,
                    "Error updating patient: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}