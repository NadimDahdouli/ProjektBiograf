package DB;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by Nadim Dahdouli on 2017-03-13.
 */
public class ConnectionHandler {

    private Connection connection = null;

    public ConnectionHandler(String host, String dbName, String username, String password) {
        try {

            Class.forName("com.mysql.jdbc.Driver").newInstance();

            String url = String.format("jdbc:mysql://%s/%s", host, dbName);
            connection = DriverManager.getConnection(url, username, password);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConn() {
        return connection;
    }

}
