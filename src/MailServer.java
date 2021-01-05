import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.HashMap;

public class MailServer{
    private HashMap<String, Account> accounts;
    public MailServer() throws RemoteException {
        super();
        accounts = new HashMap<>();
    }

    /**
     * Registers a new user to the system.
     * A user can't be registered twice using the same name.
     * @param username the uniq name of new user.
     * @param password the password of new user.
     * @return true if user was registered successfully.
     */
    public Boolean register(String username, String password){
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
    public Boolean login(String username, String password){
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
    public Boolean newEmail(String sender, String receiver, String subject, String mainbody){
        if(accounts.containsKey(sender) && accounts.containsKey(receiver)){
            Email email = new Email(sender, receiver, subject, mainbody);
            return accounts.get(receiver).submitEmail(email);
        }
        return false;
    }

    /**
     * Creates a String representation of users mailbox and returns it.
     * The representation contains the id of each e-mail, its status (seen/ unseen) and its subject.
     * @param username the user who wants to get his mailbox represented.
     * @return a string representing the user's current mailbox. If user-client does not exist a special String is returned
     * instead.
     */
    public String showEmails(String username) {
        String str = "User " + username + " is not valid";
        if(accounts.containsKey(username)) {
            str = accounts.get(username).representEmails();
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
    public String readEmail(String username, int id){
        String str = "";
        if(accounts.containsKey(username)){
            Email email = accounts.get(username).getEmail(id);
            if(email != null){
                str = email.toString();
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
    public Boolean deleteEmail(String username, int id) {
        if(accounts.containsKey(username)){
            return accounts.get(username).deleteEmail(id);
        }
        return false;
    }

    public Boolean logOut() {
        return true;
    }

    public Boolean exit()  {
        return true;
    }

    public static void main(String[] args) throws IOException{
        ServerSocket ss = new ServerSocket(5000);
        Socket socket = null;
        MailServer server = new MailServer();
        while(true) {
            try {
                socket = ss.accept();
                System.out.println("New Connection: Accepted");
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                Thread thread = new Session(server, socket, dis, dos);
                thread.start();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}

class Session extends Thread{
    enum States{SESSION, REGISTER, LOGIN, LOGOUT, SHOW, READ, DELETE, NEW, EXIT, REGISTER_USERNAME, LOGIN_USERNAME};

    final private MailServer server;
    final private Socket socket;
    final private DataInputStream dis;
    final private DataOutputStream dos;
    private String loggedUser;
    private States currentState;
    private String response, request;


    public Session(MailServer server, Socket socket, DataInputStream dis, DataOutputStream dos){
        this.server = server;
        this.socket = socket;
        this.dis = dis;
        this.dos = dos;
        loggedUser = "";
        currentState = States.SESSION;
    }

    private String generateResponse(String request){
        String response = "Invalid entry!";
        if(!request.isEmpty()){
            if(currentState == States.SESSION) {
                if (request.equalsIgnoreCase("login")) {
                    response = "Username:";
                    currentState = States.REGISTER_USERNAME;
                } else if (request.equalsIgnoreCase("signin")) {
                    response = "Username:";
                    currentState = States.LOGIN_USERNAME;
                } else if (request.equalsIgnoreCase("exit")) {
                    response = "Bye!";
                    currentState = States.EXIT;
                }
            } else if(currentState == States.REGISTER_USERNAME){
            }
        }
        return response;
    }

    private void guestSession(){
        try {
            response = "You connected as guest\n" +
                    "+ LogIn\n" +
                    "+ SignIn\n" +
                    "+ Exit\n";
            dos.writeUTF(response);
            while(true){
                request = dis.readUTF();
                System.out.println("I just read: " + request);
                if(request.equalsIgnoreCase("login")){
                    //do something
                } else if(request.equalsIgnoreCase("signin")){
                    //do something
                } else if(request.equalsIgnoreCase("exit")){
                    response = "bye\n";
                    dos.writeUTF(response);
                    socket.close();
                } else {
                    response = "Invalid entry!\n";
                }
                response += "+ LogIn\n" +
                        "+ SignIn\n" +
                        "+ Exit\n";
                dos.writeUTF(response);
            }

        } catch (Exception e){
            System.out.println(e);
        }
    }

    @Override
    public void run(){
        try {
            // Inform for availability
            dos.writeUTF("hello");
            String request;
            String response;
            Boolean run = true;

            while(run){
                // Fetch request
                request = dis.readUTF();

                // Exit
                if(request.equalsIgnoreCase("exit")){
                    run = false;
                }

                // Log-In
                else if(request.equalsIgnoreCase("login")){
                    // Get data
                    String username = dis.readUTF();
                    String password = dis.readUTF();

                    // Log user in
                    if(server.login(username, password)){
                        dos.writeUTF("ok");
                        loggedUser = username;

                        // Logged-In Session
                        while(run){

                            // Fetch request
                            request = dis.readUTF();

                            if(request.equalsIgnoreCase("newemail")){

                                // Get Data
                                String receiver = dis.readUTF();
                                String subject = dis.readUTF();
                                String mainbody = dis.readUTF();

                                // Send Email
                                if(server.newEmail(loggedUser, receiver, subject, mainbody)){
                                    response = "ok";
                                } else {
                                    response = "nok";
                                }

                                // Inform User
                                dos.writeUTF(response);
                            }

                            // Represent Emails
                            else if(request.equalsIgnoreCase("showemails")){
                                response = server.showEmails(username);
                                dos.writeUTF(response);
                            }

                            // Read Email
                            else if(request.equalsIgnoreCase("reademail")){
                                // Fetch index of e-mail
                                request = dis.readUTF();

                                // Retrieve requested e-mail
                                response = server.readEmail(username, Integer.parseInt(request));

                                // Return requested e-mail
                                dos.writeUTF(response);
                            }

                            // Delete Email
                            else if(request.equalsIgnoreCase("deleteemail")){
                                // Fetch index of e-mail
                                request = dis.readUTF();

                                // Retrieve requested e-mail
                                if(server.deleteEmail(username, Integer.parseInt(request))){
                                    response = "ok";
                                } else {
                                    response = "nok";
                                }

                                // Return status
                                dos.writeUTF(response);
                            }

                            // Log User Out
                            else if(request.equalsIgnoreCase("logout")){
                                loggedUser = "";
                                break;
                            }
                        }
                    } else {
                        dos.writeUTF("nok");
                    }
                }

                // Sign-In
                else if(request.equalsIgnoreCase("signin")){
                    // Get data
                    String username = dis.readUTF();
                    String password = dis.readUTF();

                    // Register user
                    if(server.register(username, password)){
                        response = "ok";
                    } else {
                        response = "nok";
                    }

                    // Inform client
                    dos.writeUTF(response);
                }
            }

        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
