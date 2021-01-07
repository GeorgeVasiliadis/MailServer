/**
 * Email represents the e-mail entity.
 * It contains a sender, a receiver, a subject and the mainbody, as well as whether it has been read or not.
 * @author George Vasiliadis
 * @version 7/1/21
 */
public class Email {
    private boolean isNew;
    private String sender, receiver;
    private String subject, mainbody;

    public Email(String sender, String receiver, String subject, String mainbody){
        this.isNew = true;
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.mainbody = mainbody;
    }

    boolean getNew(){
        return isNew;
    }

    String getSubject(){
        return subject;
    }

    String getSender(){
        return sender;
    }

    /**
     * Toggles the corresponding boolean variable.
     */
    void makeSeen(){
        isNew = false;
    }

    @Override
    public String toString(){
        return "Sender: " + sender + "\n" +
                "Receiver: " + receiver + "\n" +
                "Subject: " + subject + "\n" +
                "Main Body: " + mainbody + "\n";
    }
}
