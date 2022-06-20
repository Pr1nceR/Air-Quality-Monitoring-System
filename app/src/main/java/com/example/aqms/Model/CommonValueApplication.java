package com.example.aqms.Model;

import android.app.Application;
import android.view.View;
import android.widget.TextView;

import java.io.InputStream;

public class CommonValueApplication extends Application {
    public static InputStream minpuStream;
    public static TextView temptv,humtv,pm1tv,pm25tv,mp503tv,mhztv,pm10tv;
    public static View view;
    public static View temp_progress;
    public static View hum_progress;
    public static View pm1_progress;
    public static View pm25_progress;
    public static View pm10_progress;

    public static View getPm10_progress() {
        return pm10_progress;
    }

    public static void setPm10_progress(View pm10_progress) {
        CommonValueApplication.pm10_progress = pm10_progress;
    }

    public static View mp503_progress;
    public static View mhz_progress;

    public static TextView getPm10tv() {
        return pm10tv;
    }

    public static void setPm10tv(TextView pm10tv) {
        CommonValueApplication.pm10tv = pm10tv;
    }

    public static TextView getHumtv() {
        return humtv;
    }

    public static void setHumtv(TextView humtv) {
        CommonValueApplication.humtv = humtv;
    }

    public static TextView getPm1tv() {
        return pm1tv;
    }

    public static void setPm1tv(TextView pm1tv) {
        CommonValueApplication.pm1tv = pm1tv;
    }

    public static TextView getPm25tv() {
        return pm25tv;
    }

    public static void setPm25tv(TextView pm25tv) {
        CommonValueApplication.pm25tv = pm25tv;
    }

    public static TextView getMp503tv() {
        return mp503tv;
    }

    public static void setMp503tv(TextView mp503tv) {
        CommonValueApplication.mp503tv = mp503tv;
    }

    public static TextView getMhztv() {
        return mhztv;
    }

    public static void setMhztv(TextView mhztv) {
        CommonValueApplication.mhztv = mhztv;
    }

    public static TextView getTemptv() {
        return temptv;
    }

    public static void setTemptv(TextView temptv) {
        CommonValueApplication.temptv = temptv;
    }

    public static View getView(){
        return view;
    }
    public static void setView(View view){
        CommonValueApplication.view = view;
    }

    public static View getHum_progress() {
        return hum_progress;
    }

    public static void setHum_progress(View hum_progress) {
        CommonValueApplication.hum_progress = hum_progress;
    }

    public static View getTemp() {
        return temp_progress;
    }


    public static void setTemp(View temp) {
        CommonValueApplication.temp_progress = temp;
    }

    public static View getPm1_progress() {
        return pm1_progress;
    }

    public static void setPm1_progress(View voc) {
        CommonValueApplication.pm1_progress = voc;
    }

    public static View getPm25_progress() {
        return pm25_progress;
    }

    public static void setPm25_progress(View pm25) {
        CommonValueApplication.pm25_progress = pm25;
    }

    public static View getMp503_progress() {
        return mp503_progress;
    }

    public static void setMp503_progress(View mo503) {
        CommonValueApplication.mp503_progress = mo503;
    }

    public static View getMhz_progress() {
        return mhz_progress;
    }

    public static void setMhz_progress(View mhz) {
        CommonValueApplication.mhz_progress = mhz;
    }

    public static void setInputStream(InputStream is){
        minpuStream =is;
    }

    public static InputStream getInputStream(){
        return minpuStream;
    }

}
