package models;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by Nadim Dahdouli on 2017-03-13.
 */
public class Screening {

    private int ID;
    private Timestamp timestamp;

    private Movie movie;
    private Theater theater;

    private int availableSeats;
    private List<Seat> seatReservations;

    /**
     * Represents a cinema screening
     *
     * @param ID               screening ID
     * @param timestamp        timestamp for the screening
     * @param movie            the movie
     * @param theater          the theater this screening is available at
     * @param seatReservations list of reserved seats for this screening
     */
    public Screening(int ID, Timestamp timestamp, Movie movie, Theater theater, List<Seat> seatReservations) {
        this.ID = ID;
        this.timestamp = timestamp;
        this.movie = movie;
        this.theater = theater;
        this.seatReservations = seatReservations;
        this.availableSeats = theater.getNbrSeats() - seatReservations.size();
    }

    @Override
    public String toString() {
        return "Screening{" +
                "ID=" + ID +
                ", timestamp=" + timestamp +
                ", movie=" + movie +
                ", theater=" + theater +
                ", availableSeats=" + availableSeats +
                ", seatReservations=" + seatReservations +
                '}';
    }
}
