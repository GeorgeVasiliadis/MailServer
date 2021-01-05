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

    public Boolean submitEmail(Email email){
        if(email != null){
            mailbox.add(0, email);
            return true;
        }
        return false;
    }

    public Email getEmail(int index){
        Email email = null;
        if(index >= 0 && index < mailbox.size()){
            email = mailbox.get(index);
        }
        return email;
    }

    public Boolean deleteEmail(int index){
        if(index >= 0 && index < mailbox.size()){
            mailbox.remove(index);
            return true;
        }
        return false;
    }

    public String representEmails(){
        int i = 0;
        String str = "";
        for(Email email:mailbox){
            str += "[" + (email.getNew()?"N":"_") + "] " + i++ + " " + email.getSubject() + "\n";
        }
        return str;
    }
}
