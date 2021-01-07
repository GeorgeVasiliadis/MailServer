package MailServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

/**
 * MailServer implements the essential data-structure that stores and manages all user accounts.
 *
 * @author George Vasiliadis
 * @version 7/1/21
 */
public class MailServer{

    // Hashmap stores the registered accounts
    private HashMap<String, Account> accounts;

    public MailServer() {
        super();
        accounts = new HashMap<>();
    }

    public static void main(String[] args) {

        // Ask user to provide a port number for server to run
        System.out.println("Provide Hosting Port:");
        Scanner scanner = new Scanner(System.in);

        // Start initialize listening port
        ServerSocket ss = null;
        try {
            int port = scanner.nextInt();
            ss = new ServerSocket(port);
        } catch (Exception e) {
            System.out.println("Invalid or unavailable port");
            System.exit(1);
        }

        // Initialize MailServer and populate it
        MailServer server = new MailServer();
        System.out.println("MailServer status: running...");
        server.populateAccounts();

        // Start listening for client connections
        Socket socket;
        while(true) {
            try {
                socket = ss.accept();
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                Thread thread = new Session(server, dis, dos);
                thread.start();
            } catch (Exception e) {
                System.out.println("A fatal error occurred.");
                System.exit(1);
            }
        }
    }

    /**
     * Used to populate accounts with some demo users and some demo e-mails.
     */
    private void populateAccounts(){
        // Create demo users
        register("george", "111");
        register("donut", "222");
        register("admin", "hardpassword");

        // Send e-mails
        newEmail("george", "donut", "Spam", "Hey there!\nThis is spam!\n");
        newEmail("george", "admin", "Spam", "Hey there!\nThis is spam!\n");
        newEmail("george", "george", "Spam", "Hey there!\nThis is spam!\n");

        newEmail("donut", "george", "foo", "bar\n");
        newEmail("donut", "george", "baz", "fizz\n");
        newEmail("donut", "admin", "fuzz", "Lorem ipsum and stuff...\n");

        newEmail("admin", "donut", "!Important!", "I hope to pass Signals " +
                "and Systems this semester!\nI also hope to pass Digital Communications...\nThat's all folks!\n" +
                "Yours sincerely,\nGeorge Vasiliadis 3466");
        newEmail("admin", "donut", "DO NOT OPEN", "Gotcha!\n:P :P :P :P\n");
        newEmail("admin", "admin", "Passwords", "No passwords in here, lol\n");


        // Super-useless operation to extinguish some warnings
        Account dummy = accounts.get("george");
        if(!dummy.getUsername().equals("george")){
            System.out.println("I hate warnings!");
        }
    }

    /**
     * Registers a new user to the system.
     * A user can't be registered twice using the same name.
     * @param username the uniq name of new user.
     * @param password the password of new user.
     * @return true if user was registered successfully.
     */
    synchronized boolean register(String username, String password){
        if(!accounts.containsKey(username)) {
            accounts.put(username, new Account(username, password));
            return true;
        }
        return false;
    }

    /** Checks whether a user could log in with provided credentials.
     * In order for a user to successfully login, he/she must have a valid (registered) username, and a password that
     * matches the stored one.
     * @param username the user who wants to log in.
     * @param password the password of the user that wants to log in.
     * @return true if user's credentials are correct and user can log in to the system.
     */
    boolean login(String username, String password){
        if(accounts.get(username) != null){
            return password.equals(accounts.get(username).getPassword());
        }
        return false;
    }

    /**
     * Creates a new e-mail and forwards it to desired receiver.
     * @param sender who sends the e-mail.
     * @param receiver who receives the e-mail. Should be a valid (registered) account.
     * @param subject one line of subject
     * @param mainbody multi-line body of e-mail.
     * @return true if e-mail was sent successfully to appropriate receiver.
     */
    synchronized boolean newEmail(String sender, String receiver, String subject, String mainbody){
        if(accounts.containsKey(sender) && accounts.containsKey(receiver)){
            Email email = new Email(sender, receiver, subject, mainbody);
            return accounts.get(receiver).submitEmail(email);
        }
        return false;
    }

    /**
     * Creates a one-string-representation of users mailbox and returns it.
     * The representation contains the status of e-mails (seen/ unseen), their IDs, the senders and their subjects.
     * @param username the user who wants to get his mailbox represented.
     * @return a string representing the user's current mailbox. If user-client does not exist a special String is returned
     * instead.
     */
    String showEmails(String username) {
        String str = "User " + username + " is not valid";
        if(accounts.containsKey(username)) {
            str = accounts.get(username).representMailbox();
        }
        return str;
    }

    /**
     * Fetches the String representation of an email and returns it.
     * Every time the user reads an e-mail, this e-mail is considered as "seen".
     * @param username the user who wants to read an email.
     * @param id the index of user's email to be read.
     * @return the string representation of desired e-mail. If there is no such e-mail (wrong index), it returns an
     * empty String.
     */
    String readEmail(String username, int id){
        String str = "";
        if(accounts.containsKey(username)){
            Email email = accounts.get(username).getEmail(id);
            if(email != null){
                str = "\n" + email.toString();
                email.makeSeen();
            }
        }
        return str;
    }

    /**
     * Deletes requested email form user's mailbox.
     * @param username the user who want to delete an e-mail.
     * @param id the specific e-mail to be deleted.
     * @return true if e-mail was deleted successfully (if it existed in users mailbox and then it was removed).
     */
    synchronized boolean deleteEmail(String username, int id) {
        if(accounts.containsKey(username)){
            return accounts.get(username).deleteEmail(id);
        }
        return false;
    }
}