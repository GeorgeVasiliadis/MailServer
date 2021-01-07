package MailClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * MailClient is a CLI-based program that lets a user create an e-mail account and interact with it.
 *
 * @author George Vasiliadis
 * @version 7/1/21
 */
public class MailClient {

    public static void main(String[] args){

        // Get Server address info
        print("MailServer's IP Address:");
        String ip = read();
        print("MailServer's Port:");
        String port = read();

        // Establish connection to server and run
        try{
            Socket socket = new Socket(ip, Integer.parseInt(port));
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            popup("Welcome to MailServer!");
            guestSession(socket, dis, dos);
        } catch (Exception e) {
            print("Couldn't connect to server due to a fatal error. Please check connection to server and try again.");
            System.exit(1);
        }
    }

    /**
     * Simple substitute (alias) to System.out.println().
     * @param message to be printed along with a new line
     */
    private static void print(String message){
        System.out.println(message);
    }

    /**
     * Simple wrapper string to print given message in a unique way.
     * String is framed simulating a GUI pop-up window.
     * Should be used for any message that must stand out.
     * @param message to be printed in a pop-up frame.
     */
    private static void popup(String message){
        int len = message.length();
        System.out.print("+");
        for(int i=0; i<len; i++){
            System.out.print("-");
        }
        System.out.print("+\n");
        print("|" + message + "|");
        System.out.print("+");
        for(int i=0; i<len; i++){
            System.out.print("-");
        }
        System.out.print("+\n");
    }

