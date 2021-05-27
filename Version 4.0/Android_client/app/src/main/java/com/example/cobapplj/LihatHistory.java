package com.example.cobapplj;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

public class LihatHistory extends AppCompatActivity {

    ProgressBar progressBar;
    List<History> listHistory = new ArrayList<History>();
    HistoryListAdapter adapter;
    TextView info_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lihat_history);

        SharedPreferences user_pref = getSharedPreferences("USER_PREF",MODE_PRIVATE);
        String username = user_pref.getString("username","");

        //Back Button
        ImageView btn_kembali=(ImageView) findViewById(R.id.back_icon);
        btn_kembali.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view) {
                Intent intent = new Intent(LihatHistory.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        info_text = (TextView) findViewById(R.id.info_text);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        listHistory = Functions.ReadHistoryFile(LihatHistory.this);
        if (listHistory.size()!=0){
            progressBar.setVisibility(View.GONE);
            info_text.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            info_text.setVisibility(View.VISIBLE);
        }

        ListView listView = (ListView) findViewById(R.id.listView);
        adapter = new HistoryListAdapter(LihatHistory.this, R.layout.list_history,listHistory);
        listView.setAdapter(adapter);

        if (listHistory.size()!=0){
            progressBar.setVisibility(View.GONE);
            info_text.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            info_text.setVisibility(View.VISIBLE);
        }

        JSONObject send_json = new JSONObject();
        try {
            send_json.put("request", "showHistory");
            send_json.put("username", username);

            String json_string = send_json.toString();

            progressBar.setVisibility(View.VISIBLE);

            new NetworkTask().execute(json_string);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class NetworkTask extends AsyncTask<String, byte[], String> {

        SSLSocket nsocket; //Network Socket
        InputStream nis; //Network Input Stream
        OutputStream nos; //Network Output Stream

        @Override
        protected String doInBackground(String... params) { //This runs on a different thread
            try {
                Log.i("AsyncTask", "doInBackground: Creating socket");
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
            } catch (SocketTimeoutException e){
                return "timeout";
            } catch (Exception e){
                e.printStackTrace();
            }
            String result = "";
            try {
                if (nsocket.isConnected()) {
                    nis = nsocket.getInputStream();
                    nos = nsocket.getOutputStream();
                    nos.write(params[0].getBytes());
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
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("Asynctask",s);
            if (s.equals("timeout")){
                Functions.CreateAlertDialogBox(LihatHistory.this,"Error","Please check your connection");
            } else {
                if (s.substring(s.length()-1)!="}"){
                    s = s + "}";
                }

                try {
                    nis.close();
                    nos.close();
                    nsocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    JSONObject json_response = new JSONObject(s);
                    if (json_response.get("response").equals("showHistory")) {
                        listHistory.clear();
                        adapter.notifyDataSetChanged();

                        String isi = json_response.get("isi").toString();
                        Integer jumlah = Integer.parseInt(json_response.get("jumlah").toString());
                        Log.d("Asynctask", isi);
                        if (jumlah<=0){
                            info_text.setText("Tidak ada history");
                            info_text.setVisibility(View.VISIBLE);
                        } else {
                            info_text.setVisibility(View.GONE);
                            isi = isi.substring(1, isi.length() - 1);
                            String[] arrayHistory = isi.split("]");
                            for (String history : arrayHistory) {
                                String cleaned_history = history;
                                int left_index = history.indexOf("[");
                                if (left_index != -1) {
                                    cleaned_history = history.substring(left_index + 1);
                                }
                                Log.d("Asynctask", cleaned_history);
                                String[] history_content = cleaned_history.split(",");
                                String orderno = history_content[0];
                                String paket = history_content[1].substring(1, history_content[1].length() - 1);
                                String quantity = history_content[2].substring(1, history_content[2].length() - 1);
                                String status = history_content[3].substring(1, history_content[3].length() - 1);
                                String timestamp = history_content[4].substring(1, history_content[4].length() - 1);
                                String harga = history_content[5];
                                String notes = history_content[7].substring(1, history_content[7].length() - 1);

                                History history_instance = new History(orderno,timestamp, paket, quantity, harga, notes, status);
                                listHistory.add(history_instance);
                                adapter.notifyDataSetChanged();
                            }

                            if (listHistory.size() > 0) {
                                Functions.WriteHistoryFile(LihatHistory.this, listHistory);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Functions.CreateAlertDialogBox(LihatHistory.this,"Maaf","Sepertinya terjadi kesalahan. Silakan coba lagi.");
                }
            }
            progressBar.setVisibility(View.GONE);
        }
    }
}