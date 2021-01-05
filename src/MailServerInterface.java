import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MailServerInterface extends Remote{
    Boolean register(String username, String password) throws RemoteException;

    Boolean login(String username, String password) throws RemoteException;

    Boolean newEmail(String username, String receiver, String subject, String mainbody) throws RemoteException;

    String showEmails(String username) throws RemoteException;

    String readEmail(String username, int id) throws RemoteException;

    Boolean deleteEmail(String username, int id) throws RemoteException;

    Boolean logOut() throws RemoteException;

    Boolean exit() throws RemoteException;
}