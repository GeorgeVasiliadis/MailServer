import java.util.ArrayList;

/**
 * Account is used to represent the logical entity of a registered user.
 * It contains the registered user's name, a password and the corresponding mailbox.
 * The mailbox contains all incoming e-mails.
 *
 * @author George Vasiliadis
 * @version 7/1/21
 */
public class Account {
    private String username, password;
    private ArrayList<Email> mailbox;

    public Account(String username, String password){
        this.username = username;
        this.password = password;
        this.mailbox = new ArrayList<>();
    }

    String getUsername(){
        return username;
    }

    String getPassword(){
        return password;
    }

    /**
     * Places a new email at the beginning of mailbox.
     * @param email to be submitted to current account.
     * @return true if e-mail was submitted successfully. If provided e-mail is null, false is being returned.
     */
    boolean submitEmail(Email email){
        if(email != null){
            mailbox.add(0, email);
            return true;
        }
        return false;
    }

    /**
     * Return the email that is placed at the given index in mailbox.
     * @param index of the desired e-mail to be fetched.
     * @return the desired e-mail if it exists in given index, or otherwise, null.
     */
    Email getEmail(int index){
        Email email = null;
        if(index >= 0 && index < mailbox.size()){
            email = mailbox.get(index);
        }
        return email;
    }

    /**
     * Delete the email that is placed at the given index in mailbox.
     * @param index of the desired e-mail to be deleted.
     * @return true if the desired e-mail exists and it was deleted successfully.
     */
    boolean deleteEmail(int index){
        if(index >= 0 && index < mailbox.size()){
            mailbox.remove(index);
            return true;
        }
        return false;
    }

    /**
     * Creates and returns the one-string-representation of current mailbox.
     * The representation consists of e-mail's status (new/ old), id, sender and subject in the following form:
     *
     * [N] 0 User Subject0
     * [N] 1 User Subject1
     * [_] 2 User Subject2
     *        .
     *        .
     *        .
     * [_] K User SubjectK
     *
     * @return the one-string-representation of mailbox. If mailbox is empty, empty string is returned respectively.
     */
    String representMailbox(){
        int i = 0;
        StringBuilder str = new StringBuilder();
        for(Email email:mailbox){
            // Status
            str.append("[").append((email.getNew()?"N":"_")).append("]");
            str.append("\t");
            // ID
            str.append("#").append(i++);
            str.append("\t");
            // Sender
            str.append("from:").append(email.getSender());
            str.append("\t");
            // Subject
            str.append(email.getSubject()).append("\n");
        }
        return str.toString();
    }
}
