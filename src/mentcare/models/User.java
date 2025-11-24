package mentcare.models;

public class User {
    private String userId;
    private String username;
    private String password;
    private UserRole role;
    private String fullName;
    private String contactInfo;
    
    public enum UserRole {
        CLINICAL_STAFF, ADMINISTRATOR, SYSTEM_ADMIN, MHA_ADMINISTRATOR
    }
    
    public User(String userId, String username, String password, UserRole role, String fullName) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
    }
    
    // Getters and setters
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public UserRole getRole() { return role; }
    public String getFullName() { return fullName; }
    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    
    public boolean hasPermission(String permission) {
        // Simplified permission check based on role
        switch (role) {
            case CLINICAL_STAFF:
                return permission.equals("view_patients") || permission.equals("edit_patients") 
                    || permission.equals("prescribe_medication");
            case ADMINISTRATOR:
                return permission.equals("view_patients") || permission.equals("manage_appointments")
                    || permission.equals("generate_reports");
            case MHA_ADMINISTRATOR:
                return permission.equals("view_patients") || permission.equals("manage_mha");
            case SYSTEM_ADMIN:
                return true;
            default:
                return false;
        }
    }
}