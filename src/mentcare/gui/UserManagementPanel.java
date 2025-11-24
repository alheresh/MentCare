package mentcare.gui;

import mentcare.models.User;
import mentcare.dao.UserDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class UserManagementPanel extends JPanel {
    private UserDAO userDAO;
    private JTable userTable;
    private DefaultTableModel tableModel;
    
    public UserManagementPanel() {
        this.userDAO = new UserDAO();
        initializeUI();
        loadUserData();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Header
        JLabel headerLabel = new JLabel("User Management", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(headerLabel, BorderLayout.NORTH);
        
        // User table
        String[] columnNames = {"User ID", "Username", "Full Name", "Role", "Contact Info"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(userTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton addButton = new JButton("Add User");
        JButton editButton = new JButton("Edit User");
        JButton deleteButton = new JButton("Delete User");
        JButton resetPasswordButton = new JButton("Reset Password");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(resetPasswordButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Event handlers
        addButton.addActionListener(e -> addUser());
        editButton.addActionListener(e -> editUser());
        deleteButton.addActionListener(e -> deleteUser());
        resetPasswordButton.addActionListener(e -> resetPassword());
    }
    
    private void loadUserData() {
        tableModel.setRowCount(0);
        
        // For demonstration - in real implementation, UserDAO would have getAllUsers()
        // Since we don't have getAllUsers in UserDAO, we'll create sample data
        List<User> sampleUsers = List.of(
            new User("USER001", "doctor1", "password123", User.UserRole.CLINICAL_STAFF, "Dr. John Smith"),
            new User("USER002", "admin1", "password123", User.UserRole.ADMINISTRATOR, "Admin User"),
            new User("USER003", "mha1", "password123", User.UserRole.MHA_ADMINISTRATOR, "MHA Manager"),
            new User("USER004", "sysadmin", "password123", User.UserRole.SYSTEM_ADMIN, "System Administrator")
        );
        
        for (User user : sampleUsers) {
            Object[] rowData = {
                user.getUserId(),
                user.getUsername(),
                user.getFullName(),
                user.getRole(),
                user.getContactInfo() != null ? user.getContactInfo() : "Not set"
            };
            tableModel.addRow(rowData);
        }
    }
    
    private void addUser() {
        JOptionPane.showMessageDialog(this,
            "Add User functionality will allow system administrators to:\n\n" +
            "• Create new user accounts\n" +
            "• Assign roles and permissions\n" +
            "• Set initial passwords\n" +
            "• Configure contact information\n\n" +
            "Status: Under Development",
            "Add User Feature", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void editUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a user to edit.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String userId = (String) tableModel.getValueAt(selectedRow, 0);
        JOptionPane.showMessageDialog(this,
            "Edit User functionality for user: " + userId + "\n\n" +
            "This will allow modifying:\n" +
            "• User details\n" +
            "• Role assignments\n" +
            "• Contact information\n" +
            "• Account status\n\n" +
            "Status: Under Development",
            "Edit User Feature", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a user to delete.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String userId = (String) tableModel.getValueAt(selectedRow, 0);
        String userName = (String) tableModel.getValueAt(selectedRow, 2);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete user: " + userName + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this,
                "User deletion functionality would remove: " + userName + "\n\n" +
                "Status: Under Development",
                "Delete User", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void resetPassword() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a user to reset password.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String userName = (String) tableModel.getValueAt(selectedRow, 2);
        JOptionPane.showMessageDialog(this,
            "Password reset functionality for: " + userName + "\n\n" +
            "This will allow:\n" +
            "• Resetting forgotten passwords\n" +
            "• Setting temporary passwords\n" +
            "• Forcing password change on next login\n\n" +
            "Status: Under Development",
            "Reset Password", JOptionPane.INFORMATION_MESSAGE);
    }
}