import java.rmi.Naming;
import java.util.Scanner;

public class MailClient {
    private static MailServerInterface mailServer;
    private static Scanner input;

    private static void printBanner(){
        System.out.println("-------------");
        System.out.println("MailServer:");
        System.out.println("-------------");
    }

    private static void printGuestPrompt(){
        System.out.println("==========");
        System.out.println("> LogIn");
        System.out.println("> SignIn");
        System.out.println("> Exit");
        System.out.println("==========");
    }

    private static void printUserPrompt(){
        System.out.println("==========");
        System.out.println("> NewEmail");
        System.out.println("> ShowEmails");
        System.out.println("> ReadEmail");
        System.out.println("> DeleteEmail");
        System.out.println("> Logout");
        System.out.println("> Exit");
        System.out.println("==========");
    }

    private static void printWelcome(){
        printBanner();
        System.out.println("Hello, you connected as a guest.");
    }

    private static void printBye(){
        System.out.println("Thanks for using MailServer :D");
        System.out.println("Goodbye!");
    }

    private static void userMenu(String username){
        String command;
        while(true){
            printUserPrompt();
            command = input.next();
            if(command.equalsIgnoreCase("NewEmail")){
                try {
                    System.out.println("Receiver:");
                    String receiver = input.next();
                    System.out.println("Subject:");
                    String subject = input.next();
                    input.nextLine();
                    System.out.println("Text:");
                    String mainbody = input.nextLine();
                    if (!(receiver.isEmpty() || subject.isEmpty() || mainbody.isEmpty())){
                        System.out.println("Email was created successfully!");
                    } else {
                        System.out.println("Email couldn't be created.");
                    }
                    if (mailServer.newEmail(username, receiver, subject, mainbody)) {
                        System.out.println("Email was sent successfully!");
                    } else {
                        System.out.println("Email was rejected. Please try again.");
                    }
                } catch (Exception e){
                    System.out.println(e);
                }
            }else if(command.equalsIgnoreCase("ShowEmails")){
                try{
                    System.out.println(mailServer.showEmails(username));
                } catch (Exception e){
                    System.out.println(e);
                }
            } else if(command.equalsIgnoreCase("ReadEmail")){
                System.out.println("Number of desired Email:");
                int id = input.nextInt();
                try {
                    System.out.println(mailServer.readEmail(username, id));
                }catch (Exception e){
                    System.out.println(e);
                }
            } else if(command.equalsIgnoreCase("DeleteEmail")){
                System.out.println("Number of Email to delete:");
                int id = input.nextInt();
                try{
                    mailServer.deleteEmail(username, id);
                } catch (Exception e){
                    System.out.println(e);
                }
            } else if(command.equalsIgnoreCase("LogOut")){
                break;
            } else if(command.equalsIgnoreCase("Exit")){
                printBye();
                System.exit(0);
            } else{
                System.out.println("Command \"" + command + "\" is invalid.");
            }
        }
    }

    private static void guestMenu(){
        String command;

        while(true) {
            printGuestPrompt();
            command = input.next();
            if (command.equalsIgnoreCase("LogIn")) {
                System.out.println("Username:");
                String username = input.next();
                System.out.println("Password:");
                String password = input.next();
                try {
                    if(!mailServer.login(username, password)){
                        System.out.println("Invalid username or password. Please try again");
                    } else {
                        System.out.println("Welcome back " + username + "!");
                        userMenu(username);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }

            } else if (command.equalsIgnoreCase("SignIn")) {
                System.out.println("Provide a username:");
                String username = input.next();
                System.out.println("Provide a password:");
                String password = input.next();
                try {
                    if(!mailServer.register(username, password)){
                        System.out.println("User exists already. Try a different username.");
                    } else {
                        System.out.println("User " + username + " was registered successfully.");
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }

            } else if (command.equalsIgnoreCase("Exit")) {
                printBye();
                System.exit(0);
            } else {
                System.out.println("Command \"" + command + "\" is invalid.");
            }
        }

    }

    public static void main(String[] args){
        input = new Scanner(System.in);
        System.out.println("Please provide mail-server's IP (***.***.***.***): ");
        String ip = input.nextLine();

        try {
            String mailServerURL = "rmi://" + ip + "/MailServer";
            mailServer = (MailServerInterface) Naming.lookup(mailServerURL);
            System.out.println("Connection to " + ip + " has been established.");
            if(mailServer.register("george", "111"))System.out.println("STOP");
            mailServer.register("dimitra", "222");
            mailServer.register("alice", "333");
            printWelcome();
            guestMenu();
        } catch (Exception e){
            System.out.println(e);
            System.exit(1);
        }
    }
}
