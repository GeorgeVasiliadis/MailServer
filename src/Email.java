public class Email {
    private Boolean isNew;
    private String sender, receiver;
    private String subject, mainbody;

    public Email(String sender, String receiver, String subject, String mainbody){

    }

    public Boolean getNew(){
        return isNew;
    }

    public String getSender(){
        return sender;
    }

    public String getReceiver(){
        return receiver;
    }

    public String getMainbody(){
        return mainbody;
    }
}
