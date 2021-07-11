package com.example.deepakbulani.bluetoothmessenger2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.*;
import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public  BluetoothAdapter bladapter;
    private Set<BluetoothDevice> pairedDevices;
    private ListView list1;
    private Button button_1;
    private Button button_2;
    private Button button_3;
    private TextView text1;
    private Intent blintent;
    ArrayList<String> stringArrayList;
    ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button_1 = (Button) findViewById(R.id.button);
        button_2 = (Button) findViewById(R.id.button2);
        button_3= (Button) findViewById(R.id.button3);
        list1 = (ListView) findViewById(R.id.listView);
        bladapter = BluetoothAdapter.getDefaultAdapter();
        //text1 = (TextView) findViewById(R.id.editText);
        //text1.setBackgroundColor(Color.GREEN);
        button_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createConnection();
            }
        });
        button_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discoverDevices();
            }
        });

        list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice bl=null;
                int p=0;
                for( BluetoothDevice bl1:pairedDevices)
                {
                    if(p==position) {
                        bl = bl1;
                        break;
                    }
                    p++;
                }

                startActivityInClientMode(bl);
            }
        });

        button_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityInServerMode();
            }
        });

    }

    public void startActivityInClientMode(BluetoothDevice bl)
    {
        Intent intent1=new Intent(this,MessengerManager.class);
        intent1.putExtra("Mode","Client");
        intent1.putExtra("Device",bl);
        startActivity(intent1);
    }

    public void startActivityInServerMode()
    {
        Intent intent2=new Intent(this,MessengerManager.class);
        intent2.putExtra("Mode","Server");
        startActivity(intent2);
    }

    private void discoverDevices() {

        if(bladapter==null)
        {
            Toast.makeText(getApplicationContext(),"Bluetooth service not available in your device",Toast.LENGTH_SHORT).show();
            finish();
        }
        else
        {
            if(bladapter.isEnabled())
                bladapter.disable();
            if(!bladapter.isEnabled())
            {
                blintent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(blintent,2);
            }
        }



    }
    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                stringArrayList.add(deviceName);
                arrayAdapter.notifyDataSetChanged();
            }
        }
    };


    public void createConnection()
    {
        if(bladapter==null)
        {
            Toast.makeText(getApplicationContext(),"Bluetooth service not available in your device",Toast.LENGTH_SHORT).show();
            finish();
        }
        else
        {
            if(bladapter.isEnabled())
                bladapter.disable();
            if(!bladapter.isEnabled())
            {
                blintent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(blintent,1);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1)
        {
            if(resultCode==RESULT_OK)
            {
                Toast.makeText(getApplicationContext(),"Bluetooth service enabled",Toast.LENGTH_SHORT).show();
            }
            else if(resultCode==RESULT_CANCELED)
            {
                Toast.makeText(getApplicationContext(),"Bluetooth enable request cancelled",Toast.LENGTH_SHORT).show();
            }
            pairedDevices=bladapter.getBondedDevices();
            stringArrayList=new ArrayList<String>();
            for(BluetoothDevice device: pairedDevices)
            {
                stringArrayList.add(device.getName());
            }
            text1.setText("PAIRED DEVICES");
            if(stringArrayList.size()>0) {
                arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, stringArrayList);
                list1.setAdapter(arrayAdapter);
            }
        }
        else if(requestCode==2)
        {
            if(resultCode==RESULT_OK)
            {
                Toast.makeText(getApplicationContext(),"Bluetooth service enabled",Toast.LENGTH_SHORT).show();
            }
            else if(resultCode==RESULT_CANCELED)
            {
                Toast.makeText(getApplicationContext(),"Bluetooth enable request cancelled",Toast.LENGTH_SHORT).show();
            }

            bladapter.startDiscovery();
            // Register for broadcasts when a device is discovered.
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            text1.setText("DISCOVERED DEVICES");
            stringArrayList=new ArrayList<String>();
            arrayAdapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,stringArrayList);
            list1.setAdapter(arrayAdapter);
            registerReceiver(mReceiver, filter);

        }
    }




}

