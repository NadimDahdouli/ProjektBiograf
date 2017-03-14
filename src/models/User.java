package models;

/**
 * @author Nadim Dahdouli
 */
public class User {


    private int ID;
    private String username;
    private String password;
    private boolean admin;

    public User(int ID, String username, String password, boolean admin) {
        this(username, password, admin);
        this.ID = ID;
    }

    public User(String username, String password, boolean admin) {
        this.username = username;
        this.password = password;
        this.admin = admin;
    }


    public int getID() {
        return ID;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
