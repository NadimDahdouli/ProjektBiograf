import db.ConnectionHandler;
import db.DecisionMakerModule;
import db.UserModule;
import models.Customer;
import models.Seat;
import models.User;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Hampus on 2017-03-13.
 */
public class DBTests {
    boolean registerUser(String username, String password) {
        User user = new User(username, password, false);
        return new DecisionMakerModule().registerUser(user);
    }

    boolean registerAdmin(String username, String password) {
        User user = new User(username, password, true);
        return new DecisionMakerModule().registerUser(user);
    }

    boolean registerCustomer(String email, String firstname, String phonenumber, String debitcard) {
        Customer customer = new Customer(email, firstname, phonenumber, debitcard);
        return new DecisionMakerModule().addCustomer(customer);
    }

    boolean changeCustomer(String email, String firstname, String phonenumber, String debitcard) {
        UserModule um = new UserModule();
        Customer customer = new Customer("myvalid@testemail.here", "testname", "123123123", "123123123123");
        assertTrue(um.addCustomer(customer));

        Customer customer2 = um.getCustomer(customer.getEmail());
        assertNotNull(customer2);

        customer.setID(customer2.getID());

        customer2.setEmail(email);
        customer2.setFirstname(firstname);
        customer2.setPhonenumber(phonenumber);
        customer2.setDebitcard(debitcard);
        boolean success = um.updateCustomer(customer2);

        // Clean up db
        if (success) {
            assertTrue(um.deleteCustomer(customer2));
            assertNull(um.getCustomer(customer2.getEmail()));
        } else {
            assertTrue(um.deleteCustomer(customer));
            assertNull(um.getCustomer(customer.getEmail()));
        }

        return success;
    }

    int createDummyReservation(int screening_id, int customer_id, int seats) {
        UserModule um = new UserModule();
        List<Seat> list = um.getAvailableSeatsForScreening(2);
        List<Seat> list2 = new LinkedList<>();

        for (int i = 0; i < (seats > list.size() ? list.size() : seats); i++)
            list2.add(list.get(i));

        return um.createReservation(screening_id, customer_id, list2);
    }

    boolean changeReservation(int screening_id, int customer_id, int seats) {
        UserModule um = new UserModule();

        List<Seat> list = um.getAvailableSeatsForScreening(2);
        List<Seat> list2 = new LinkedList<>();

        for (int i = 0; i < (seats > list.size() ? list.size() : seats); i++)
            list2.add(list.get(i));

        int id = um.createReservation(2, 2, list2);
        boolean success = um.changeReservation(id, screening_id, customer_id, list2);

        um.deleteReservation(id);

        return success;
    }

    @Test
    public void testDBConnection() {
        ConnectionHandler connectionHandler = new ConnectionHandler("nadimdahdouli.me", "biograf", "biograf", "biograf");
        assertNotNull(connectionHandler.getConn());
    }

    @Test
    public void testCreateUserEmpty() {
        assertFalse(registerUser(null, null));
        assertFalse(registerUser("", ""));
    }

    @Test
    public void testCreateUserEmptyUsername() {
        assertFalse(registerUser(null, "testpassword"));
        assertFalse(registerUser("", "testpassword"));
    }

    @Test
    public void testCreateUserEmptyPassword() {
        assertFalse(registerUser("testuser", null));
        assertFalse(registerUser("testuser", ""));
    }

    @Test
    public void testCreateUserInvalidData() {
        assertFalse(registerUser(null, null));
    }

    @Test
    public void testCreateUserInvalidUsername() {
        assertFalse(registerUser("A", "testpassword"));
    }

    @Test
    public void testCreateUserTakenUsername() {
        assertFalse(registerUser("myuser", "testpassword"));
    }

    @Test
    public void testCreateUserInvalidPassword() {
        assertFalse(registerUser("testuser", "123"));
    }

    @Test
    public void testCreateUserValidData() {
        assertTrue(registerUser("testuser", "testpassword"));

        // Clean up changes in db
        DecisionMakerModule dmm = new DecisionMakerModule();
        User user = dmm.getUser("testuser");
        assertNotNull(user);
        assertTrue(dmm.deleteUser(user.getID()));
    }

    @Test
    public void testCreateReservationEmptySeatID() {
        UserModule um = new UserModule();
        List<Seat> list = new LinkedList<>();
        list.add(new Seat(-1, 0, 0));

        int id = um.createReservation(2, 2, list);
        assertTrue(id < 0);
    }

