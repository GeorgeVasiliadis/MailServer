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

    public Boolean login(String username, String password) throws RemoteException {
        if(accounts.get(username) != null){
            return password.equals(accounts.get(username).getPassword());
        }
        return false;
    }

    public Boolean newEmail(String username, String receiver, String subject, String mainbody) throws RemoteException {
        if(accounts.containsKey(username) && accounts.containsKey(receiver)){
            Email email = new Email(username, receiver, subject, mainbody);
            return accounts.get(receiver).pullEmail(email);
        }
        return false;
    }

    public String showEmails(String username) throws RemoteException {
        String str = "User " + username + " is not valid";
        if(accounts.containsKey(username)) {
            str = accounts.get(username).representEmails();
        }
        return str;
    }

    public String readEmail(String username, int id) throws RemoteException {
        String str = "Invalid query.";
        if(accounts.containsKey(username)){
            Email email = accounts.get(username).getEmail(id);
            if(email != null){
                str = email.toString();
                email.makeSeen();
            }
        }
        return str;
    }

    public Boolean deleteEmail(String username, int id) throws RemoteException {
        if(accounts.containsKey(username)){
            accounts.get(username).deleteEmail(id);
        }
        return true;
    }

    public Boolean logOut() throws RemoteException {
        return true;
    }

    public Boolean exit() throws RemoteException {
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
    private Boolean loggedIn;
    private States currentState;
    private String response, request;


    public Session(MailServer server, Socket socket, DataInputStream dis, DataOutputStream dos){
        this.server = server;
        this.socket = socket;
        this.dis = dis;
        this.dos = dos;

        loggedIn = false;
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
