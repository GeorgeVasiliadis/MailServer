import java.rmi.Naming;

public class MailServer {
    public MailServer(){

    }

    public static void main(String[] args){
        try{
            MailServerImplementation server = new MailServerImplementation();
            Naming.rebind("MailServer", server);
        } catch (Exception e){
            System.out.println(e);
        }
    }
}
