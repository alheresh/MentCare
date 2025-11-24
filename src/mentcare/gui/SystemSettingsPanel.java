package mentcare.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SystemSettingsPanel extends JPanel {
    private JCheckBox backupEnabledCheckBox;
    private JTextField backupTimeField;
    private JComboBox<String> logLevelComboBox;
    private JTextField dataRetentionField;
    
    public SystemSettingsPanel() {
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Header
        JLabel headerLabel = new JLabel("System Settings", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(headerLabel, BorderLayout.NORTH);
        
        // Settings panel
        JPanel settingsPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Backup settings
        settingsPanel.add(new JLabel("Enable Automatic Backups:"));
        backupEnabledCheckBox = new JCheckBox("Enabled", true);
        settingsPanel.add(backupEnabledCheckBox);
        
        settingsPanel.add(new JLabel("Backup Time:"));
        backupTimeField = new JTextField("20:00");
        settingsPanel.add(backupTimeField);
        
        // Logging settings
        settingsPanel.add(new JLabel("Log Level:"));
        logLevelComboBox = new JComboBox<>(new String[]{"DEBUG", "INFO", "WARN", "ERROR"});
        logLevelComboBox.setSelectedItem("INFO");
        settingsPanel.add(logLevelComboBox);
        
        // Data retention
        settingsPanel.add(new JLabel("Data Retention (months):"));
        dataRetentionField = new JTextField("36");
        settingsPanel.add(dataRetentionField);
        
        // Security settings
        settingsPanel.add(new JLabel("Session Timeout (minutes):"));
        JTextField sessionTimeoutField = new JTextField("30");
        settingsPanel.add(sessionTimeoutField);
        
        settingsPanel.add(new JLabel("Password Policy:"));
        JComboBox<String> passwordPolicyComboBox = new JComboBox<>(
            new String[]{"Standard", "Enhanced", "Strict"});
        passwordPolicyComboBox.setSelectedItem("Standard");
        settingsPanel.add(passwordPolicyComboBox);
        
        add(settingsPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Save Settings");
        JButton resetButton = new JButton("Reset to Defaults");
        JButton testBackupButton = new JButton("Test Backup");
        JButton viewLogsButton = new JButton("View System Logs");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(testBackupButton);
        buttonPanel.add(viewLogsButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Event handlers
        saveButton.addActionListener(new SaveSettingsHandler());
        resetButton.addActionListener(e -> resetToDefaults());
        testBackupButton.addActionListener(e -> testBackup());
        viewLogsButton.addActionListener(e -> viewSystemLogs());
    }
    
    private class SaveSettingsHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Validate settings
            if (!isValidTime(backupTimeField.getText())) {
                JOptionPane.showMessageDialog(SystemSettingsPanel.this,
                    "Invalid backup time format. Please use HH:MM (24-hour format).",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                int retentionMonths = Integer.parseInt(dataRetentionField.getText());
                if (retentionMonths < 6) {
                    JOptionPane.showMessageDialog(SystemSettingsPanel.this,
                        "Data retention must be at least 6 months.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(SystemSettingsPanel.this,
                    "Invalid data retention value. Please enter a number.",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Save settings (in real implementation, this would persist to configuration)
            JOptionPane.showMessageDialog(SystemSettingsPanel.this,
                "System settings saved successfully!\n\n" +
                "Backup Enabled: " + backupEnabledCheckBox.isSelected() + "\n" +
                "Backup Time: " + backupTimeField.getText() + "\n" +
                "Log Level: " + logLevelComboBox.getSelectedItem() + "\n" +
                "Data Retention: " + dataRetentionField.getText() + " months",
                "Settings Saved", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private boolean isValidTime(String time) {
        return time.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$");
    }
    
    private void resetToDefaults() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to reset all settings to default values?",
            "Confirm Reset", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            backupEnabledCheckBox.setSelected(true);
            backupTimeField.setText("20:00");
            logLevelComboBox.setSelectedItem("INFO");
            dataRetentionField.setText("36");
            
            JOptionPane.showMessageDialog(this,
                "All settings have been reset to default values.",
                "Reset Complete", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void testBackup() {
        JOptionPane.showMessageDialog(this,
            "Backup test initiated...\n\n" +
            "This would:\n" +
            "• Create a test backup of the database\n" +
            "• Verify backup integrity\n" +
            "• Generate a backup report\n" +
            "• Send notification of test results\n\n" +
            "Status: Under Development",
            "Test Backup", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void viewSystemLogs() {
        JOptionPane.showMessageDialog(this,
            "System Logs Viewer\n\n" +
            "This would display:\n" +
            "• Application logs\n" +
            "• Security audit logs\n" +
            "• Error logs\n" +
            "• User activity logs\n\n" +
            "Status: Under Development",
            "System Logs", JOptionPane.INFORMATION_MESSAGE);
    }
}