package DB;

import com.sun.istack.internal.Nullable;

/**
 * Created by Nadim Dahdouli on 2017-03-13.
 */
public class UserModule {

    ConnectionHandler connectionHandler;

    public UserModule() {

        connectionHandler = new ConnectionHandler("localhost", "biograf", "root", "");

    }

    public void search(String s) {
        
    }


    public void getMovie() {

    }

    public static void main(String[] args) {
        new UserModule();
    }

}
