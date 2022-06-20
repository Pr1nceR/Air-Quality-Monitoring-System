package com.example.aqms;

import static com.example.aqms.Notification.App.CHANNEL_ID_2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.example.aqms.Model.CommonValueApplication;
import com.example.aqms.Service.DataService;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class MainActivity extends AppCompatActivity {

    private NotificationManagerCompat notificationManager;

    byte[] readBuffer;
    volatile boolean stopWork = false;
    Thread workerThread;


    InputStream mmInputStream;
    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket mmSocket;
    int REQUEST_ENABLE_BLUETOOTH = 1;
    TextView temptv,humtv,pm1tv,pm10tv,pm25tv,mp503tv,mhztv;
    private static final String TAG = "tag";

    CircularProgressIndicator temp_progress,hum_progress,pm1_progress,pm10_progress,pm25_progress
            ,mp503_progress,mhz_progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temp_progress = findViewById(R.id.temp_progress);
        hum_progress = findViewById(R.id.hum_progress);
       // pm1_progress = findViewById(R.id.pm1_progress);
        pm10_progress = findViewById(R.id.p_m10_progress);
        pm25_progress = findViewById(R.id.p_m2_5_progress);
        mp503_progress = findViewById(R.id.mp503_progress);
        mhz_progress = findViewById(R.id.mhz19c_progress);

        temptv = findViewById(R.id.temp_textView);
        humtv = findViewById(R.id.hum_textView);
       // pm1tv = findViewById(R.id.pm1_textView);
        pm10tv = findViewById(R.id.p_m10_textView);
        pm25tv = findViewById(R.id.p_m2_5_textView);
        mp503tv = findViewById(R.id.mp503_textView);
        mhztv = findViewById(R.id.mhz19c_textView);

        pm10_progress.setMaxProgress(180);
        pm25_progress.setMaxProgress(110);
        mhz_progress.setMaxProgress(5000);
        mp503_progress.setMaxProgress(1024);
        temp_progress.setMaxProgress(60);
        hum_progress.setMaxProgress(100);

        CommonValueApplication.setTemptv(temptv);
        CommonValueApplication.setHumtv(humtv);
        //CommonValueApplication.setPm1tv(pm1tv);
        CommonValueApplication.setPm10tv(pm10tv);
        CommonValueApplication.setPm25tv(pm25tv);
        CommonValueApplication.setMp503tv(mp503tv);
        CommonValueApplication.setMhztv(mhztv);

        CommonValueApplication.setTemp(temp_progress);
        CommonValueApplication.setHum_progress(hum_progress);
        //CommonValueApplication.setPm1_progress(pm1_progress);
        CommonValueApplication.setPm10_progress(pm10_progress);
        CommonValueApplication.setPm25_progress(pm25_progress);
        CommonValueApplication.setMp503_progress(mp503_progress);
        CommonValueApplication.setMhz_progress(mhz_progress);

        notificationManager = NotificationManagerCompat.from(this);
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Log.i(TAG, "onSaveInstanceState");
        temp_progress = findViewById(R.id.temp_progress);
        //pm1_progress = findViewById(R.id.pm1_progress);
        pm25_progress = findViewById(R.id.p_m2_5_progress);
        mp503_progress = findViewById(R.id.mp503_progress);
        mhz_progress = findViewById(R.id.mhz19c_progress);




        CommonValueApplication.setTemp(temp_progress);
        CommonValueApplication.setPm1_progress(pm1_progress);
        CommonValueApplication.setPm25_progress(pm25_progress);
        CommonValueApplication.setMp503_progress(mp503_progress);
        CommonValueApplication.setMhz_progress(mhz_progress);
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Log.i(TAG, "onRestoreState");
        temp_progress = findViewById(R.id.temp_progress);
        //pm1_progress = findViewById(R.id.pm1_progress);
        pm25_progress = findViewById(R.id.p_m2_5_progress);
        mp503_progress = findViewById(R.id.mp503_progress);
        mhz_progress = findViewById(R.id.mhz19c_progress);

        CommonValueApplication.setTemp(temp_progress);
        CommonValueApplication.setPm1_progress(pm1_progress);
        CommonValueApplication.setPm25_progress(pm25_progress);
        CommonValueApplication.setMp503_progress(mp503_progress);
        CommonValueApplication.setMhz_progress(mhz_progress);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.bt_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            openBT();

        }
        else if(resultCode == RESULT_CANCELED){
            Toast.makeText(getApplicationContext(), "Bluetooth must be enabled", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void openBT(){
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        BluetoothDevice hc05 = btAdapter.getRemoteDevice("98:D3:31:80:40:1A");
        try {
            mmSocket = hc05.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmInputStream = mmSocket.getInputStream();
        } catch (IOException e) {
            Toast.makeText(this,"Cant connect please try again", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        if(mmSocket.isConnected()){
            Intent serviceIntent = new Intent(this, DataService.class);
            CommonValueApplication.setInputStream(mmInputStream);
            startService(serviceIntent);
          //  beginListenForData();
            Toast.makeText(MainActivity.this, "Connected To AQMS", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.connect_bt:
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                bluetoothAdapter.disable();
                if (!bluetoothAdapter.isEnabled()){
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_ENABLE_BLUETOOTH);

                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}