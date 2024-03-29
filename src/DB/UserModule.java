package db;

import models.*;

import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Nadim Dahdouli
 * @author Johan Held
 */
@SuppressWarnings("Duplicates")
public class UserModule {

    protected Connection conn;
    protected PreparedStatement stmt;

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

    public boolean getMovie(String title) {
        try {
            String sql = "SELECT * FROM movie WHERE title=?";
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, title);

            return stmt.executeQuery().first();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<Screening> getScreenings(Movie movie) {
        List<Screening> screenings = new ArrayList<>();

        String sql = "SELECT ID, timestamp, theater_id FROM screening WHERE movie_id=?";

        try {

            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, movie.getID());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                screenings.add(new Screening(
                        rs.getInt("ID"),
                        rs.getTimestamp("timestamp"),
                        movie,
                        getTheater(rs.getInt("theater_id")),
                        getSeatsForScreening(rs.getInt("ID"))
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return screenings;
    }

    /**
     * Cinema schedule from today's date to a week ahead. The returned result is a list of {@link Screening}s which contains all the relevant information such as screening time, {@link Movie} info, {@link Theater} and available {@link Seat}s
     *
     * @return list of screenings
     */
    public List<Screening> getSchedule() {
        List<Screening> screenings = new ArrayList<>();

        Timestamp now;
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        now = new Timestamp(calendar.getTimeInMillis());

        Timestamp future;
        calendar = new GregorianCalendar();
        calendar.add(Calendar.DATE, 7);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);


        future = new Timestamp(calendar.getTimeInMillis());


        String sql = "SELECT screening.* " +
                "FROM screening " +
                "JOIN movie ON screening.movie_id=movie.ID " +
                "JOIN theater ON screening.theater_id=theater.ID " +
                "WHERE screening.timestamp >= ? AND screening.timestamp <= ? " +
                "ORDER BY screening.timestamp ASC";

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setTimestamp(1, now);
            stmt.setTimestamp(2, future);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int screening_id = rs.getInt("ID");
                Timestamp screening_timestamp = rs.getTimestamp("timestamp");

                Movie movie = getMovie(rs.getInt("movie_id"));

                Theater theater = getTheater(rs.getInt("theater_id"));


                Screening screening = new Screening(
                        screening_id,
                        screening_timestamp,
                        movie,
                        theater,
                        getSeatsForScreening(screening_id)
                );

                screenings.add(screening);

                System.out.println(screening + "\n");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return screenings;
    }

    public Theater getTheater(int ID) {
        Theater theater = null;

        String sql = "SELECT " +
                "theater.name, COUNT(seat.ID) as seats " +
                "FROM theater " +
                "JOIN theater_seats ON theater.ID = theater_seats.theater_id " +
                "JOIN seat ON theater_seats.seat_id = seat.ID " +
                "WHERE theater.ID=?";

        try {
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, ID);

            ResultSet rs = stmt.executeQuery();

            if (rs.first()) {
                theater = new Theater(
                        ID,
                        rs.getString("name"),
                        rs.getInt("seats"),
                        getSeatsForTheater(ID)
                );
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        return theater;
    }

    public List<Seat> getSeatsForScreening(int screening_id) {
        List<Seat> seats = new ArrayList<>();

        String sql = "SELECT " +
                "seat.* " +
                "FROM seat_reservation " +
                "JOIN seat ON seat_reservation.seat_id = seat.ID " +
                "WHERE seat_reservation.screening_id = ?";

        try {
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, screening_id);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                seats.add(new Seat(
                        rs.getInt("ID"),
                        rs.getInt("row"),
                        rs.getInt("number")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return seats;
    }

    public List<Seat> getAvailableSeatsForScreening(int screening_id) {
        List<Seat> seats = new ArrayList<>();

        String sql = "SELECT * FROM seat WHERE ID IN (SELECT seat_id FROM theater_seats JOIN screening ON theater_seats.theater_id=screening.theater_id JOIN seat WHERE screening.ID=? AND seat_id NOT IN (SELECT seat_id FROM seat_reservation JOIN screening ON seat_reservation.screening_id=screening.ID WHERE screening.ID=?))";

        try {
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, screening_id);
            stmt.setInt(2, screening_id);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                seats.add(new Seat(
                        rs.getInt("ID"),
                        rs.getInt("row"),
                        rs.getInt("number")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return seats;
    }

    public List<Seat> getReservationSeats(int reservationID) {
        List<Seat> seats = new ArrayList<>();

        String sql = "SELECT * FROM seat JOIN seat_reservation ON seat.ID=seat_reservation.seat_id WHERE seat_reservation.reservation_id=?";

        try {
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, reservationID);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                seats.add(new Seat(
                        rs.getInt("ID"),
                        rs.getInt("row"),
                        rs.getInt("number")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return seats;
    }

    private List<Seat> getSeatsForTheater(int theater_id) {
        List<Seat> seats = new ArrayList<>();


        String sql = "SELECT " +
                "seat.ID, seat.row, seat.number " +
                "FROM theater " +
                "JOIN theater_seats ON theater.ID = theater_seats.theater_id " +
                "JOIN seat ON theater_seats.seat_id = seat.ID " +
                "WHERE theater.ID = ?";

        try {
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, theater_id);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                seats.add(new Seat(
                        rs.getInt("ID"),
                        rs.getInt("row"),
                        rs.getInt("number")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return seats;
    }

    public boolean addCustomer(Customer customer) {
        if (!approveCustomerData(customer))
            return false;

        String sql = "INSERT INTO customer (email, firstname, phonenumber, debitcard) VALUES(?, ?, ?, ?)";

        try {
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, customer.getEmail());
            stmt.setString(2, customer.getFirstname());
            stmt.setString(3, customer.getPhonenumber());
            stmt.setString(4, customer.getDebitcard());

            return stmt.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteCustomer(Customer customer) {

        String sql = "DELETE FROM customer WHERE ID=?";

        try {
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, customer.getID());

            return stmt.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateCustomer(Customer customer) {
        if (!approveCustomerData(customer))
            return false;

        String sql = "UPDATE customer SET email=?, firstname=?, phonenumber=?, debitcard=? WHERE ID=?";

        try {
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, customer.getEmail());
            stmt.setString(2, customer.getFirstname());
            stmt.setString(3, customer.getPhonenumber());
            stmt.setString(4, customer.getDebitcard());
            stmt.setInt(5, customer.getID());

            return stmt.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public Customer getCustomer(String email) {
        String sql = "SELECT * FROM customer WHERE email=?";

        try {
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            if (rs.first())
                return new Customer(rs.getInt("ID"),
                        rs.getString("email"),
                        rs.getString("firstname"),
                        rs.getString("phonenumber"),
                        rs.getString("debitcard"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Customer getCustomer(int id) {
        String sql = "SELECT * FROM customer WHERE ID=?";

        try {
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.first())
                return new Customer(rs.getInt("ID"),
                        rs.getString("email"),
                        rs.getString("firstname"),
                        rs.getString("phonenumber"),
                        rs.getString("debitcard"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean approveCustomerData(Customer customer) {
        if (customer.getEmail() == null)
            return false;

        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(customer.getEmail());
        if (!matcher.matches())
            return false;

        String sql = "SELECT * FROM customer WHERE email=?";

        try {
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, customer.getEmail());

            ResultSet rs = stmt.executeQuery();
            if (rs.first())
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (customer.getFirstname() == null || customer.getFirstname().length() < 2)
            return false;

        if (customer.getPhonenumber() == null || customer.getPhonenumber().length() < 5)
            return false;

        for (char c : customer.getPhonenumber().toCharArray())
            if (c < '0' || c > '9')
                return false;

        if (customer.getDebitcard() != null || customer.getDebitcard().length() > 0){
            for (char c : customer.getDebitcard().toCharArray())
                if (c < '0' || c > '9')
                    return false;
        }

        return true;
    }

    public Reservation searchReservation(int id) {
        String sql = "SELECT * FROM reservation WHERE ID=?";

        try {
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.first())
                return new Reservation(rs.getInt("ID"),
                        rs.getBoolean("paid"),
                        rs.getInt("screening_id"),
                        rs.getInt("customer_id"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public int createReservation(int screeningID, int customerID, List<Seat> seats) {
        try {
            String sql = "SELECT * FROM screening WHERE ID=?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, screeningID);
            ResultSet rs = stmt.executeQuery();
            if (!rs.first())
                return -1;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (getCustomer(customerID) == null)
            return -1;

        if (seats.size() == 0)
            return -1;

        List<Seat> availableSeats = getAvailableSeatsForScreening(screeningID);

        if (availableSeats.size() < seats.size())
            return -1;

        for (Seat seat : seats) {
            if (!availableSeats.contains(seat))
                return -1;
        }

        try {
            String sql = "INSERT INTO reservation (screening_id, customer_id) VALUES (?, ?)";

            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, screeningID);
            stmt.setInt(2, customerID);

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.first()) {
                int reservationID = rs.getInt(1);

                sql = "INSERT INTO seat_reservation (seat_id, reservation_id, screening_id) VALUES (?, ?, ?)";

                stmt = conn.prepareStatement(sql);

                for (Seat seat : seats) {
                    stmt.setInt(1, seat.getID());
                    stmt.setInt(2, reservationID);
                    stmt.setInt(3, screeningID);

                    stmt.addBatch();
                }

                if(stmt.executeBatch().length == seats.size())
                    return reservationID;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public boolean deleteReservation(int reservationID) {
        if (searchReservation(reservationID) == null)
            return false;

        try {
            String sql = "DELETE FROM reservation WHERE ID=?";

            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, reservationID);

            if (stmt.executeUpdate() == 1) {
                sql = "DELETE FROM seat_reservation WHERE reservation_id=?";

                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, reservationID);
                stmt.execute();

                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean changeReservation(int reservationID, int screeningID, int customerID, List<Seat> seats) {
        try {
            String sql = "SELECT * FROM screening WHERE ID=?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, screeningID);
            ResultSet rs = stmt.executeQuery();
            if (!rs.first())
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (getCustomer(customerID) == null)
            return false;

        if (seats.size() == 0)
            return false;

        if (!deleteReservation(reservationID))
            return false;

        List<Seat> availableSeats = getAvailableSeatsForScreening(screeningID);

        if (availableSeats.size() < seats.size())
            return false;

        for (Seat seat : seats) {
            if (!availableSeats.contains(seat))
                return false;
        }

        try {
            String sql = "INSERT INTO reservation (ID, screening_id, customer_id) VALUES (?, ?, ?)";

            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, reservationID);
            stmt.setInt(2, screeningID);
            stmt.setInt(3, customerID);

            stmt.executeUpdate();

            sql = "INSERT INTO seat_reservation (seat_id, reservation_id, screening_id) VALUES (?, ?, ?)";

            stmt = conn.prepareStatement(sql);

            for (Seat seat : seats) {
                stmt.setInt(1, seat.getID());
                stmt.setInt(2, reservationID);
                stmt.setInt(3, screeningID);

                stmt.addBatch();
            }

            if(stmt.executeBatch().length == seats.size())
                return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void main(String[] args) {
        UserModule um = new UserModule();
    }
}
