package models;

/**
 * Created by Nadim Dahdouli on 2017-03-13.
 */
public class Seat {

    private int ID;
    private int row;
    private int number;

    /**
     * Represents a seat in a theater
     *
     * @param ID     seat ID
     * @param row    seat row
     * @param number seat number
     */
    public Seat(int ID, int row, int number) {
        this.ID = ID;
        this.row = row;
        this.number = number;
    }

    public int getID() {
        return ID;
    }

    public int getRow() {
        return row;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Seat))
            return false;

        return getID() == ((Seat)o).getID();
    }

    @Override
    public String toString() {
        return "Seat{" +
                "ID=" + ID +
                ", row=" + row +
                ", number=" + number +
                '}';
    }
}
