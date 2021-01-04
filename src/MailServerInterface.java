import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MailServerInterface extends Remote{
    Boolean register(String username, String password) throws RemoteException;

    Boolean login(String username, String password) throws RemoteException;

    Boolean newEmail() throws RemoteException;

    Boolean showEmail() throws RemoteException;

    Boolean readEmail() throws RemoteException;

    Boolean deleteEmail() throws RemoteException;

    Boolean logOut() throws RemoteException;

    Boolean exit() throws RemoteException;
}
