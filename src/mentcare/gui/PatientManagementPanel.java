package mentcare.gui;

import mentcare.models.User;
import mentcare.models.Patient;
import mentcare.dao.PatientDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class PatientManagementPanel extends JPanel {
    private User currentUser;
    private PatientDAO patientDAO;
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, viewButton;
    
    public PatientManagementPanel(User user) {
        this.currentUser = user;
        this.patientDAO = new PatientDAO();
        initializeUI();
        loadPatientData();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Header
        JLabel headerLabel = new JLabel("Patient Management", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(headerLabel, BorderLayout.NORTH);
        
        // Patient table
        String[] columnNames = {"Patient ID", "Name", "Age", "Risk Level", "Sectioned", "Last Review"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        patientTable = new JTable(tableModel);
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(patientTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        addButton = new JButton("Add Patient");
        editButton = new JButton("Edit Patient");
        viewButton = new JButton("View Details");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(viewButton);
        
        // Set button permissions based on user role
        if (!currentUser.hasPermission("edit_patients")) {
            addButton.setEnabled(false);
            editButton.setEnabled(false);
        }
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Event handlers
        addButton.addActionListener(new AddPatientHandler());
        editButton.addActionListener(new EditPatientHandler());
        viewButton.addActionListener(new ViewPatientHandler());
    }
    
    private void loadPatientData() {
        tableModel.setRowCount(0); // Clear existing data
        
        List<Patient> patients = patientDAO.getAllPatients();
        for (Patient patient : patients) {
            Object[] rowData = {
                patient.getPatientId(),
                patient.getName(),
                patient.getAge(),
                patient.getRiskAssessment(),
                patient.isSectioned() ? "Yes" : "No",
                patient.getReviewDate() != null ? patient.getReviewDate().toString() : "N/A"
            };
            tableModel.addRow(rowData);
        }
    }
    
    private class AddPatientHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Implementation for adding new patient
            JOptionPane.showMessageDialog(PatientManagementPanel.this,
                "Add Patient functionality to be implemented");
        }
    }
    
    private class EditPatientHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = patientTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(PatientManagementPanel.this,
                    "Please select a patient to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String patientId = (String) tableModel.getValueAt(selectedRow, 0);
            Patient patient = patientDAO.findPatientById(patientId);
            
            if (patient != null) {
                // Open edit dialog
                new PatientEditDialog(patient, PatientManagementPanel.this).setVisible(true);
            }
        }
    }
    
    private class ViewPatientHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = patientTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(PatientManagementPanel.this,
                    "Please select a patient to view", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String patientId = (String) tableModel.getValueAt(selectedRow, 0);
            Patient patient = patientDAO.findPatientById(patientId);
            
            if (patient != null) {
                // Open view dialog
                new PatientViewDialog(patient).setVisible(true);
            }
        }
    }
    
    public void refreshPatientData() {
        loadPatientData();
    }
}