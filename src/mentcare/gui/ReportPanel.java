package mentcare.gui;

import mentcare.models.User;
import mentcare.dao.PatientDAO;
import mentcare.dao.ConsultationDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ReportPanel extends JPanel {
    private User currentUser;
    private PatientDAO patientDAO;
    private ConsultationDAO consultationDAO;
    private JComboBox<String> reportTypeComboBox;
    private JTextArea reportArea;
    
    public ReportPanel(User user) {
        this.currentUser = user;
        this.patientDAO = new PatientDAO();
        this.consultationDAO = new ConsultationDAO();
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Header
        JLabel headerLabel = new JLabel("Report Generation", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(headerLabel, BorderLayout.NORTH);
        
        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(new JLabel("Report Type:"));
        
        reportTypeComboBox = new JComboBox<>(new String[]{
            "Patient Statistics",
            "Consultation Summary", 
            "Risk Assessment Overview",
            "Sectioned Patients Report"
        });
        controlPanel.add(reportTypeComboBox);
        
        JButton generateButton = new JButton("Generate Report");
        generateButton.addActionListener(new GenerateReportHandler());
        controlPanel.add(generateButton);
        
        JButton exportButton = new JButton("Export Report");
        exportButton.addActionListener(e -> exportReport());
        controlPanel.add(exportButton);
        
        add(controlPanel, BorderLayout.NORTH);
        
        // Report area
        reportArea = new JTextArea(20, 60);
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reportArea.setText("Select a report type and click 'Generate Report' to view data.");
        
        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private class GenerateReportHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String reportType = (String) reportTypeComboBox.getSelectedItem();
            switch (reportType) {
                case "Patient Statistics":
                    generatePatientStatistics();
                    break;
                case "Consultation Summary":
                    generateConsultationSummary();
                    break;
                case "Risk Assessment Overview":
                    generateRiskAssessmentReport();
                    break;
                case "Sectioned Patients Report":
                    generateSectionedPatientsReport();
                    break;
            }
        }
    }
    
    private void generatePatientStatistics() {
        StringBuilder report = new StringBuilder();
        report.append("PATIENT STATISTICS REPORT\n");
        report.append("=========================\n\n");
        
        var patients = patientDAO.getAllPatients();
        
        report.append("Total Patients: ").append(patients.size()).append("\n\n");
        
        // Risk level breakdown
        long lowRisk = patients.stream().filter(p -> p.getRiskAssessment() == mentcare.models.Patient.RiskLevel.LOW).count();
        long mediumRisk = patients.stream().filter(p -> p.getRiskAssessment() == mentcare.models.Patient.RiskLevel.MEDIUM).count();
        long highRisk = patients.stream().filter(p -> p.getRiskAssessment() == mentcare.models.Patient.RiskLevel.HIGH).count();
        long criticalRisk = patients.stream().filter(p -> p.getRiskAssessment() == mentcare.models.Patient.RiskLevel.CRITICAL).count();
        
        report.append("Risk Level Breakdown:\n");
        report.append("  LOW: ").append(lowRisk).append(" patients\n");
        report.append("  MEDIUM: ").append(mediumRisk).append(" patients\n");
        report.append("  HIGH: ").append(highRisk).append(" patients\n");
        report.append("  CRITICAL: ").append(criticalRisk).append(" patients\n\n");
        
        // Sectioned patients
        long sectioned = patients.stream().filter(p -> p.isSectioned()).count();
        report.append("Sectioned Patients: ").append(sectioned).append("\n");
        report.append("Non-Sectioned Patients: ").append(patients.size() - sectioned).append("\n\n");
        
        // Age statistics
        double averageAge = patients.stream()
            .mapToInt(p -> p.getAge())
            .average()
            .orElse(0.0);
        report.append("Average Patient Age: ").append(String.format("%.1f years", averageAge)).append("\n");
        
        reportArea.setText(report.toString());
    }
    
    private void generateConsultationSummary() {
        StringBuilder report = new StringBuilder();
        report.append("CONSULTATION SUMMARY REPORT\n");
        report.append("===========================\n\n");
        
        var consultations = consultationDAO.getAllConsultations();
        var patients = patientDAO.getAllPatients();
        
        report.append("Total Consultations: ").append(consultations.size()).append("\n");
        report.append("Total Patients with Consultations: ").append(
            consultations.stream().map(c -> c.getPatientId()).distinct().count()
        ).append("\n\n");
        
        // Consultations per patient
        report.append("Consultations per Patient:\n");
        for (var patient : patients) {
            long patientConsultations = consultations.stream()
                .filter(c -> c.getPatientId().equals(patient.getPatientId()))
                .count();
            if (patientConsultations > 0) {
                report.append("  ").append(patient.getName()).append(": ").append(patientConsultations).append(" consultations\n");
            }
        }
        
        reportArea.setText(report.toString());
    }
    
    private void generateRiskAssessmentReport() {
        StringBuilder report = new StringBuilder();
        report.append("RISK ASSESSMENT OVERVIEW\n");
        report.append("========================\n\n");
        
        var patients = patientDAO.getAllPatients();
        
        report.append("High and Critical Risk Patients:\n");
        report.append("--------------------------------\n");
        
        patients.stream()
            .filter(p -> p.getRiskAssessment() == mentcare.models.Patient.RiskLevel.HIGH || 
                        p.getRiskAssessment() == mentcare.models.Patient.RiskLevel.CRITICAL)
            .forEach(patient -> {
                report.append("Name: ").append(patient.getName()).append("\n");
                report.append("  Patient ID: ").append(patient.getPatientId()).append("\n");
                report.append("  Risk Level: ").append(patient.getRiskAssessment()).append("\n");
                report.append("  Sectioned: ").append(patient.isSectioned() ? "Yes" : "No").append("\n");
                if (patient.isSectioned() && patient.getReviewDate() != null) {
                    report.append("  Next Review: ").append(patient.getReviewDate()).append("\n");
                }
                report.append("\n");
            });
        
        reportArea.setText(report.toString());
    }
    
    private void generateSectionedPatientsReport() {
        StringBuilder report = new StringBuilder();
        report.append("SECTIONED PATIENTS REPORT\n");
        report.append("=========================\n\n");
        
        var patients = patientDAO.getAllPatients();
        var sectionedPatients = patients.stream().filter(p -> p.isSectioned()).toList();
        
        report.append("Total Sectioned Patients: ").append(sectionedPatients.size()).append("\n\n");
        
        for (var patient : sectionedPatients) {
            report.append("Patient: ").append(patient.getName()).append("\n");
            report.append("  ID: ").append(patient.getPatientId()).append("\n");
            report.append("  Risk Level: ").append(patient.getRiskAssessment()).append("\n");
            report.append("  Sectioned Date: ").append(patient.getSectionedDate() != null ? patient.getSectionedDate() : "Not recorded").append("\n");
            report.append("  Review Date: ").append(patient.getReviewDate() != null ? patient.getReviewDate() : "Not scheduled").append("\n");
            report.append("\n");
        }
        
        reportArea.setText(report.toString());
    }
    
    private void exportReport() {
        JOptionPane.showMessageDialog(this,
            "Export functionality will be implemented to save reports to CSV/PDF formats.",
            "Export Feature", JOptionPane.INFORMATION_MESSAGE);
    }
}