package models;

import java.sql.Date;
import java.util.List;

/**
 * Created by Nadim Dahdouli on 2017-03-13.
 */
public class Screening {

    private int ID;
    private Date timestamp;

    private Movie movie;
    private Theater theater;

    private List<Seat> seatReservations;

    public Screening(int ID, Date timestamp, Movie movie, Theater theater, List<Seat> seatReservations) {
        this.ID = ID;
        this.timestamp = timestamp;
        this.movie = movie;
        this.theater = theater;
        this.seatReservations = seatReservations;
    }
}
