package db;

import models.Movie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nadim Dahdouli on 2017-03-13.
 */
public class UserModule {

    Connection conn;
    PreparedStatement stmt;

    public UserModule() {
        conn = new ConnectionHandler("localhost", "biograf", "root", "").getConn();
    }

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


    public void getMovie() {

    }

    public static void main(String[] args) {
        new UserModule();
    }

}
