package android.com.fridvoiceoutput.activity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.com.fridvoiceoutput.R;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.UUID;

public class ScaneActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private static final int RECIEVE_MESSAGE = 1;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    //TODO change the device bluetooth address
//    private static String address = "98:D3:32:20:90:60";
//    private static String address = "20:16:01:05:33:54";
//    private static String address = "98:D3:32:30:98:F5";
//    private static String address = "98:D3:35:00:B5:3B";
    private static String address = "98:D3:31:80:75:36";
    Context context = ScaneActivity.this;
    Handler handler;
    int count = 0;
    boolean checked = false;
    String strUIID = "";
    private Toolbar toolbar;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder sb = new StringBuilder();
    private ConnectedThread mConnectedThread;
    private String TAG = ScaneActivity.class.getSimpleName();
    private TextView txtUIID;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scane);

        init();
        tts = new TextToSpeech(context, this);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                count++;
                String readMessage = (String) msg.obj;
                sb.append(readMessage);
                Log.e(TAG, " Read message is as " + readMessage);

                if (!sb.equals("") && sb.length() > 15) {

                    strUIID = sb.toString().substring(0, 11);
                    txtUIID.append("\n" + strUIID);
                    Log.e(TAG, "UIID: " + strUIID);

                    if (!checked) {

//                        checkUIID();

                           /* startActivity(new Intent(NFCScanActivity.this, LoginActivity.class));
                            Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();

                            finish();
*/
                        checked = true;
                    }
                }

            }
        };

        checkBTState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "...onResume - try connect...");

//        checkBTState();

        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        System.out.println("## onResume :");

        Log.e(TAG, " Bluetooth device values " + device);
        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Log.e(TAG, " Socket inti error " + e.getMessage());
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
            System.out.println("## e:" + e);
        }

        Log.e(TAG, " SCOKET " + btSocket);

        btAdapter.cancelDiscovery();

        Log.e("######", "##...Connecting...");
        try {
            btSocket.connect();
            Log.e("####", "##....Connection ok...");
        } catch (IOException e) {
            Log.e(TAG, "CONNECTION ERROR " + e.getMessage());
            try {
                System.out.println("## socket close e :" + e);
                btSocket.close();
            } catch (IOException e2) {
                System.out.println("## IOException:" + e2);
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        // Create a data stream so we can talk to server.
        Log.e("#####", "###...Create Socket...");

        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
        Log.e("#####", "###...Create thread...");

    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d("####", "##...In onPause()...");

        try {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if (Build.VERSION.SDK_INT >= 10) {
            try {
              /*  final Method  m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);*/

                final Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                return (BluetoothSocket) m.invoke(device, 1);

            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection", e);
            }
        }
        return device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    private void init() {
//        txtScaning = (TextView) findViewById(R.id.txtScanning);
        txtUIID = (TextView) findViewById(R.id.txtUIID);
//        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Checking availability of UIID......");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.scane);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void checkBTState() {

        if (btAdapter == null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
                System.out.println("## Bluetooth ON:");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private void errorExit(String title, String message) {
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
//                tts.speak("WelCome To Android World", TextToSpeech.QUEUE_FLUSH, null);
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    @Override
    public void onDestroy() {

        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[512];  // buffer store for the stream
            int bytes; // bytes returned from read()
            System.out.println("## waiting to read:");
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);// Get number of bytes and message in "buffer"
                    Log.e(TAG, " Message m is as " + readMessage);
                    if (tts != null) {
                        switch (readMessage) {
                            case "M":
                                tts.speak("WelCome To Mechanical Departments", TextToSpeech.QUEUE_FLUSH, null);
                                break;
                            case "C":
                                tts.speak("WelCome To Computer Department", TextToSpeech.QUEUE_FLUSH, null);
                                break;

                            case "E":
                                tts.speak("WelCome To Electronics Department", TextToSpeech.QUEUE_FLUSH, null);
                                break;
                        }

                    }
                    handler.obtainMessage(RECIEVE_MESSAGE, bytes, -1, readMessage).sendToTarget();     // Send to message queue Handler
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String message) {
            Log.d(TAG, "...Data to send: " + message + "...");

            System.out.println("## write message :");

            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
                System.out.println("## IOException write message :" + e);
            }
        }
    }


}
