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
        this(timestamp, movie, theater, seatReservations);
        this.ID = ID;
    }

    public Screening(Timestamp timestamp, Movie movie, Theater theater, List<Seat> seatReservations) {
        this.timestamp = timestamp;
        this.movie = movie;
        this.theater = theater;
        this.seatReservations = seatReservations;
        if (seatReservations != null)
            this.availableSeats = theater.getNbrSeats() - seatReservations.size();
        else
            this.availableSeats = theater.getNbrSeats();
    }

    public int getID() {
        return ID;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public Movie getMovie() {
        return movie;
    }

    public Theater getTheater() {
        return theater;
    }

    public static Screening createScreening(Timestamp timestamp, String title, int price) {
        return null;
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
