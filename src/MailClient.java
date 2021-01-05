import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class MailClient {
    private static MailServerInterface mailServer;
    private static Scanner input;

    private static void print(String message){
        System.out.println(message);
    }

    private static String read(){
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    private static void printBanner(){
        System.out.println("-------------");
        System.out.println("MailServer:");
        System.out.println("-------------");
    }

    private static void printGuestPrompt(){
        print("==========");
        print("> LogIn");
        print("> SignIn");
        print("> Exit");
        print("==========");
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
        Scanner scanner = new Scanner(System.in);
        print("MailServer IP Address:");
        String ip = scanner.nextLine();
        print("MailServer Port:");
        int port = scanner.nextInt();

        ip = "127.0.0.1";
        port = 5000;
        Socket socket = null;
        DataInputStream dis;
        DataOutputStream dos;
        String response;
        String request;

        try{
            // Establish connection to server
            socket = new Socket(ip, port);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            // Ensure server is listening
            response = dis.readUTF();
            if(!response.equalsIgnoreCase("hello")){
                print("Couldn't establish connection to server.");
                socket.close();
                System.exit(1);
            }

            print("Connection to server was established successfully!");
            while(true) {
                print("You are connected as guest.");
                printGuestPrompt();
                request = read();
                if(request.equalsIgnoreCase("login")){
                    print("You asked to log-in.");
                } else if(request.equalsIgnoreCase("signin")){
                    // Ask to start registration procedure
                    dos.writeUTF("signin");

                    // Username
                    print("Username:");
                    String username = read();
                    dos.writeUTF(username);

                    // Password
                    print("Password:");
                    String password = read();
                    dos.writeUTF(password);

                    // Check if registration was successful
                    response = dis.readUTF();
                    if(response.equalsIgnoreCase("ok")){
                        print("User " + username + " was successfully registered!");
                    } else {
                        print("Registration failed.");
                    }
                } else if(request.equalsIgnoreCase("exit")){
                    print("Thanks for using MailServer :D");
                    socket.close();
                    System.exit(0);
                } else {
                    print("Invalid Option.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
