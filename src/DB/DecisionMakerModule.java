package db;

import jdk.nashorn.internal.runtime.ECMAException;
import models.Movie;
import models.Screening;
import models.Theater;
import models.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Nadim Dahdouli on 2017-03-13.
 */
@SuppressWarnings("Duplicates")
public class DecisionMakerModule extends UserModule {


    public boolean registerUser(User user) {

        // FIXME: Hash password
        // FIXME: Check if current user is admin


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

        // FIXME: Check if current user is admin

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

        // FIXME: Check if current user is admin

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

    public boolean addMovie(Movie movie) {

        String sql = "INSERT INTO movie (title, price, runtime, agelimit) VALUES (?, ?, ?, ?)";

        try {

            stmt = conn.prepareStatement(sql);

            stmt.setString(1, movie.getTitle());
            stmt.setInt(2, movie.getPrice());
            stmt.setInt(3, movie.getRuntime());
            stmt.setInt(4, movie.getAgelimit());

            return stmt.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

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

            stmt.executeBatch();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public static void main(String[] args) {

        DecisionMakerModule makerModule = new DecisionMakerModule();

        //System.out.println(makerModule.addMovie(new Movie("Batman 2", 100, 120, 16)));


    }

}
