import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class MailServerImplementation extends UnicastRemoteObject implements MailServerInterface{
    private HashMap<String, String> usersMap;
    public MailServerImplementation() throws RemoteException {
        super();
        usersMap = new HashMap<>();
    }

    public Boolean register(String username, String password){
        return usersMap.putIfAbsent(username, password) == null;
    }

    public Boolean login(String username, String password) throws RemoteException {
        return password.equals(usersMap.get(username));
    }

    public Boolean newEmail() throws RemoteException {
        return true;
    }

    public Boolean showEmail() throws RemoteException {
        return true;
    }

    public Boolean readEmail() throws RemoteException {
        return true;
    }

    public Boolean deleteEmail() throws RemoteException {
        return true;
    }

    public Boolean logOut() throws RemoteException {
        return true;
    }

    public Boolean exit() throws RemoteException {
        return true;
    }
}
