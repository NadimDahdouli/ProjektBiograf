package models;

/**
 * @author Nadim Dahdouli
 */
public class Customer {

    private int ID;
    private String email;
    private String firstname;
    private String phonenumber;
    private String debitcard;

    public Customer(int ID, String email, String firstname, String phonenumber, String debitcard) {
        this(email, firstname, phonenumber, debitcard);
        this.ID = ID;
    }

    public Customer(String email, String firstname, String phonenumber, String debitcard) {
        this.email = email;
        this.firstname = firstname;
        this.phonenumber = phonenumber;
        this.debitcard = debitcard;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getDebitcard() {
        return debitcard;
    }

    public void setDebitcard(String debitcard) {
        this.debitcard = debitcard;
    }
}
