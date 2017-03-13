package db;

import models.Movie;
import models.Screening;
import models.Seat;
import models.Theater;

import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * @author Nadim Dahdouli
 * @author Johan Held
 */
public class UserModule {

    Connection conn;
    PreparedStatement stmt;

    public UserModule() {
        conn = new ConnectionHandler("nadimdahdouli.me", "biograf", "biograf", "biograf").getConn();
    }

    /**
     * Searches the database for movies containing the search term
     *
     * @param term Search term
     * @return list of movies or empty list if the result is null
     */
    public List<Movie> search(String term) {

        List<Movie> movies = new ArrayList<>();

        String sql = "SELECT * FROM movie WHERE title LIKE ?";

        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + term + "%");

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                movies.add(new Movie(
                        rs.getInt("ID"),
                        rs.getString("title"),
                        rs.getInt("price"),
                        rs.getInt("runtime"),
                        rs.getInt("agelimit")
                ));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return movies;
    }


    /**
     * Get a specific movie by ID
     *
     * @param ID Movie ID
     * @return The movie
     */
    public Movie getMovie(int ID) {

        Movie movie = null;

        String sql = "SELECT * FROM movie WHERE ID=?";

        try {

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, ID);

            ResultSet rs = stmt.executeQuery();

            if (rs.first()) {
                movie = new Movie(
                        rs.getInt("ID"),
                        rs.getString("title"),
                        rs.getInt("price"),
                        rs.getInt("runtime"),
                        rs.getInt("agelimit")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return movie;

    }

    /**
     * Cinema schedule from today's date to a week ahead. The returned result is a list of {@link Screening}s which contains all the relevant information such as screening time, {@link Movie} info, {@link Theater} and available {@link Seat}s
     *
     * @return list of screenings
     */
    public List<Screening> getSchedule() {
        List<Screening> screenings = new ArrayList<>();

        java.util.Date dtToday = new java.util.Date();
        java.util.Date dtFuture;

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(dtToday);
        calendar.add(Calendar.DATE, 7);

        dtFuture = new Date(calendar.getTime().getTime());


        String sql = "SELECT screening.*, " +
                "movie.title, movie.price, movie.runtime, movie.agelimit, " +
                "theater.name AS theaterName, theater.seats AS theaterSeats " +
                "FROM screening " +
                "JOIN movie ON screening.movie_id=movie.ID " +
                "JOIN theater ON screening.theater_id=theater.ID " +
                "WHERE UNIX_TIMESTAMP(screening.timestamp) >= ? AND UNIX_TIMESTAMP(screening.timestamp) <= ?";

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, dtToday.getTime() / 1000);
            stmt.setLong(2, dtFuture.getTime() / 1000);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int screening_id = rs.getInt("ID");
                Timestamp screening_timestamp = rs.getTimestamp("timestamp");

                Movie movie = new Movie(
                        rs.getInt("movie_id"),
                        rs.getString("title"),
                        rs.getInt("price"),
                        rs.getInt("runtime"),
                        rs.getInt("agelimit")
                );

                Theater theater = new Theater(
                        rs.getInt("theater_id"),
                        rs.getString("theaterName"),
                        rs.getInt("theaterSeats"),
                        null
                );


                sql = "SELECT seat_reservation.seat_id, " +
                        "seat.row, seat.number " +
                        "FROM `seat_reservation` " +
                        "JOIN seat ON seat_reservation.seat_id=seat.ID " +
                        "WHERE `screening_id` = ?";

                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, screening_id);

                ResultSet rsSeatReservations = stmt.executeQuery();

                List<Seat> seatReservations = new ArrayList<>();
                while (rsSeatReservations.next()) {
                    seatReservations.add(new Seat(
                            rsSeatReservations.getInt("seat_id"),
                            rsSeatReservations.getInt("row"),
                            rsSeatReservations.getInt("number")
                    ));
                }


                Screening screening = new Screening(
                        screening_id,
                        screening_timestamp,
                        movie,
                        theater,
                        seatReservations
                );

                screenings.add(screening);

                System.out.println(screening);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        return screenings;
    }

    public static void main(String[] args) {
        new UserModule().getSchedule();
    }

}
