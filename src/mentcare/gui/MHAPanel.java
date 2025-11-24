package mentcare.gui;

import mentcare.models.User;
import mentcare.models.Patient;
import mentcare.dao.PatientDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class MHAPanel extends JPanel {
    private User currentUser;
    private PatientDAO patientDAO;
    private JTable mhaTable;
    private DefaultTableModel tableModel;
    
    public MHAPanel(User user) {
        this.currentUser = user;
        this.patientDAO = new PatientDAO();
        initializeUI();
        loadMHAData();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Header
        JLabel headerLabel = new JLabel("Mental Health Act (MHA) Management", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(headerLabel, BorderLayout.NORTH);
        
        // Description
        JTextArea descriptionArea = new JTextArea(
            "This panel manages patients under the Mental Health Act (Scotland) 2003.\n" +
            "It tracks sectioned patients, review dates, and ensures compliance with legal requirements."
        );
        descriptionArea.setEditable(false);
        descriptionArea.setBackground(getBackground());
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 12));
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(descriptionArea, BorderLayout.NORTH);
        
        // MHA patients table
        String[] columnNames = {"Patient ID", "Name", "Risk Level", "Sectioned Date", "Review Date", "Days Until Review"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        mhaTable = new JTable(tableModel);
        mhaTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(mhaTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton scheduleReviewButton = new JButton("Schedule Review");
        JButton sendRemindersButton = new JButton("Send Reminders");
        JButton viewDetailsButton = new JButton("View Patient Details");
        JButton generateLettersButton = new JButton("Generate MHA Letters");
        
        buttonPanel.add(scheduleReviewButton);
        buttonPanel.add(sendRemindersButton);
        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(generateLettersButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Event handlers
        scheduleReviewButton.addActionListener(new ScheduleReviewHandler());
        sendRemindersButton.addActionListener(new SendRemindersHandler());
        viewDetailsButton.addActionListener(new ViewDetailsHandler());
        generateLettersButton.addActionListener(new GenerateLettersHandler());
    }
    
    private void loadMHAData() {
        tableModel.setRowCount(0);
        
        List<Patient> sectionedPatients = patientDAO.getAllPatients().stream()
            .filter(Patient::isSectioned)
            .collect(Collectors.toList());
        
        for (Patient patient : sectionedPatients) {
            long daysUntilReview = patient.getReviewDate() != null ? 
                java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), patient.getReviewDate()) : -1;
            
            Object[] rowData = {
                patient.getPatientId(),
                patient.getName(),
                patient.getRiskAssessment(),
                patient.getSectionedDate() != null ? patient.getSectionedDate().toString() : "Not set",
                patient.getReviewDate() != null ? patient.getReviewDate().toString() : "Not scheduled",
                daysUntilReview >= 0 ? daysUntilReview + " days" : "No review scheduled"
            };
            tableModel.addRow(rowData);
        }
    }
    
    private class ScheduleReviewHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = mhaTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(MHAPanel.this,
                    "Please select a patient to schedule a review for.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String patientId = (String) tableModel.getValueAt(selectedRow, 0);
            Patient patient = patientDAO.findPatientById(patientId);
            
            if (patient != null) {
                String reviewDateStr = JOptionPane.showInputDialog(MHAPanel.this,
                    "Enter review date for " + patient.getName() + " (YYYY-MM-DD):",
                    patient.getReviewDate() != null ? patient.getReviewDate().toString() : LocalDate.now().plusDays(30).toString());
                
                if (reviewDateStr != null && !reviewDateStr.trim().isEmpty()) {
                    try {
                        LocalDate reviewDate = LocalDate.parse(reviewDateStr.trim());
                        patient.setReviewDate(reviewDate);
                        patientDAO.savePatient(patient);
                        loadMHAData();
                        
                        JOptionPane.showMessageDialog(MHAPanel.this,
                            "Review scheduled for " + reviewDate,
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(MHAPanel.this,
                            "Invalid date format. Please use YYYY-MM-DD.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }
    
    private class SendRemindersHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Simulate sending reminders
            List<Patient> patientsNeedingReview = patientDAO.getAllPatients().stream()
                .filter(p -> p.isSectioned() && p.getReviewDate() != null)
                .filter(p -> java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), p.getReviewDate()) <= 30)
                .collect(Collectors.toList());
            
            if (patientsNeedingReview.isEmpty()) {
                JOptionPane.showMessageDialog(MHAPanel.this,
                    "No patients currently require review reminders.",
                    "No Reminders Needed", JOptionPane.INFORMATION_MESSAGE);
            } else {
                StringBuilder message = new StringBuilder();
                message.append("Reminders would be sent for:\n\n");
                for (Patient patient : patientsNeedingReview) {
                    long daysUntilReview = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), patient.getReviewDate());
                    message.append("• ").append(patient.getName())
                          .append(" (Review in ").append(daysUntilReview).append(" days)\n");
                }
                
                JOptionPane.showMessageDialog(MHAPanel.this,
                    message.toString(),
                    "Review Reminders", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private class ViewDetailsHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = mhaTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(MHAPanel.this,
                    "Please select a patient to view details.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String patientId = (String) tableModel.getValueAt(selectedRow, 0);
            Patient patient = patientDAO.findPatientById(patientId);
            
            if (patient != null) {
                new PatientViewDialog(patient).setVisible(true);
            }
        }
    }
    
    private class GenerateLettersHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(MHAPanel.this,
                "MHA letter generation feature will create:\n" +
                "• Patient notification letters\n" +
                "• Carer information letters\n" +
                "• Facility manager notifications\n" +
                "• Review outcome letters\n\n" +
                "Status: Under Development",
                "MHA Letter Generation", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}