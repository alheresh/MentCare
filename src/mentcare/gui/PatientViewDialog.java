package mentcare.gui;

import mentcare.models.Patient;
import mentcare.models.Consultation;
import mentcare.dao.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PatientViewDialog extends JDialog {
    private Patient patient;
    private ConsultationDAO consultationDAO;
    
    public PatientViewDialog(Patient patient) {
        super((Frame) null, "Patient Details - " + patient.getName(), true);
        this.patient = patient;
        this.consultationDAO = new ConsultationDAO();
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setSize(700, 500);
        setLocationRelativeTo(null);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Basic Information Tab
        tabbedPane.addTab("Basic Information", createBasicInfoPanel());
        
        // Medical History Tab
        tabbedPane.addTab("Medical History", createMedicalHistoryPanel());
        
        // Consultations Tab
        tabbedPane.addTab("Consultations", createConsultationsPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Close button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createBasicInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Patient information
        addDetailRow(panel, "Patient ID:", patient.getPatientId());
        addDetailRow(panel, "National Health Number:", patient.getNationalHealthNumber());
        addDetailRow(panel, "Name:", patient.getName());
        addDetailRow(panel, "Address:", patient.getAddress());
        addDetailRow(panel, "Date of Birth:", patient.getDateOfBirth().toString());
        addDetailRow(panel, "Age:", String.valueOf(patient.getAge()));
        addDetailRow(panel, "Contact Details:", patient.getContactDetails());
        
        // Risk assessment with color coding
        JLabel riskLabel = new JLabel(patient.getRiskAssessment().toString());
        switch (patient.getRiskAssessment()) {
            case LOW: riskLabel.setForeground(Color.GREEN); break;
            case MEDIUM: riskLabel.setForeground(Color.ORANGE); break;
            case HIGH: riskLabel.setForeground(Color.RED); break;
            case CRITICAL: riskLabel.setForeground(Color.MAGENTA); break;
        }
        addDetailRow(panel, "Risk Assessment:", riskLabel);
        
        // Sectioned status
        addDetailRow(panel, "Sectioned:", patient.isSectioned() ? "Yes" : "No");
        
        if (patient.isSectioned()) {
            addDetailRow(panel, "Sectioned Date:", 
                patient.getSectionedDate() != null ? patient.getSectionedDate().toString() : "N/A");
            addDetailRow(panel, "Review Date:", 
                patient.getReviewDate() != null ? patient.getReviewDate().toString() : "N/A");
        }
        
        // Conditions
        if (!patient.getConditions().isEmpty()) {
            JTextArea conditionsArea = new JTextArea(String.join("\n", patient.getConditions()));
            conditionsArea.setEditable(false);
            conditionsArea.setBackground(panel.getBackground());
            addDetailRow(panel, "Conditions:", conditionsArea);
        } else {
            addDetailRow(panel, "Conditions:", "No conditions recorded");
        }
        
        return panel;
    }
    
    private JPanel createMedicalHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel label = new JLabel("Medical History - Under Development", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.ITALIC, 14));
        panel.add(label, BorderLayout.CENTER);
        
        // TODO: Implement medical history display
        // This would show diagnoses, treatments, prescriptions history
        
        return panel;
    }
    
    private JPanel createConsultationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Get consultations for this patient
        List<Consultation> consultations = consultationDAO.getConsultationsByPatient(patient.getPatientId());
        
        if (consultations.isEmpty()) {
            JLabel noDataLabel = new JLabel("No consultations recorded", JLabel.CENTER);
            panel.add(noDataLabel, BorderLayout.CENTER);
        } else {
            // Create table model
            String[] columnNames = {"Date", "Staff", "Diagnoses", "Updated"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            // Populate table
            for (Consultation consultation : consultations) {
                Object[] rowData = {
                    consultation.getDateTime().toLocalDate().toString(),
                    String.join(", ", consultation.getStaffIds()),
                    String.join(", ", consultation.getDiagnoses()),
                    consultation.isRecordUpdated() ? "Yes" : "No"
                };
                tableModel.addRow(rowData);
            }
            
            JTable consultationsTable = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(consultationsTable);
            panel.add(scrollPane, BorderLayout.CENTER);
        }
        
        return panel;
    }
    
    private void addDetailRow(JPanel panel, String label, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(labelComponent.getFont().deriveFont(Font.BOLD));
        panel.add(labelComponent);
        
        JLabel valueComponent = new JLabel(value != null ? value : "N/A");
        panel.add(valueComponent);
    }
    
    private void addDetailRow(JPanel panel, String label, JComponent component) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(labelComponent.getFont().deriveFont(Font.BOLD));
        panel.add(labelComponent);
        panel.add(component);
    }
}