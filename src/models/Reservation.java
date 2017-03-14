package models;

/**
 * @author Nadim Dahdouli
 */
public class Reservation {

    private int ID;
    private boolean paid;
    private int screening_id;
    private int customer_id;

    public Reservation(int ID, boolean paid, int screening_id, int customer_id) {
        this(paid, screening_id, customer_id);
        this.ID = ID;
    }

    public Reservation(boolean paid, int screening_id, int customer_id) {
        this.paid = paid;
        this.screening_id = screening_id;
        this.customer_id = customer_id;
    }

    public int getID() {
        return ID;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public int getScreening_id() {
        return screening_id;
    }

    public void setScreening_id(int screening_id) {
        this.screening_id = screening_id;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }
}
