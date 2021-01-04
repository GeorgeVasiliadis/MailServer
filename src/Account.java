import java.util.ArrayList;

public class Account {
    private String username, password;
    private ArrayList<Email> mailbox;

    public Account(String username, String password){
        this.username = username;
        this.password = password;
        this.mailbox = new ArrayList<>();
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public ArrayList<Email> getMailbox(){
        return new ArrayList<>(mailbox);
    }
}