    @Test
    public void testCreateReservationInvalidShowID() {
        int id = createDummyReservation(-1, 2, 0);
        assertTrue(id < 0);
    }

    @Test
    public void testCreateReservationInvalidCustomerID() {
        int id = createDummyReservation(2, -1, 0);
        assertTrue(id < 0);
    }

    @Test
    public void testCreateReservationInvalidSeat() {
        int id = createDummyReservation(2, 2, 0);
        assertTrue(id < 0);
    }

    @Test
    public void testCreateReservationValidData() {
        int id = createDummyReservation(2, 2, 5);
        assertTrue(id > 0);

        // Clean up db
        UserModule um = new UserModule();
        assertTrue(um.deleteReservation(id));
    }

    @Test
    public void testCreateCustomerInvalidEmail() {
        assertFalse(registerCustomer("invalidemail", "hej", "123123123123", "7514267389123"));
    }

    @Test
    public void testCreateCustomerInvalidFirstname() {
        assertFalse(registerCustomer("valid@email.here", "h", "123123123123", "7514267389123"));
    }

    @Test
    public void testCreateCustomerInvalidPhonenumber() {
        assertFalse(registerCustomer("valid@email.here", "name", "asda6875asd", "7514267389123"));
    }

    @Test
    public void testCreateCustomerInvalidCreditcardNumber() {
        assertFalse(registerCustomer("valid@email.here", "name", "123123123123", "asdas123123"));
    }

    @Test
    public void testCreateCustomerEmptyEmail() {
        assertFalse(registerCustomer(null, "name", "123123123123", "7514267389123"));
        assertFalse(registerCustomer("", "name", "123123123123", "7514267389123"));
    }

    @Test
    public void testCreateCustomerEmptyFirstname() {
        assertFalse(registerCustomer("valid@email.here", null, "123123123123", "7514267389123"));
        assertFalse(registerCustomer("valid@email.here", "", "123123123123", "7514267389123"));
    }

    @Test
    public void testCreateCustomerEmptyPhonenumber() {
        assertFalse(registerCustomer("valid@email.here", "name", null, "7514267389123"));
        assertFalse(registerCustomer("valid@email.here", "name", "", "7514267389123"));
    }

    @Test
    public void testCreateCustomerValidData() {
        assertTrue(registerCustomer("valid@email.here", "name", "123123123123", "7514267389123"));

        // Clean up db
        UserModule um = new UserModule();
        Customer customer = um.getCustomer("valid@email.here");
        assertNotNull(customer);
        assertTrue(um.deleteCustomer(customer));
    }

    @Test
    public void testRemoveReservationInvalidReservationID() {
        UserModule um = new UserModule();
        assertFalse(um.deleteReservation(-1));
    }

    @Test
    public void testRemoveReservationValidData() {
        UserModule um = new UserModule();
        int id = createDummyReservation(2, 2, 5);
        assertTrue(id > 0);
        assertNotNull(um.searchReservation(id));
        um.deleteReservation(id);
        assertNull(um.searchReservation(id));
    }

    @Test
    public void testChangeCustomerInvalidEmail() {
        assertFalse(changeCustomer("notavalidemail.here", "validname", "123123123", "123123123132"));
    }

    @Test
    public void testChangeCustomerInvalidUsername() {
        assertFalse(changeCustomer("stillvalid@email.here", "a", "123123123", "123123123132"));
    }

    @Test
    public void testChangeCustomerEmptyUsername() {
        assertFalse(changeCustomer("stillvalid@email.here", null, "123123123", "123123123132"));
        assertFalse(changeCustomer("stillvalid@email.here", "", "123123123", "123123123132"));
    }

    @Test
    public void testChangeCustomerEmptyEmail() {
        assertFalse(changeCustomer(null, "validname", "123123123", "123123123132"));
        assertFalse(changeCustomer("", "validname", "123123123", "123123123132"));
    }

    @Test
    public void testChangeCustomerEmptyPhonenumber() {
        assertFalse(changeCustomer("stillvalid@email.here", "validname", null, "123123123132"));
        assertFalse(changeCustomer("stillvalid@email.here", "validname", "", "123123123132"));
    }

    @Test
    public void testChangeCustomerInvalidPhonenumber() {
        assertFalse(changeCustomer("stillvalid@email.here", "validname", "tyiu123", "123123123132"));
    }

    @Test
    public void testChangeCustomerInvalidCreditcardNumber() {
        assertFalse(changeCustomer("stillvalid@email.here", "validname", "123123123", "asda68asd"));
    }

