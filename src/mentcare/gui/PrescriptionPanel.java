package mentcare.gui;

import mentcare.models.User;
import mentcare.models.Patient;
import mentcare.models.Prescription;
import mentcare.dao.PatientDAO;
import mentcare.dao.PrescriptionDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

public class PrescriptionPanel extends JPanel {
    private User currentUser;
    private PatientDAO patientDAO;
    private PrescriptionDAO prescriptionDAO;
    
    private JTable prescriptionTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, viewButton;
    private JComboBox<Patient> patientComboBox;
    
    public PrescriptionPanel(User user) {
        this.currentUser = user;
        this.patientDAO = new PatientDAO();
        this.prescriptionDAO = new PrescriptionDAO();
        initializeUI();
        loadPrescriptionData();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Header
        JLabel headerLabel = new JLabel("Prescription Management", JLabel.CENTER);
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
            loadPrescriptionData();
        });
        filterPanel.add(clearFilterButton);
        
        mainPanel.add(filterPanel, BorderLayout.NORTH);
        
        // Prescription table
        String[] columnNames = {"Prescription ID", "Patient", "Drug Name", "Dosage", "Frequency", "Start Date", "End Date", "Repeat"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        prescriptionTable = new JTable(tableModel);
        prescriptionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(prescriptionTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        addButton = new JButton("Add Prescription");
        editButton = new JButton("Edit Prescription");
        viewButton = new JButton("View Details");
        deleteButton = new JButton("Delete Prescription");
        JButton repeatButton = new JButton("Generate Repeat");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(repeatButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Event handlers
        addButton.addActionListener(new AddPrescriptionHandler());
        editButton.addActionListener(new EditPrescriptionHandler());
        viewButton.addActionListener(new ViewPrescriptionHandler());
        deleteButton.addActionListener(new DeletePrescriptionHandler());
        repeatButton.addActionListener(new RepeatPrescriptionHandler());
    }
    
    private void loadPatientComboBox() {
        patientComboBox.removeAllItems();
        // Add "All Patients" option
        patientComboBox.addItem(new AllPatientsOption());
        
        List<Patient> patients = patientDAO.getAllPatients();
        for (Patient patient : patients) {
            patientComboBox.addItem(patient);
        }
    }
    
    // Helper class for "All Patients" option
    private class AllPatientsOption extends Patient {
        public AllPatientsOption() {
            super("ALL", "ALL", "All Patients", "", LocalDate.now(), "");
        }
        
        @Override
        public String toString() {
            return "All Patients";
        }
    }
    
    private void loadPrescriptionData() {
        tableModel.setRowCount(0);
        
        List<Prescription> prescriptions = prescriptionDAO.getAllPrescriptions();
        Object selectedItem = patientComboBox.getSelectedItem();
        
        for (Prescription prescription : prescriptions) {
            // Apply filter if a specific patient is selected (not "All Patients")
            if (selectedItem instanceof Patient && !(selectedItem instanceof AllPatientsOption)) {
                Patient selectedPatient = (Patient) selectedItem;
                if (!prescription.getPatientId().equals(selectedPatient.getPatientId())) {
                    continue;
                }
            }
            
            // Get patient name for display
            Patient patient = patientDAO.findPatientById(prescription.getPatientId());
            String patientName = patient != null ? patient.getName() : "Unknown Patient";
            
            Object[] rowData = {
                prescription.getPrescriptionId(),
                patientName,
                prescription.getDrugName(),
                prescription.getDosage(),
                prescription.getFrequency(),
                prescription.getStartDate().toString(),
                prescription.getEndDate() != null ? prescription.getEndDate().toString() : "Not set",
                prescription.isRepeat() ? "Yes" : "No"
            };
            tableModel.addRow(rowData);
        }
    }
    
    private class FilterHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            loadPrescriptionData();
        }
    }
    
    private class AddPrescriptionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Open add prescription dialog
            PrescriptionDialog dialog = new PrescriptionDialog(null, currentUser, PrescriptionPanel.this);
            dialog.setVisible(true);
        }
    }
    
    private class EditPrescriptionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = prescriptionTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(PrescriptionPanel.this,
                    "Please select a prescription to edit", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String prescriptionId = (String) tableModel.getValueAt(selectedRow, 0);
            Prescription prescription = prescriptionDAO.getPrescriptionById(prescriptionId);
            
            if (prescription != null) {
                PrescriptionDialog dialog = new PrescriptionDialog(prescription, currentUser, PrescriptionPanel.this);
                dialog.setVisible(true);
            }
        }
    }
    
    private class ViewPrescriptionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = prescriptionTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(PrescriptionPanel.this,
                    "Please select a prescription to view", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String prescriptionId = (String) tableModel.getValueAt(selectedRow, 0);
            Prescription prescription = prescriptionDAO.getPrescriptionById(prescriptionId);
            
            if (prescription != null) {
                showPrescriptionDetails(prescription);
            }
        }
    }
    
    private class DeletePrescriptionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = prescriptionTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(PrescriptionPanel.this,
                    "Please select a prescription to delete", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String prescriptionId = (String) tableModel.getValueAt(selectedRow, 0);
            String patientName = (String) tableModel.getValueAt(selectedRow, 1);
            String drugName = (String) tableModel.getValueAt(selectedRow, 2);
            
            int confirm = JOptionPane.showConfirmDialog(PrescriptionPanel.this,
                "Are you sure you want to delete the prescription for " + 
                patientName + " - " + drugName + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                prescriptionDAO.deletePrescription(prescriptionId);
                loadPrescriptionData();
                JOptionPane.showMessageDialog(PrescriptionPanel.this,
                    "Prescription deleted successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private class RepeatPrescriptionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = prescriptionTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(PrescriptionPanel.this,
                    "Please select a prescription to generate a repeat", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String prescriptionId = (String) tableModel.getValueAt(selectedRow, 0);
            Prescription originalPrescription = prescriptionDAO.getPrescriptionById(prescriptionId);
            
            if (originalPrescription != null) {
                // Create a new prescription as a repeat
                Prescription repeatPrescription = new Prescription(
                    "PRES" + System.currentTimeMillis(),
                    originalPrescription.getPatientId(),
                    originalPrescription.getDrugName(),
                    originalPrescription.getDosage(),
                    originalPrescription.getFrequency(),
                    LocalDate.now(),
                    currentUser.getUserId()
                );
                
                repeatPrescription.setRepeat(true);
                repeatPrescription.setComments("Repeat prescription - originally prescribed on " + 
                    originalPrescription.getStartDate());
                
                // Set end date (typically 2 weeks for restricted meds)
                repeatPrescription.setEndDate(LocalDate.now().plusWeeks(2));
                
                prescriptionDAO.savePrescription(repeatPrescription);
                loadPrescriptionData();
                
                JOptionPane.showMessageDialog(PrescriptionPanel.this,
                    "Repeat prescription generated successfully!\n" +
                    "New prescription ID: " + repeatPrescription.getPrescriptionId(),
                    "Repeat Generated", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void showPrescriptionDetails(Prescription prescription) {
        Patient patient = patientDAO.findPatientById(prescription.getPatientId());
        String patientName = patient != null ? patient.getName() : "Unknown Patient";
        
        StringBuilder details = new StringBuilder();
        details.append("Prescription Details\n");
        details.append("====================\n\n");
        details.append("Prescription ID: ").append(prescription.getPrescriptionId()).append("\n");
        details.append("Patient: ").append(patientName).append("\n");
        details.append("Drug Name: ").append(prescription.getDrugName()).append("\n");
        details.append("Dosage: ").append(prescription.getDosage()).append("\n");
        details.append("Frequency: ").append(prescription.getFrequency()).append("\n");
        details.append("Start Date: ").append(prescription.getStartDate()).append("\n");
        details.append("End Date: ").append(prescription.getEndDate() != null ? prescription.getEndDate() : "Not set").append("\n");
        details.append("Repeat Prescription: ").append(prescription.isRepeat() ? "Yes" : "No").append("\n");
        details.append("Prescriber: ").append(prescription.getPrescriberId()).append("\n");
        
        if (prescription.getComments() != null && !prescription.getComments().isEmpty()) {
            details.append("Comments: ").append(prescription.getComments()).append("\n");
        }
        
        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setBackground(UIManager.getColor("Panel.background"));
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "Prescription Details - " + prescription.getPrescriptionId(), 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void refreshPrescriptionData() {
        loadPrescriptionData();
    }
}