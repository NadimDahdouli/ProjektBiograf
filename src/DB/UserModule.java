package DB;

import java.sql.*;

/**
 * Created by Nadim Dahdouli on 2017-03-13.
 */
public class UserModule {

    ConnectionHandler connectionHandler;

    public UserModule() {

        connectionHandler = new ConnectionHandler("localhost", "biograf", "root", "");

    }

    public void search(String term) {




    }


    public void getMovie() {

    }

    public static void main(String[] args) {
        new UserModule();
    }

}