    @Test
    public void testChangeCustomerValidData() {
        assertTrue(changeCustomer("stillvalid@email.here", "validname", "123123123", "123123123132"));
    }

    @Test
    public void testSearchReservationInvalidReservationID() {
        assertNull(new UserModule().searchReservation(-1));
    }

    @Test
    public void testSearchReservationValidData() {
        assertNotNull(new UserModule().searchReservation(1));
    }

    @Test
    public void testAddMovieScheduleEmptyTitle() {
        assertTrue(false);
    }

    @Test
    public void testAddMovieScheduleInvalidTitle() {
        assertTrue(false);
    }

    @Test
    public void testAddMovieScheduleInvalidPrice() {
        assertTrue(false);
    }

    @Test
    public void testAddMovieScheduleInvalidLength() {
        assertTrue(false);
    }

    @Test
    public void testAddMovieScheduleEmptyAgeLimit() {
        assertTrue(false);
    }

    @Test
    public void testAddMovieScheduleInvalidAgeLimit() {
        assertTrue(false);
    }

    @Test
    public void testAddMovieScheduleEmptyTime() {
        assertTrue(false);
    }

    @Test
    public void testAddMovieScheduleInvalidTime() {
        assertTrue(false);
    }

    @Test
    public void testAddMovieScheduleInvalidRoomID() {
        assertTrue(false);
    }

    @Test
    public void testAddMovieScheduleValidData() {
        assertTrue(false);
    }

    @Test
    public void testCreateAdminUserInvalidUsername() {
        assertFalse(registerAdmin("nv", "myadminpassword"));
    }

    @Test
    public void testCreateAdminUserEmptyUsername() {
        assertFalse(registerAdmin(null, "myadminpassword"));
        assertFalse(registerAdmin("", "myadminpassword"));
    }

    @Test
    public void testCreateAdminUserInvalidPassword() {
        assertFalse(registerAdmin("iamadmin", "123"));
    }

    @Test
    public void testCreateAdminUserEmptyPassword() {
        assertFalse(registerAdmin("iamadmin", null));
        assertFalse(registerAdmin("iamadmin", ""));
    }

    @Test
    public void testCreateAdminUserValidData() {
        assertTrue(registerAdmin("iamadmin", "myadminpassword"));

        // Clean up changes in db
        DecisionMakerModule dmm = new DecisionMakerModule();
        User user = dmm.getUser("iamadmin");
        assertNotNull(user);
        assertTrue(dmm.deleteUser(user.getID()));
    }

    @Test
    public void testChangeReservationInvalidShowID() {
        assertFalse(changeReservation(-1, 2, 5));
    }

    @Test
    public void testChangeReservationInvalidCustomerID() {
        assertFalse(changeReservation(2, -1, 5));
    }

    @Test
    public void testChangeReservationInvalidSeats() {
        UserModule um = new UserModule();

        List<Seat> list = um.getAvailableSeatsForScreening(2);
        List<Seat> list2 = new LinkedList<>();

        for (int i = 0; i < (5 > list.size() ? list.size() : 5); i++)
            list2.add(list.get(i));

        int id = um.createReservation(2, 2, list2);
        assertTrue(um.getReservationSeats(id).size() == 5);

        list2.clear();

        assertFalse(um.changeReservation(id, 2, 2, list2));

        list2.add(new Seat(-1, 0, 0));
        assertFalse(um.changeReservation(id, 2, 2, list2));

        um.deleteReservation(id);
    }

    @Test
    public void testChangeReservationValidData() {
        UserModule um = new UserModule();

        List<Seat> list = um.getAvailableSeatsForScreening(2);
        List<Seat> list2 = new LinkedList<>();

        for (int i = 0; i < (5 > list.size() ? list.size() : 5); i++)
            list2.add(list.get(i));

        int id = um.createReservation(2, 2, list2);
        assertTrue(um.getReservationSeats(id).size() == 5);

        list2.clear();
        for (int i = 0; i < (10 > list.size() ? list.size() : 10); i++)
            list2.add(list.get(i));

        um.changeReservation(id, 2, 2, list2);

        assertTrue(um.getReservationSeats(id).size() == 10);

        um.deleteReservation(id);
    }

    @Test
    public void testSeeEmptySeatsInvalidID() {
        UserModule um = new UserModule();
        assertTrue(um.getSeatsForScreening(-1).size() == 0);
    }

    @Test
    public void testSeeEmptySeatsValidID() {
        UserModule um = new UserModule();
        assertTrue(um.getSeatsForScreening(2).size() > 0);
    }
}
