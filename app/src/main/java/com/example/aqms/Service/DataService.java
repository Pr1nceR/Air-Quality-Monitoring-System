package com.example.aqms.Service;

import static com.example.aqms.Notification.App.CHANNEL_ID;
import static com.example.aqms.Notification.App.CHANNEL_ID_2;

import static java.lang.String.*;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.example.aqms.MainActivity;
import com.example.aqms.Model.CommonValueApplication;
import com.example.aqms.R;

import java.io.IOException;
import java.io.InputStream;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class DataService extends Service {

    byte[] readBuffer;
    Thread workerThread;
    InputStream mmInputStream;
    volatile boolean stopWork = false;
    TextView temptv,humtv,pm1tv,pm10tv,pm25tv,mp503tv,mhztv;
    CircularProgressIndicator temp_prog;
    CircularProgressIndicator hum_prog;
    //CircularProgressIndicator pm1_prog;
    CircularProgressIndicator pm10_prog;
    CircularProgressIndicator pm25_prog;
    CircularProgressIndicator mp503_prog;
    CircularProgressIndicator mhz_prog;

    private NotificationManagerCompat notificationManager;
    static final int STATE_MESSAGE_RECEIVED = 5;
    private static final String DEFAULT_PATTERN = "%d%%";


    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       // Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        mmInputStream = CommonValueApplication.getInputStream();
        notificationManager = NotificationManagerCompat.from(this);

        temptv = (TextView) CommonValueApplication.getTemptv();
        humtv = (TextView)  CommonValueApplication.getHumtv();
       // pm1tv = (TextView) CommonValueApplication.getPm1tv();
        pm10tv = (TextView) CommonValueApplication.getPm10tv();
        pm25tv = (TextView) CommonValueApplication.getPm25tv();
        mp503tv = (TextView) CommonValueApplication.getMp503tv();
        mhztv = (TextView) CommonValueApplication.getMhztv();

        temp_prog = (CircularProgressIndicator) CommonValueApplication.getTemp();
        hum_prog = (CircularProgressIndicator) CommonValueApplication.getHum_progress();
       // pm1_prog = (CircularProgressIndicator) CommonValueApplication.getPm1_progress();
        pm10_prog = (CircularProgressIndicator) CommonValueApplication.getPm10_progress();
        pm25_prog = (CircularProgressIndicator) CommonValueApplication.getPm25_progress();
        mp503_prog = (CircularProgressIndicator) CommonValueApplication.getMp503_progress();
        mhz_prog = (CircularProgressIndicator) CommonValueApplication.getMhz_progress();
        beginListenForData();
        return START_NOT_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void beginListenForData() {
        final Handler handler = new Handler();
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[]buffer = new byte[1024];
                int bytes;
                while(!Thread.currentThread().isInterrupted() && !stopWork){
                    try {
                        int bytesAvailable = mmInputStream.available();
                        if (bytesAvailable >0){
                            bytes = mmInputStream.read(buffer);
                            String temp = new String(buffer);
                            String[] strings = temp.split(",");

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println("tem:"+strings[0]);//temp
                                    System.out.println("hum:"+strings[1]);//hum
                                    System.out.println("mp503:"+strings[2]);//mp503
                                    System.out.println("CO2:"+strings[3]);//co2
                                    System.out.println("PM25:"+strings[4]);//pm25
                                    System.out.println("PM10: "+strings[5]);//pm10
                                    System.out.println("PM1: "+ strings[6]);//pm1
                                    foregroundService(strings[0],strings[1],strings[2],strings[3],strings[4],strings[5],strings[6]);
                                    circular_progress(strings[0],strings[1],strings[2],strings[3],strings[4],strings[5],strings[6]);
                                    alert_notification(strings[0],strings[1],strings[2],strings[3],strings[4],strings[5]);
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            ////
        });
        workerThread.start();
    }
    public void foregroundService(String temperature, String hum, String mp503, String co2, String pm25,String pm10, String pm1){
        float temp = Float.parseFloat(temperature);
        float humm =Float.parseFloat(hum);
        float voc = Float.parseFloat(mp503);
        float c = Float.parseFloat(co2);
        float pm2 = Float.parseFloat(pm25);
        float pm = Float.parseFloat(pm1);
        float pm3 = Float.parseFloat(pm10);

        RemoteViews collapsedView = new RemoteViews(getPackageName(),
                R.layout.notification_collapsed);
        RemoteViews expandedView = new RemoteViews(getPackageName(),
                R.layout.notification_expanded);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,notificationIntent,0);


        expandedView.setTextViewText(R.id.temp_notification,temperature);
        if(temp > 40){
            expandedView.setInt(R.id.temp_status,"setBackgroundColor",R.color.bad);
        }
        if(humm > 70){
           expandedView.setInt(R.id.hum_status,"setBackgroundColor",R.color.bad);
        }
       if(voc > 500){
            expandedView.setInt(R.id.mp503_status,"setBackgroundColor",R.color.bad);
        }
        if(c > 5000){
            expandedView.setInt(R.id.co2_status,"setBackgroundColor",R.color.bad);
        }
        if(pm2 > 110){
            expandedView.setInt(R.id.pm2_status,"setBackgroundColor",R.color.bad);
        }
        if(pm > 150){
            expandedView.setInt(R.id.pm1_status,"setBackgroundColor",R.color.bad);
        }
        if(pm3 > 180){
            expandedView.setInt(R.id.pm10_status,"setBackgroundColor",R.color.bad);
        }

        expandedView.setTextViewText(R.id.hum_notification,hum);
        expandedView.setTextViewText(R.id.co2_notification,co2);
        expandedView.setTextViewText(R.id.p_m1_notification,pm1);
        expandedView.setTextViewText(R.id.p_m2_5_notification,pm25);
        expandedView.setTextViewText(R.id.p_m10_notification,pm10);
        expandedView.setTextViewText(R.id.mp503_notification,mp503);
        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.finalicon)
                .setCustomBigContentView(expandedView)
                .setCustomContentView(collapsedView)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(2,notification);
    }

    public void circular_progress(String temperature, String hum, String mp503, String co2, String pm25,String pm10, String pm1){

        //temp
        temptv.setText((String.valueOf(temperature))+ " °C");
        temp_prog.setMaxProgress(100);
//        temp_prog.setProgressFormatter(new CircleProgressBar.ProgressFormatter() {
//            @Override
//            public CharSequence format(int i, int i1) {
//                return String.format(DEFAULT_PATTERN,(int) ((float) i / (float) i1 * 50));
//            }
//        });
//        temp_prog.setProgress((int) Float.parseFloat(valueOf(temperature)));
        temp_prog.setCurrentProgress((int)Float.parseFloat(valueOf(temperature)));
        //hum
        humtv.setText(String.valueOf(hum)+ " %");
        hum_prog.setCurrentProgress((int)Float.parseFloat(valueOf(hum)));
        //pm1
//        pm1tv.setText(String.valueOf(pm1));
        //pm1_prog.setCurrentProgress((int)Float.parseFloat(valueOf(pm1)));
        //pm10
        pm10_prog.setMaxProgress(180);
        pm10tv.setText(String.valueOf(pm10)+" µg/m3");
        pm10_prog.setCurrentProgress((int)Float.parseFloat(valueOf(pm10)));
        //pm25
        pm25_prog.setMaxProgress(110);
        pm25tv.setText(String.valueOf(pm25)+" µg/m3");
        pm25_prog.setCurrentProgress((int)Float.parseFloat(valueOf(pm25)));
        //co2
        mhz_prog.setMaxProgress(5000);
        mhztv.setText(String.valueOf(co2)+" PPM");
        mhz_prog.setCurrentProgress((int)Float.parseFloat(valueOf(co2)));
        //mp503
        mp503_prog.setCurrentProgress((int)Float.parseFloat(valueOf(mp503)));
        float voc = Float.parseFloat(mp503);
        if(voc < 100){
            mp503tv.setText("LOW");
        }
        else if (voc > 100 && voc < 150){
            mp503tv.setText("MODERATE");
        }
        else{
            mp503tv.setText("HIGH");
        }
    }
    public void alert_notification(String temperature, String hum, String mp503, String co2, String pm25,String pm10){
        float temp = Float.parseFloat(temperature);
        float humm =Float.parseFloat(hum);
        float voc = Float.parseFloat(mp503);
        float c = Float.parseFloat(co2);
        float pm2 = Float.parseFloat(pm25);
        float pm3 = Float.parseFloat(pm10);

        if(temp > 40){
           alert(temperature,hum,mp503,co2,pm25,pm10);
        }
        if(humm > 70){
            alert(temperature,hum,mp503,co2,pm25,pm10);
        }
        if(voc > 500){
            alert(temperature,hum,mp503,co2,pm25,pm10);
        }
        if(c > 2000){
            alert(temperature,hum,mp503,co2,pm25,pm10);
        }
        if(pm2 > 110){
            alert(temperature,hum,mp503,co2,pm25,pm10);
        }
        if(pm3 > 180){
            alert(temperature,hum,mp503,co2,pm25,pm10);
        }
    }
    public void alert(String temperature, String hum, String mp503, String co2, String pm25,String pm10){
        float temp = Float.parseFloat(temperature);
        float humm =Float.parseFloat(hum);
        float voc = Float.parseFloat(mp503);
        float c = Float.parseFloat(co2);
        float pm2 = Float.parseFloat(pm25);
        float pm3 = Float.parseFloat(pm10);


        RemoteViews expandedView = new RemoteViews(getPackageName(),
                R.layout.notification_expanded);
        RemoteViews collapsedView = new RemoteViews(getPackageName(),
                R.layout.notification_collapsed_2);

        expandedView.setTextViewText(R.id.temp_notification,temperature);

        if(temp > 40){
            expandedView.setInt(R.id.temp_status,"setBackgroundColor",R.color.bad);
        }
        if(humm > 70){
            expandedView.setInt(R.id.hum_status,"setBackgroundColor",R.color.bad);
        }
        if(voc > 500){
            expandedView.setInt(R.id.mp503_status,"setBackgroundColor",R.color.bad);
        }
        if(c > 2000){
            expandedView.setInt(R.id.co2_status,"setBackgroundColor",R.color.bad);
        }
        if(pm2 > 110){
            expandedView.setInt(R.id.pm2_status,"setBackgroundColor",R.color.bad);
        }
        if(pm3 > 180){
            expandedView.setInt(R.id.pm10_status,"setBackgroundColor",R.color.bad);
        }

        expandedView.setTextViewText(R.id.hum_notification,hum);
        expandedView.setTextViewText(R.id.co2_notification,co2);
        expandedView.setTextViewText(R.id.p_m2_5_notification,pm25);
        expandedView.setTextViewText(R.id.p_m10_notification,pm10);
        expandedView.setTextViewText(R.id.mp503_notification,mp503);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_2)
                .setSmallIcon(R.drawable.ic_android)
                .setCustomBigContentView(expandedView)
                .setSmallIcon(R.drawable.warning)
                .setCustomContentView(collapsedView)
                .build();
        notificationManager.notify(1, notification);

    }
}
