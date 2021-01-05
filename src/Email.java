public class Email {
    private Boolean isNew;
    private String sender, receiver;
    private String subject, mainbody;

    public Email(String sender, String receiver, String subject, String mainbody){
        this.isNew = true;
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.mainbody = mainbody;
    }

    public Boolean getNew(){
        return isNew;
    }

    public String getSender(){
        return sender;
    }

    public void makeSeen(){
        isNew = false;
    }

    public String getReceiver(){
        return receiver;
    }

    public String getSubject(){
        return subject;
    }

    public String getMainbody(){
        return mainbody;
    }

    public String toString(){
        String str = "Sender: " + sender + "\n" +
                "Receiver: " + receiver + "\n" +
                "Subject: " + subject + "\n" +
                "+ - - - - - - - - +" + "\n" +
                "Main Body: " + mainbody;

        return str;
    }
}
