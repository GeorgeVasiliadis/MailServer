package MailServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Session represents the session between a MailClient and the MailServer.
 * Server's protocol is implemented here.
 *
 * @author George Vasiliadis
 * @version 7/1/21
 */
class Session extends Thread{
    private static int sessionCount = 0;
    final private int sid;
    final private MailServer server;
    final private DataInputStream dis;
    final private DataOutputStream dos;

    Session(MailServer server, DataInputStream dis, DataOutputStream dos){
        this.server = server;
        this.dis = dis;
        this.dos = dos;
        sid = sessionCount++;
    }

    @Override
    public void run(){
        System.out.println("Session #" + sid + " has started.");
        try {
            String request;
            String response;
            boolean connected = true;

            // Guest Session
            while(connected){

                // Fetch request
                request = dis.readUTF();

                // Exit
                if(request.equalsIgnoreCase("exit")){
                    connected = false;
                    System.out.println("Session #" + sid + " has been terminated.");
                }

                // Log-In
                else if(request.equalsIgnoreCase("login")){
                    // Get data
                    String username = dis.readUTF();
                    String password = dis.readUTF();

                    // Log user in
                    if(server.login(username, password)){
                        // Accept client
                        dos.writeUTF("ok");
                        boolean logged = true;

                        // Logged-In Session
                        while(logged){

                            // Fetch request
                            request = dis.readUTF();

                            // New Email
                            if(request.equalsIgnoreCase("newemail")){

                                // Get Data
                                String receiver = dis.readUTF();
                                String subject = dis.readUTF();
                                String mainbody = dis.readUTF();

                                // Send Email
                                if(server.newEmail(username, receiver, subject, mainbody)){
                                    response = "ok";
                                } else {
                                    response = "nok";
                                }

                                // Inform User
                                dos.writeUTF(response);
                            }

                            // Represent Emails
                            else if(request.equalsIgnoreCase("showemails")){
                                response = server.showEmails(username);
                                dos.writeUTF(response);
                            }

                            // Read Email
                            else if(request.equalsIgnoreCase("reademail")){
                                // Fetch index of e-mail
                                request = dis.readUTF();

                                // Retrieve requested e-mail
                                response = server.readEmail(username, Integer.parseInt(request));

                                // Return requested e-mail
                                dos.writeUTF(response);
                            }

                            // Delete Email
                            else if(request.equalsIgnoreCase("deleteemail")){
                                // Fetch index of e-mail
                                request = dis.readUTF();

                                // Retrieve requested e-mail
                                if(server.deleteEmail(username, Integer.parseInt(request))){
                                    response = "ok";
                                } else {
                                    response = "nok";
                                }

                                // Return status
                                dos.writeUTF(response);
                            }

                            // Log Out
                            else if(request.equalsIgnoreCase("logout")){
                                logged = false;
                            }

                            // Exit
                            else if(request.equalsIgnoreCase("exit")){
                                logged = connected = false;
                                System.out.println("Session #" + sid + " has been terminated.");
                            }
                        }
                    } else {
                        dos.writeUTF("nok");
                    }
                }

                // Sign-In
                else if(request.equalsIgnoreCase("signin")){
                    // Get data
                    String username = dis.readUTF();
                    String password = dis.readUTF();

                    // Register user
                    if(server.register(username, password)){
                        response = "ok";
                    } else {
                        response = "nok";
                    }

                    // Inform client
                    dos.writeUTF(response);
                }
            }

        } catch (IOException e){
            System.out.println("An error has occurred while communicating with a client.");
            System.out.println("Session #" + sid + " has been lost.");
        }
    }
}