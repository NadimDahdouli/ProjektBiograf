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

    public Theater(int ID, String name, int nbrSeats, List<Seat> seats) {
        this.ID = ID;
        this.name = name;
        this.nbrSeats = nbrSeats;
        this.seats = seats;
    }

    public int getNbrSeats() {
        return nbrSeats;
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
