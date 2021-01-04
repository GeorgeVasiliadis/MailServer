import java.util.ArrayList;

public class Account {
    private String username, password;
    private ArrayList<Email> mailbox;

    public Account(){

    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public ArrayList<Email> getMailbox(){
        return mailbox;
    }
}
