import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class MailServer extends UnicastRemoteObject implements MailServerInterface{
    private HashMap<String, Account> accounts;
    public MailServer() throws RemoteException {
        super();
        accounts = new HashMap<>();
    }

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

    public static void main(String[] args){
        try{
            MailServer server = new MailServer();
            Naming.rebind("MailServer", server);
        } catch (Exception e){
            System.out.println(e);
        }
    }
}
