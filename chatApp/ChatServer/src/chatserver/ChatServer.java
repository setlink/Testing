/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 *
 * @author admin
 */
public class ChatServer extends Application {

    public class Members {

        String userName;
        Socket userSk;
        DataInputStream in;
        DataOutputStream out;
    }
    /**
     * Setup the maxium number of user that is able to join the server
     */
    static int MAX_SERVER_MEMBER = 10;
    /**
     * setup the port number
     */
    int connectionCount = 0;

    static int SERVER_PORT = 8000;
    static int member = 0;
    //static int conp = 0;
    boolean isConectingMember = false;
    Members[] mArr = new Members[MAX_SERVER_MEMBER];

    private final TextArea ta = new TextArea();
    Queue<String> Que = new LinkedList<>();// = new Queue<String>();
    Queue<Integer> uName = new LinkedList<>();

    @Override
    public void start(Stage primaryStage) {

        // Create a scene and place it in the stage
        ta.setPrefSize(445, 195);
        ta.setWrapText(true);
        Scene scene = new Scene(new ScrollPane(ta), 450, 200);
        primaryStage.setTitle("ChatServer"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage

        new Thread(() -> { // connection thread
            try {
                //System.out.println("sss");
                ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
                printString("Server started!");
                //InetAddress ip = InetAddress.getLocalHost;
                try {
                    printString("Server connection is: " + InetAddress.getLocalHost() + ":" + SERVER_PORT);
                } catch (Exception ex) {
                    System.out.println(ex);
                }

                while (true) {
                    System.out.println("Waiting for connection...");
                    Socket socket = serverSocket.accept();
                    System.out.println("got connection " + ++connectionCount);
                    printString("got connection " + connectionCount);
                    //InetAddress inetAddress = socket.getInetAddress();
                    new Thread(new HandleAClient(socket)).start();
                }

            } catch (IOException ex) {
                //Logger.getLogger(ChatRoomServer.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("fail starting the server: " + ex);
            }
        }).start();

        new Thread(() -> { // send out messages to all user that is in the chat
            while (true) {
                while (member == 0) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException ex) {
                        System.out.println(ex);
                    }
                }
                //System.out.println("message output thread started");
                ////////////////////////////
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
                 //System.out.println("Que peek:" + Que);
                //System.out.println("uName peek:" + uName);
                ///////////////////////////

                if (Que.peek() != null && uName.peek() != null) {
                   // System.out.println("message output thread started");
                    for (int i = 0; i < member; i++) {
                       // if (uName.peek() == i) {
                       //     i++;
                      //  } else {
                            if (i == member - 1) {
                                try {
                                    System.out.println(" member-1 stage");
                                    writeString(mArr[i].out, mArr[uName.poll()].userName);
                                    writeString(mArr[i].out, Que.poll());
                                } catch (ConnectException ex) {
                                    System.out.println("Connection:" + ex);
                                } catch (IOException ex) {
                                    System.out.println("IO:" + ex);
                                }
                            } else {
                                try {
                                    System.out.println(" else stage " + i);
                                    System.out.println("Que peek:" + Que.peek());
                                    System.out.println("uName peek:" + uName.peek().intValue());
                                    writeString(mArr[i].out, mArr[uName.peek().intValue()].userName);
                                    writeString(mArr[i].out, Que.peek());
                                } catch (ConnectException ex) {
                                    System.out.println("Connection:" + ex);
                                } catch (IOException ex) {
                                    System.out.println("IO:" + ex);
                                }
                          //  }

                        }
                    }
                }
            }
        }).start();
        /*
         new Thread(() -> { //check if anyone left
         }).start();
         */
    }

    class HandleAClient implements Runnable {

        private Socket socket; // A connected socket

        /**
         * Construct a thread
         */
        public HandleAClient(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            int conp;
            DataOutputStream cOutput = null;
            DataInputStream cInput = null;
            String c;// string reciver

            try {
                
                cInput = new DataInputStream(socket.getInputStream());
                cOutput = new DataOutputStream(socket.getOutputStream());
                String name = readString(cInput);
                printString("got name " + name + " on halt!");
                if (member != 0) //dose not do conning check for no member
                {
                    System.out.println("server is not empty");
                    for (int i = 0; i < member && (mArr[i] != null); ++i) { // check if the member is connecting
                        if (mArr[i].userName.equals(name)) {
                            isConectingMember = true;
                        }
                    }
                }
                if (!isConectingMember && member >= MAX_SERVER_MEMBER) { // check if the server is maxed

                    System.out.println("refuse to connect");
                    cOutput.writeBoolean(false);
                    cOutput.close();

                } else {
                    System.out.println("connected " + member);
                    cOutput.writeBoolean(true);
                    conp = member;
                    //mArr[member].userName = new String();
                    //mArr[member].userSk = new Socket();
                    //mArr[member].in = new DataInputStream(socket.getInputStream());
                    //mArr[member].out = new DataOutputStream(socket.getOutputStream());
                    mArr[conp] = new Members();
                    mArr[member].userName = name;
                    mArr[member].userSk = socket;
                    mArr[member].in = cInput;
                    mArr[member].out = cOutput;
                    cOutput.writeInt(member);
                    int handleCount = member;
                    member++;
                    printString(name + " join the chat!");

                    while (true) {
                        int a = mArr[handleCount].in.readInt();
                        //int a = cInput.readInt();
                        System.out.println("User " + a);
                        Integer n = a;
                        uName.add(n);//get username
                        c = readString(mArr[handleCount].in); //get chat
                        printString(mArr[a].userName + " said: " + c);
                        //System.out.println("User "+ c);
                        Que.add(c);
                        System.out.println("uQueue: " + uName.peek());
                        System.out.println("Queue: " + Que.peek());
                        System.out.println("uQueue: " + uName.peek());
                        System.out.println("Queue: " + Que.peek());
                        System.out.println("uQueue: " + uName.peek());
                        System.out.println("Queue: " + Que.peek());
                    }
                }

                //do IO command
            } catch (IOException ex) {
                System.out.println(ex);
            } //finally {
            /*
             try {
             if (cOutput == null) {
             cOutput.close();
             }
             } catch (IOException ex) {
             System.out.println(ex);
             }
             */
            // }
        }
    }

    /**
     * @param args the command line arguments
     */
    /*
     public static void main(String[] args) {
     launch(args);
     }
     */
    private void printString(String str) {
        Platform.runLater(() -> {
            ta.appendText(str + "\n");
        });
    }

    private String readString(DataInputStream input) throws IOException {

        int length = input.readInt();
        byte[] nStream = new byte[length];
        input.readFully(nStream);

        return new String(nStream, "UTF-8");
    }

    private void writeString(DataOutputStream output, String words) throws IOException {
        output.writeInt(words.length());
        output.writeBytes(words);
    }
}
