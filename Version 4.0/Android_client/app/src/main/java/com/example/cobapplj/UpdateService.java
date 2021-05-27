package com.example.cobapplj;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

public class UpdateService extends IntentService {

    SSLSocket nsocket; //Network Socket
    InputStream nis; //Network Input Stream
    OutputStream nos; //Network Output Stream

    String update_request_string;

    public UpdateService(){
        super("UpdateService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d("Refresh","Start Intent");
        SharedPreferences user_pref = getSharedPreferences("USER_PREF",MODE_PRIVATE);
        String username = user_pref.getString("username","");
        JSONObject update_request_json = new JSONObject();
        try {
            update_request_json.put("request", "showHistory");
            update_request_json.put("username",username);
            update_request_string = update_request_json.toString();
        } catch (Exception e){
            e.printStackTrace();
            return;
        }
        try {
            Log.i("Refresh", "Creating socket");
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, new X509TrustManager[]{new X509TrustManager(){
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            SSLSocketFactory socketFactory = sslContext.getSocketFactory();
            nsocket = (SSLSocket) socketFactory.createSocket(Constants.server_addr,Constants.server_port);
            nsocket.setSoTimeout(5000);
        } catch (Exception e){
            e.printStackTrace();
            return;
        }
        String result = "";
        try {
            if (nsocket.isConnected()) {
                nis = nsocket.getInputStream();
                nos = nsocket.getOutputStream();
                nos.write(update_request_string.getBytes());
                byte[] buffer = new byte[4096];
                int read = nis.read(buffer, 0, 4096); //This is blocking
                while (read != -1) {
                    byte[] tempdata = new byte[read];
                    System.arraycopy(buffer, 0, tempdata, 0, read);
                    result = new String(tempdata);
                    read = -1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = "";
            return;
        }

        if (result.substring(result.length()-1)!="}"){
            result = result + "}";
        }
        Log.d("Refresh",result);
        try {
            JSONObject json_response = new JSONObject(result);
            String isi = json_response.get("isi").toString();
            isi = isi.substring(1, isi.length() - 1);
            String[] arrayHistory = isi.split("]");
            List<History> history_list = new ArrayList<History>();
            boolean isReady = true;
            for (String history : arrayHistory) {
                String cleaned_history = history;
                int left_index = history.indexOf("[");
                if (left_index != -1) {
                    cleaned_history = history.substring(left_index + 1);
                }
                String[] history_content = cleaned_history.split(",");
                String orderno = history_content[0];
                String paket = history_content[1].substring(1,history_content[1].length()-1);
                String quantity = history_content[2].substring(1,history_content[2].length()-1);
                String status = history_content[3].substring(1,history_content[3].length()-1);
                String timestamp = history_content[4].substring(1,history_content[4].length()-1);
                String harga = history_content[5];
                String notes = history_content[7].substring(1,history_content[7].length()-1);

                History history_instance = new History(orderno,timestamp,paket,quantity,harga,notes,status);
                history_list.add(history_instance);
            }
            List<History> last_history = Functions.ReadHistoryFile(UpdateService.this);

            List<String> result_list = Functions.CompareHistoryList(last_history,history_list);

            if (result_list.size()!=0){
                for (String compare_message : result_list){
                    Functions.CreateNotification(getApplicationContext(),1,"Notification",compare_message);
                }
            }
            Functions.WriteHistoryFile(UpdateService.this,history_list);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            nis.close();
            nos.close();
            nsocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
