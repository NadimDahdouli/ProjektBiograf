package models;

/**
 * Created by Nadim Dahdouli on 2017-03-13.
 */
public class Seat {

    private int ID;
    private int row;
    private int number;

    public Seat(int ID, int row, int number) {
        this.ID = ID;
        this.row = row;
        this.number = number;
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
