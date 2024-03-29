package db;

import models.Movie;
import models.Screening;
import models.Theater;
import models.User;
import org.omg.CORBA.INTERNAL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * @author Nadim Dahdouli
 */
@SuppressWarnings("Duplicates")
public class DecisionMakerModule extends UserModule {


    public boolean registerUser(User user) {

        // FIXME: Hash password
        // FIXME: Check if current user is admin

        if (!approveUsername(user.getUsername()) || !approvePassword(user.getPassword()))
            return false;

        String sql = "INSERT INTO user (username, password, admin) VALUES(?, ?, ?)";

        try {
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setBoolean(3, user.isAdmin());

            return stmt.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

    public boolean editUser(User user) {

        String sql = "UPDATE user SET username=?, password=?, admin=? WHERE ID=?";

        try {

            stmt = conn.prepareStatement(sql);

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setBoolean(3, user.isAdmin());
            stmt.setInt(4, user.getID());

            return stmt.executeUpdate() == 1;


        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteUser(int ID) {

        String sql = "DELETE FROM user WHERE ID=?";

        try {

            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, ID);

            return stmt.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public User getUser(String username) {
        String sql = "SELECT * FROM user WHERE username=?";

        try {

            stmt = conn.prepareStatement(sql);

            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            if (rs.first())
                return new User(rs.getInt("ID"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("admin").equals("admin"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public int addMovie(Movie movie) {
        if (!approveMovie(movie))
            return -1;
        
        String sql = "INSERT INTO movie (title, price, runtime, agelimit) VALUES (?, ?, ?, ?)";

        try {

            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, movie.getTitle());
            stmt.setInt(2, movie.getPrice());
            stmt.setInt(3, movie.getRuntime());
            stmt.setInt(4, movie.getAgelimit());

            stmt.execute();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.first())
                return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public boolean approveMovie(Movie movie) {
        if (movie.getPrice() < 0)
            return false;
        if (movie.getRuntime() < 1)
            return false;
        if (movie.getTitle() == null || movie.getTitle().length() == 0)
            return false;

        switch (movie.getAgelimit()) {
            case 0:
            case 7:
            case 11:
            case 15:
                break;
            default:
                return false;
        }

        return true;
    }

    public boolean deleteMovie(int ID) {

        String sql = "DELETE FROM movie WHERE ID=?";

        try {

            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, ID);

            return stmt.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean editMovie(Movie movie) {

        String sql = "UPDATE movie SET title=?, price=?, runtime=?, agelimit=? WHERE ID=?";

        try {

            stmt = conn.prepareStatement(sql);

            stmt.setString(1, movie.getTitle());
            stmt.setInt(2, movie.getPrice());
            stmt.setInt(3, movie.getRuntime());
            stmt.setInt(4, movie.getAgelimit());
            stmt.setInt(5, movie.getID());

            return stmt.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean addScreenings(List<Screening> screenings) {
        for (Screening screening : screenings)
            if (!approveScreeningData(screening))
                return false;

        try {
            String sql = "INSERT INTO screening (timestamp, theater_id, movie_id) VALUES(?, ?, ?)";
            stmt = conn.prepareStatement(sql);

            screenings.forEach(screening -> {

                try {
                    stmt.setTimestamp(1, screening.getTimestamp());
                    stmt.setInt(2, screening.getTheater().getID());
                    stmt.setInt(3, screening.getMovie().getID());

                    stmt.addBatch();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            return stmt.executeBatch().length >= 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean editScreenings(List<Screening> screenings) {

        try {
            String sql = "UPDATE screening SET timestamp=?, theater_id=?, movie_id=? WHERE ID=?";
            stmt = conn.prepareStatement(sql);

            screenings.forEach(screening -> {
                try {
                    stmt.setTimestamp(1, screening.getTimestamp());
                    stmt.setInt(2, screening.getTheater().getID());
                    stmt.setInt(3, screening.getMovie().getID());
                    stmt.setInt(4, screening.getID());

                    stmt.addBatch();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            return stmt.executeBatch().length >= 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteScreenings(List<Screening> screenings) {

        try {
            String sql = "DELETE FROM screening WHERE ID=?";
            stmt = conn.prepareStatement(sql);

            screenings.forEach(screening -> {
                try {
                    stmt.setInt(1, screening.getID());

                    stmt.addBatch();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            return stmt.executeBatch().length >= 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public Screening getScreening(int id) {
        try {
            String sql = "SELECT * FROM reservation WHERE ID=?";

            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

            if(rs.first())
                return new Screening(rs.getInt("ID"), rs.getTimestamp("timestamp"), getMovie( rs.getInt("movie_id")), getTheater(rs.getInt("theater_id")), null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void addTheater(String name, int seats) {
        String sql = "INSERT INTO theater(name, seats) VALUES(?, ?)";

        try {
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, name);
            stmt.setInt(2, seats);

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();

            if (!rs.first())
                return;

            int theaterID = rs.getInt(1);

            sql = "INSERT INTO seat(row, number) VALUES(?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < seats; i++) {
                stmt.setInt(1, (i / 20) + 1);
                stmt.setInt(2, (i % 20) + 1);

                stmt.addBatch();
            }

            stmt.executeBatch();

            rs = stmt.getGeneratedKeys();
            sql = "INSERT INTO theater_seats(theater_id, seat_id) VALUES(?, ?)";
            stmt = conn.prepareStatement(sql);
            while (rs.next()) {
                stmt.setInt(1, theaterID);
                stmt.setInt(2, rs.getInt(1));

                stmt.addBatch();
            }

            stmt.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean getTheater(String name) {
        try {
            String sql = "SELECT * FROM theater WHERE name=?";
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, name);

            return stmt.executeQuery().first();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public Theater getTheater(int id) {
        try {
            String sql = "SELECT * FROM theater WHERE ID=?";
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

            if(rs.first())
                return new Theater(rs.getInt("ID"), rs.getString("name"), rs.getInt("seats"), null);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean approveUsername(String username) {
        if (username == null || username.length() < 3)
            return false;

        String sql = "SELECT * FROM user WHERE username=?";

        try {

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            if (rs.first())
                return false;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public boolean approvePassword(String password) {
        if (password == null || password.length() < 6)
            return false;
        return true;
    }

    public boolean approveScreeningData(Screening screening) {
        if (screening.getMovie() == null && getMovie(screening.getMovie().getTitle()))
            return false;

        if (screening.getTheater() == null || getTheater(screening.getTheater().getID()) == null)
            return false;

        if (screening.getTimestamp() == null || screening.getTimestamp().before(new Timestamp(new Date().getTime())))
            return false;

        return true;
    }

    public static void main(String[] args) {

        DecisionMakerModule makerModule = new DecisionMakerModule();

        System.out.println(makerModule.addMovie(new Movie("Batman 2", 100, 120, 16)));

    }

}
