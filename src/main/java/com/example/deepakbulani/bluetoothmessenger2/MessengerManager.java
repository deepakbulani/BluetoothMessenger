package com.example.deepakbulani.bluetoothmessenger2;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

public class MessengerManager extends AppCompatActivity {




    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private Button sendButton;
    private EditText sendText;
    private static final String TAG="MainActivity";
    private RecyclerView.LayoutManager mLayoutManager;
    private Vector<HashMap<Integer,String> > mydataset;
    private HashMap<Integer,String> hmessages;

    public final String NAME = "BLUETOOTH_CHAT_SERVICE";
    private final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private BluetoothAdapter mybluetooth;
    private sendreceive sr;
    static final int STATE_LISTENING=1;
    static final int STATE_CONNECTING=2;
    static final int STATE_CONNECTED=3;
    static final int STATE_CONNECTION_FAILED=4;
    static final int STATE_MESSAGE_RECEIVED=5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"vsdvcghsvdchgv");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger_manager);

        Intent intent1=getIntent();
        String str=intent1.getStringExtra("Mode");
        if(str.contentEquals("Server"))
        {
            mybluetooth=BluetoothAdapter.getDefaultAdapter();
            ServerClass serverClass = new ServerClass();
            serverClass.start();
            Toast.makeText(getApplicationContext(),"Connecting",Toast.LENGTH_SHORT).show();
        }
        else if(str.contentEquals("Client"))
        {
            mybluetooth=BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice bldevice=(BluetoothDevice)intent1.getParcelableExtra("Device");
            ClientClass obj = new ClientClass(bldevice);
            obj.start();
            Toast.makeText(getApplicationContext(),"Connecting",Toast.LENGTH_SHORT).show();

        }

        sendButton=(Button)findViewById(R.id.button_chatbox_send);
        sendText=(EditText)findViewById(R.id.edittext_chatbox);
        mRecyclerView = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        mLayoutManager = new LinearLayoutManager(this);
        hmessages=new HashMap<Integer, String>();
        hmessages.put(1,"bcjdjc");
        mydataset=new Vector<HashMap<Integer, String>>();
        mydataset.add(hmessages);
        hmessages=new HashMap<Integer, String>();
        hmessages.put(2,"xcbjhcb");
        mydataset.add(hmessages);
        mAdapter=new MessageAdapter(mydataset);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessages();
            }
        });

    }

    public void sendMessages()
    {
        String mystring=new String();
        mystring=sendText.getText().toString();
        if(mystring!=null)
        {
            hmessages=new HashMap<Integer, String>();
            hmessages.put(1,mystring);
            mydataset.add(hmessages);
            mAdapter=new MessageAdapter(mydataset);
            mRecyclerView.setAdapter(mAdapter);
            sendText.setText("");
            sr.write(mystring.getBytes());
        }
    }

    public  void receiveMessage(String recmessage)
    {
        hmessages=new HashMap<Integer, String>();
        hmessages.put(2,recmessage);
        mydataset.add(hmessages);
        mAdapter=new MessageAdapter(mydataset);
        mRecyclerView.setAdapter(mAdapter);
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what)
            {
                case STATE_LISTENING:
                    Toast.makeText(getApplicationContext(),"Listening",Toast.LENGTH_SHORT).show();
                    break;
                case STATE_CONNECTING:
                    Toast.makeText(getApplicationContext(),"Connecting",Toast.LENGTH_SHORT).show();
                    break;
                case STATE_CONNECTED:
                    Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_SHORT).show();
                    break;
                case STATE_CONNECTION_FAILED:
                    Toast.makeText(getApplicationContext(),"Connected Failed",Toast.LENGTH_SHORT).show();
                    break;
                case STATE_MESSAGE_RECEIVED:
                    byte[] readbuff = (byte[])msg.obj;
                    String tempmsg = new String(readbuff,0,msg.arg1);
                    receiveMessage(tempmsg);
                    break;
            }
            return true;
        }
    });



    private class ServerClass extends Thread
    {
        private BluetoothServerSocket servsock;

        public ServerClass()
        {
            try {
                servsock = mybluetooth.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        public void run()
        {
            BluetoothSocket socket=null;
            while(socket==null)
            {
                try {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTING;
                    handler.sendMessage(message);
                    socket = servsock.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                }
                if(socket!=null)
                {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);

                    sr = new sendreceive(socket);
                    sr.start();
                    break;
                }
            }
        }
    }
    private class ClientClass extends Thread
    {
        private BluetoothDevice device1;
        private BluetoothSocket socket;
        public ClientClass(BluetoothDevice device) {
            device1 = device;
            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void run()
        {
            try {
                socket.connect();
                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);

                sr = new sendreceive(socket);
                sr.start();
            }
            catch (IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }
    private class sendreceive extends Thread
    {
        private final BluetoothSocket socket;
        private final InputStream in;
        private final OutputStream out;
        public sendreceive(BluetoothSocket bsocket)
        {
            socket = bsocket;
            InputStream tempin=null;
            OutputStream tempout=null;


            try {
                tempin = socket.getInputStream();
                tempout = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            in = tempin;
            out = tempout;
        }

        public void run()
        {

            byte[] buffer = new byte[1024];
            int bytes;
            while(true)
            {
                try {
                    bytes = in.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        public void write(byte[] bytes)
        {
            try {
                out.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}

