package models;

import java.util.List;

/**
 * Created by Nadim Dahdouli on 2017-03-13.
 */
public class Theater {

    private int ID;
    private String name;
    private int nbrSeats;

    private List<Seat> seats;

    /**
     * Represents a theater at a cinema
     *
     * @param ID       theater ID
     * @param name     human readable name for the theater
     * @param nbrSeats total number of seats in this theater
     * @param seats    list of seats for this theater
     */
    public Theater(int ID, String name, int nbrSeats, List<Seat> seats) {
        this.ID = ID;
        this.name = name;
        this.nbrSeats = nbrSeats;
        this.seats = seats;
    }

    /**
     * The total number of seats in this theater
     *
     * @return number of seats
     */
    public int getNbrSeats() {
        return nbrSeats;
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    @Override
    public String toString() {
        return "Theater{" +
                "ID=" + ID +
                ", name='" + name + '\'' +
                ", nbrSeats=" + nbrSeats +
                ", seats=" + seats +
                '}';
    }
}
