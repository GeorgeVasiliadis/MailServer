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

    private static void printWelcome(){
        printBanner();
        System.out.println("Hello, you connected as a guest.");
    }

    private static void printBye(){
        System.out.println("Thanks for using MailServer :D");
        System.out.println("Goodbye!");
    }

    private static void userMenu(String username){
        System.out.println("Yes, i live inside userMenu. What's wrong with you.");
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
                break;
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
            printWelcome();
            guestMenu();
            printBye();
        } catch (Exception e){
            System.out.println(e);
            System.exit(1);
        }
    }
}
