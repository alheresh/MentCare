package mentcare.gui;

import mentcare.models.User;
import mentcare.models.Patient;
import mentcare.models.Consultation;
import mentcare.dao.PatientDAO;
import mentcare.dao.ConsultationDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.util.List;

public class ConsultationPanel extends JPanel {
    private User currentUser;
    private PatientDAO patientDAO;
    private ConsultationDAO consultationDAO;
    
    private JTable consultationTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, viewButton;
    private JComboBox<Patient> patientComboBox;
    
    public ConsultationPanel(User user) {
        this.currentUser = user;
        this.patientDAO = new PatientDAO();
        this.consultationDAO = new ConsultationDAO();
        initializeUI();
        loadConsultationData();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Header
        JLabel headerLabel = new JLabel("Consultation Management", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(headerLabel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter by Patient:"));
        
        patientComboBox = new JComboBox<>();
        loadPatientComboBox();
        patientComboBox.addActionListener(new FilterHandler());
        filterPanel.add(patientComboBox);
        
        JButton clearFilterButton = new JButton("Clear Filter");
        clearFilterButton.addActionListener(e -> {
            patientComboBox.setSelectedIndex(0);
            loadConsultationData();
        });
        filterPanel.add(clearFilterButton);
        
        mainPanel.add(filterPanel, BorderLayout.NORTH);
        
        // Consultation table
        String[] columnNames = {"Consultation ID", "Patient", "Date", "Time", "Staff", "Diagnoses", "Updated"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        consultationTable = new JTable(tableModel);
        consultationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(consultationTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        addButton = new JButton("Add Consultation");
        editButton = new JButton("Edit Consultation");
        viewButton = new JButton("View Details");
        deleteButton = new JButton("Delete Consultation");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(deleteButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Event handlers
        addButton.addActionListener(new AddConsultationHandler());
        editButton.addActionListener(new EditConsultationHandler());
        viewButton.addActionListener(new ViewConsultationHandler());
        deleteButton.addActionListener(new DeleteConsultationHandler());
    }
    
    private void loadPatientComboBox() {
        patientComboBox.removeAllItems();
        patientComboBox.addItem(new Patient("ALL", "ALL", "All Patients", "", java.time.LocalDate.now(), ""));
        
        List<Patient> patients = patientDAO.getAllPatients();
        for (Patient patient : patients) {
            patientComboBox.addItem(patient);
        }
    }
    
    private void loadConsultationData() {
        tableModel.setRowCount(0);
        
        List<Consultation> consultations = consultationDAO.getAllConsultations();
        Patient selectedPatient = (Patient) patientComboBox.getSelectedItem();
        
        for (Consultation consultation : consultations) {
            // Apply filter if a specific patient is selected
            if (selectedPatient != null && !selectedPatient.getPatientId().equals("ALL") 
                && !consultation.getPatientId().equals(selectedPatient.getPatientId())) {
                continue;
            }
            
            // Get patient name for display
            Patient patient = patientDAO.findPatientById(consultation.getPatientId());
            String patientName = patient != null ? patient.getName() : "Unknown Patient";
            
            Object[] rowData = {
                consultation.getConsultationId(),
                patientName,
                consultation.getDateTime().toLocalDate().toString(),
                consultation.getDateTime().toLocalTime().toString(),
                String.join(", ", consultation.getStaffIds()),
                String.join(", ", consultation.getDiagnoses()),
                consultation.isRecordUpdated() ? "Yes" : "No"
            };
            tableModel.addRow(rowData);
        }
    }
    
    private class FilterHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            loadConsultationData();
        }
    }
    
    private class AddConsultationHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Open add consultation dialog
            new ConsultationDialog(null, currentUser, ConsultationPanel.this).setVisible(true);
        }
    }
    
    private class EditConsultationHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = consultationTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(ConsultationPanel.this,
                    "Please select a consultation to edit", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String consultationId = (String) tableModel.getValueAt(selectedRow, 0);
            Consultation consultation = consultationDAO.getConsultationById(consultationId);
            
            if (consultation != null) {
                new ConsultationDialog(consultation, currentUser, ConsultationPanel.this).setVisible(true);
            }
        }
    }
    
    private class ViewConsultationHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = consultationTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(ConsultationPanel.this,
                    "Please select a consultation to view", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String consultationId = (String) tableModel.getValueAt(selectedRow, 0);
            Consultation consultation = consultationDAO.getConsultationById(consultationId);
            
            if (consultation != null) {
                // Show consultation details in a dialog
                showConsultationDetails(consultation);
            }
        }
    }
    
    private class DeleteConsultationHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = consultationTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(ConsultationPanel.this,
                    "Please select a consultation to delete", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String consultationId = (String) tableModel.getValueAt(selectedRow, 0);
            String patientName = (String) tableModel.getValueAt(selectedRow, 1);
            
            int confirm = JOptionPane.showConfirmDialog(ConsultationPanel.this,
                "Are you sure you want to delete the consultation for " + patientName + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                consultationDAO.deleteConsultation(consultationId);
                loadConsultationData();
                JOptionPane.showMessageDialog(ConsultationPanel.this,
                    "Consultation deleted successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void showConsultationDetails(Consultation consultation) {
        Patient patient = patientDAO.findPatientById(consultation.getPatientId());
        String patientName = patient != null ? patient.getName() : "Unknown Patient";
        
        StringBuilder details = new StringBuilder();
        details.append("Consultation Details\n");
        details.append("===================\n");
        details.append("ID: ").append(consultation.getConsultationId()).append("\n");
        details.append("Patient: ").append(patientName).append("\n");
        details.append("Date: ").append(consultation.getDateTime().toLocalDate()).append("\n");
        details.append("Time: ").append(consultation.getDateTime().toLocalTime()).append("\n");
        details.append("Staff: ").append(String.join(", ", consultation.getStaffIds())).append("\n");
        details.append("Diagnoses: ").append(String.join(", ", consultation.getDiagnoses())).append("\n");
        
        if (consultation.getSubjectiveImpressions() != null && !consultation.getSubjectiveImpressions().isEmpty()) {
            details.append("Impressions: ").append(consultation.getSubjectiveImpressions()).append("\n");
        }
        
        if (!consultation.getReferrals().isEmpty()) {
            details.append("Referrals: ").append(String.join(", ", consultation.getReferrals())).append("\n");
        }
        
        details.append("Record Updated: ").append(consultation.isRecordUpdated() ? "Yes" : "No").append("\n");
        
        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setBackground(UIManager.getColor("Panel.background"));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "Consultation Details", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void refreshConsultationData() {
        loadConsultationData();
    }
}