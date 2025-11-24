package mentcare.gui;

import mentcare.models.User;
import javax.swing.*;

public class AppointmentPanel extends JPanel {
    private User currentUser;
    
    public AppointmentPanel(User user) {
        this.currentUser = user;
        initializeUI();
    }
    
    private void initializeUI() {
    //    setLayout(new BorderLayout());
        
  //      JPanel contentPanel = new JPanel(new BorderLayout());
    //    contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Appointment Management", JLabel.CENTER);
   //     titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
  //      contentPanel.add(titleLabel, BorderLayout.NORTH);
        
        JTextArea descriptionArea = new JTextArea(
            "This module will integrate with the APPOINTMENTS system to:\n\n" +
            "• Schedule patient appointments\n" +
            "• Manage clinic schedules\n" +
            "• Track appointment attendance\n" +
            "• Send appointment reminders\n" +
            "• Generate appointment reports\n\n" +
            "Status: Under Development"
        );
        descriptionArea.setEditable(false);
        descriptionArea.setBackground(getBackground());
       // descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
      //  contentPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);
        
     //   add(contentPanel, BorderLayout.CENTER);
    }
}