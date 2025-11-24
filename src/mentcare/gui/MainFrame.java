package mentcare.gui;

import mentcare.models.User;
import mentcare.models.Patient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private User currentUser;
    private JTabbedPane tabbedPane;
    
    public MainFrame(User user) {
        this.currentUser = user;
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Mentcare System - Welcome " + currentUser.getFullName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Create menu bar
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Add tabs based on user role
        switch (currentUser.getRole()) {
            case CLINICAL_STAFF:
                //tabbedPane.addTab("Patient Management", new PatientManagementPanel(currentUser));
                tabbedPane.addTab("Consultations", new ConsultationPanel(currentUser));
                tabbedPane.addTab("Prescriptions", new PrescriptionPanel(currentUser));
                break;
            case ADMINISTRATOR:
                tabbedPane.addTab("Appointments", new AppointmentPanel(currentUser));
                tabbedPane.addTab("Reports", new ReportPanel(currentUser));
                break;
            case MHA_ADMINISTRATOR:
                tabbedPane.addTab("MHA Management", new MHAPanel(currentUser));
                tabbedPane.addTab("Patient Overview", new PatientManagementPanel(currentUser));
                break;
            case SYSTEM_ADMIN:
                tabbedPane.addTab("User Management", new UserManagementPanel());
                tabbedPane.addTab("System Settings", new SystemSettingsPanel());
                break;
        }
        
        add(tabbedPane);
    }
    
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        JMenuItem exitItem = new JMenuItem("Exit");
        
        logoutItem.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        
        aboutItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Mentcare System v1.0\nMental Health Patient Management System",
                "About", JOptionPane.INFORMATION_MESSAGE);
        });
        
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        
        return menuBar;
    }
}