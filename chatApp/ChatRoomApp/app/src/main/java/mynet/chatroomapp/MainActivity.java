package mynet.chatroomapp;

import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class MainActivity extends ActionBarActivity {
    private TextView EnterTxt,textView;
    //private Button TxtBtn, getInfoBtn;
    //private EditText editText,editText2,editText3;// 0 send msg, 2 IP, 3 Name
    DataOutputStream outPut = null;
    DataInputStream inPut = null;
    String userID;
    int userSeat;
    boolean connected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) { // do initialization here
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView cv = ((TextView) findViewById(R.id.CommuteView));
        final Button button = (Button) findViewById(R.id.TxtBtn);
        final Button button1 = (Button) findViewById(R.id.getInfoBtn);
        TextView enter = (TextView) findViewById(R.id.EnterTxt);
        TextView tx =(TextView) findViewById(R.id.textView);

        ((TextView) findViewById(R.id.CommuteView)).append("\nWelcome! ");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

//stuff that updates ui


                try {
                    button.setOnClickListener(new View.OnClickListener() { //send messages
                        public void onClick(View v) {
                            //Log.v("M", "AAA");
                            if (connected) {
                                String a = ((TextView) findViewById(R.id.EnterTxt)).getText().toString();
                                //((TextView) findViewById(R.id.CommuteView)).append("\nYou said: " + a);
                                if (a.equals("cls")) {
                                    cv.setText(" ");
                                } else {
                                    final String n = a;
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                outPut.writeInt(userSeat);
                                                writeString(outPut, n);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }).start();
                                }
                            } else {
                                ((TextView) findViewById(R.id.CommuteView)).append("\nNot Connected! ");
                            }
                        }
                    });
                } catch (Exception ex) {
                    Log.v("E", "" + ex);
                }
                button1.setOnClickListener(new View.OnClickListener() { // get connection
                    public void onClick(View v) {
                        // Log.v("M", "BBBB");
                        if (!connected) {
                            final String ipAdd = ((TextView) findViewById(R.id.ip)).getText().toString();
                            userID = ((TextView) findViewById(R.id.name)).getText().toString();
                            Log.v("M", "Login stage1");
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    InetAddress ipadd = null;
                                    Socket socket = null;
                                    try {
                                        //ipadd = InetAddress.getByName("192.168.1.5");
                                        ipadd = InetAddress.getByName(ipAdd);
                                        socket = new Socket(ipadd, 8000);
                                        inPut = new DataInputStream(socket.getInputStream());
                                        outPut = new DataOutputStream(socket.getOutputStream());
                                        writeString(outPut, userID);
                                        if (inPut.readBoolean()) {
                                            userSeat = inPut.readInt();
                                            connected = true;
                                        } else {
                                            connected = false;
                                        }
                                        //  ((TextView) findViewById(R.id.CommuteView)).append("\nConnected! ");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    // new Thread(new HandleAClient(socket)).start();
                                }
                            }).start();

                            // button1.setText("Disconnect");
                        } else {
                            try {
                                inPut.close();
                                outPut.close();
                                connected = false;
                                button1.setText("Connect");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            while (connected) {
                                try {
                                    String n = readString(inPut);
                                    String c = readString(inPut);
                                    Log.v("s",n+" Said: "+c);
                                    cv.append("\n" + n + " said: " + c);
                                    //((TextView) findViewById(R.id.CommuteView)).append("\n" + n + " said: " + c);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }).start();
            }
        });

    }
    /*
    public View.OnClickListener sentTextBtn= new View.OnClickListener(){
        @Override
    public void onClick(View v){
            switch (v.getId()){
                case R.id.TxtBtn:
                    textView.setText("Send text");
                break;
                case R.id.getInfoBtn:
                    textView.setText("Send infomation");
                    break;
            }
        }
        };*/
    private void writeString(DataOutputStream output, String words) throws IOException {
        output.writeInt(words.length());
        output.writeBytes(words);
    }

    private String readString(DataInputStream input) throws IOException {
        int length = input.readInt();
        byte[] nStream = new byte[length];
        input.readFully(nStream);
        return new String(nStream, "UTF-8");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //@Override
    class HandleAClient implements Runnable {

        private Socket socket; // A connected socket

        /**
         * Construct a thread
         * @param socket
         */
        public HandleAClient(Socket socket) {
            try {
                Log.v("M", "Login stage2");
                inPut = new DataInputStream(this.socket.getInputStream());
                outPut = new DataOutputStream(this.socket.getOutputStream());

               //
            } catch(UnknownHostException e){
                e.printStackTrace();
            } catch (IOException e) {
                Log.v("M", ""+e);
                e.printStackTrace();
            }
           // this.socket = socket;
        }
        @Override
        public void run(){

            try {
                writeString(outPut, userID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.v("M", "Login stage3");
            connected = true;

        }
    }
}