    /**
     * Simple substitute (alias) to Scanner.nextLine().
     * Prompts user with a special symbol to enter some text.
     * @return tex entered by user without trailing new-line character.
     */
    private static String read(){
        System.out.print(">>> ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    /**
     * Closes connection and farwells user
     * @param socket of current connection
     * @param dos output stream of current connection
     */
    private static void exit(Socket socket, DataOutputStream dos){
        try {
            dos.writeUTF("exit");
            socket.close();
        } catch (IOException e){
            print("Couldn't terminate connection to server properly.");
        }
        popup("Thanks for using MailServer :D");
        System.exit(0);
    }

    /**
     * Simple output function that informs a guest user with available commands.
     */
    private static void printGuestMenu(){
        print("=========");
        print("o| LogIn");
        print("o| SignIn");
        print("o| Exit");
        print("=========");
    }

    /**
     * Simple output function that informs a logged-in user with available commands.
     */
    private static void printUserMenu(){
        print("===============");
        print("o| NewEmail");
        print("o| ShowEmails");
        print("o| ReadEmail");
        print("o| DeleteEmail");
        print("o| Logout");
        print("o| Exit");
        print("===============");
    }

    /**
     * Implements the client's side protocol when user is connected as guest.
     * @param socket of current connection
     * @param dis input stream of current connection
     * @param dos output stream of current connection
     */
    private static void guestSession(Socket socket, DataInputStream dis, DataOutputStream dos){
        String request;
        String response;

        popup("You are connected as guest.");

        try {
            while(true) {
                printGuestMenu();
                request = read();

                // Sign-In
                if (request.equalsIgnoreCase("signin")) {

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
                    if (response.equalsIgnoreCase("ok")) {
                        popup("User " + username + " was successfully registered!");
                    } else {
                        print("Registration failed.");
                    }
                }

                // Log-In
                else if (request.equalsIgnoreCase("login")) {

                    // Ask to start login procedure
                    dos.writeUTF(request);

                    // Username
                    print("Username:");
                    String username = read();
                    dos.writeUTF(username);

                    // Password
                    print("Password:");
                    String password = read();
                    dos.writeUTF(password);

                    // Check if login was successful
                    response = dis.readUTF();
                    if (response.equalsIgnoreCase("ok")) {
                        userSession(username, socket, dis, dos);
                        popup("You are connected as guest.");
                    } else {
                        print("LogIn failed.\nPlease make sure you entered the right credentials.");
                    }
                }

                // Exit
                else if (request.equalsIgnoreCase("exit")) {
                    exit(socket, dos);
                    break;
                }

                // Typo
                else {
                    print("Invalid option.");
                }
            }
        } catch (IOException e){
            print("A fatal error occurred while communicating with server.");
            System.exit(1);
        }
    }

    /**
     * Implements the client's side protoco when user is logged-in.
     * @param username of currently logged in user
     * @param socket of current connection
     * @param dis input stream of current connection
     * @param dos output stream of current connection
     */
    private static void userSession(String username, Socket socket, DataInputStream dis, DataOutputStream dos){
        String request;
        String response;

        try{
            popup("You are connected as " + username + ".");
            while (true) {
                // Print new prompt
                printUserMenu();

                // Get request
                request = read();

                // Create new e-mail
                if (request.equalsIgnoreCase("newemail")) {

                    // Ask to start creating a new e-mail
                    dos.writeUTF(request);

                    // Receiver
                    print("Receiver");
                    String receiver = read();
                    dos.writeUTF(receiver);


                    // Subject
                    print("Subject:");
                    String subject = read();
                    dos.writeUTF(subject);

                    // Main Body
                    print("Main Body (enter \"<ok>\" to send):");
                    StringBuilder mainbody = new StringBuilder();
                    String line;
                    while (true) {
                        line = read();
                        if (line.equalsIgnoreCase("<ok>")) {
                            break;
                        }
                        mainbody.append(line).append("\n");
                    }
                    dos.writeUTF(mainbody.toString());

                    response = dis.readUTF();
                    if (response.equalsIgnoreCase("ok")) {
                        print("E-mail was sent successfully!");
                    } else {
                        print("Couldn't send e-mail.");
                    }
                }

                // Show mailbox
                else if (request.equalsIgnoreCase("showemails")) {
                    // Ask to start presenting e-mails
                    dos.writeUTF(request);

                    // Fetch Data
                    response = dis.readUTF();

                    // Display Data
                    if (response.isEmpty()) {
                        print("There are no e-mails yet.");
                    } else {
                        print(response);
                    }
                }

                // Read a specific email
                else if (request.equalsIgnoreCase("reademail")) {
                    // Ask to start email-reading procedure
                    dos.writeUTF(request);

                    // Get index of desired e-mail
                    String index;
                    while (true) {
                        print("E-mail ID to be read:");
                        index = read();
                        try {
                            int i = Integer.parseInt(index);
                            if (i >= 0) {
                                break;
                            } else {
                                print("ID should be a non-negative integer.");
                            }
                        } catch (NumberFormatException e) {
                            print("ID should be a non-negative integer.");
                        }
                    }

                    // Request a specific e-mail
                    dos.writeUTF(index);

                    // Fetch requested e-mail
                    response = dis.readUTF();

                    // Check e-mail validity
                    if (!response.isEmpty()) {
                        print(response);
                    } else {
                        print("E-mail #" + index + " doesn't exist.");
                    }
                }

                // Delete a specific email
                else if (request.equalsIgnoreCase("deleteemail")) {
                    // Ask to start email-reading procedure
                    dos.writeUTF(request);

                    // Get index of desired e-mail to be deleted
                    String index;
                    while (true) {
                        print("E-mail ID to be deleted:");
                        index = read();
                        try {
                            int i = Integer.parseInt(index);
                            if (i >= 0) {
                                break;
                            } else {
                                print("ID should be a non-negative integer.");
                            }
                        } catch (NumberFormatException e) {
                            print("ID should be a non-negative integer.");
                        }
                    }

                    // Request a specific e-mail
                    dos.writeUTF(index);

                    // Fetch response and inform user
                    response = dis.readUTF();
                    if (response.equalsIgnoreCase("ok")) {
                        print("E-mail #" + index + " was deleted successfully!");
                    } else {
                        print("E-mail #" + index + " doesn't exist.");
                    }
                }

                // Log user out
                else if (request.equalsIgnoreCase("logout")) {
                    dos.writeUTF(request);
                    break;
                }

                // Exit
                else if (request.equalsIgnoreCase("exit")) {
                    exit(socket, dos);
                    break;
                }

                // Typo
                else {
                    print("Invalid option.");
                }
            }
        } catch (Exception e){
            print("A fatal error occurred while communicating with server.");
            System.exit(1);
        }
    }
}