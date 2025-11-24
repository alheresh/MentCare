package mentcare.dao;

import mentcare.models.User;
import mentcare.utils.CSVUtils;

import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private static final String USERS_CSV = "data/users.csv";
    
    public User authenticate(String username, String password) {
        List<User> users = getAllUsers();
        return users.stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }
    
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        List<String[]> rows = CSVUtils.readCSV(USERS_CSV);
        
        // Skip header row if exists
        int startIndex = 0;
        if (!rows.isEmpty() && rows.get(0)[0].equals("userId")) {
            startIndex = 1;
        }
        
        for (int i = startIndex; i < rows.size(); i++) {
            String[] row = rows.get(i);
            if (row.length >= 5) {
                try {
                    // Debug: Print the row to see what's being read
                    System.out.println("Reading user row: " + String.join(",", row));
                    
                    String userId = row[0];
                    String username = row[1];
                    String password = row[2];
                    String roleStr = row[3];
                    String fullName = row[4];
                    
                    // Convert role string to enum - handle case sensitivity and spaces
                    User.UserRole role;
                    try {
                        role = User.UserRole.valueOf(roleStr.trim().toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid role in CSV: '" + roleStr + "'. Using CLINICAL_STAFF as default.");
                        role = User.UserRole.CLINICAL_STAFF;
                    }
                    
                    User user = new User(userId, username, password, role, fullName);
                    
                    // Add contact info if available
                    if (row.length > 5) {
                        user.setContactInfo(row[5]);
                    }
                    
                    users.add(user);
                    
                } catch (Exception e) {
                    System.err.println("Error parsing user row: " + String.join(",", row));
                    e.printStackTrace();
                }
            }
        }
        
        return users;
    }
}