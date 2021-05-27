package com.example.cobapplj;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

public class HomeActivity extends AppCompatActivity {

    TextView saldo_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final SharedPreferences user_pref = getSharedPreferences("USER_PREF", MODE_PRIVATE);

        String username = user_pref.getString("username",null);
        Integer saldo = user_pref.getInt("saldo",-1);

        JSONObject send_json = new JSONObject();
        try {
            send_json.put("request", "search");
            send_json.put("tipe", "username");
            send_json.put("username",username);

            String json_string = send_json.toString();

            new NetworkTask().execute(json_string);
        } catch (Exception e){
            e.printStackTrace();
        }

        //Pesan Button
        Button btn_pesan=(Button)findViewById(R.id.but_pesan);
        btn_pesan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View arg0) {
                Intent intent = new Intent(HomeActivity.this, PesanActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Order History Button
        Button btn_history=(Button)findViewById(R.id.but_history);
        btn_history.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View arg0) {
                Intent intent = new Intent(HomeActivity.this, LihatHistory.class);
                startActivity(intent);
                finish();
            }
        });

        //Logout Button
        Button btn_logout=(Button)findViewById(R.id.but_logout);
        btn_logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view) {
                SharedPreferences.Editor user_pref_editor = user_pref.edit();
                user_pref_editor.putString("username","");
                user_pref_editor.putInt("saldo",-1);
                user_pref_editor.apply();

                MainActivity.CancelRefresh(getApplicationContext());

                try {
                    File f = new File(getFilesDir() + "/log.txt");
                    f.delete();
                } catch (Exception e){
                    e.printStackTrace();
                }

                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        TextView hi_text = (TextView) findViewById(R.id.hi_text);
        hi_text.setText("Hi "+username);

        saldo_text = (TextView) findViewById(R.id.saldo_text);
        if (saldo!=-1){
            saldo_text.setText("Saldo : Rp " + String.format("%,d",saldo));
        } else {
            saldo_text.setText("Saldo : Rp -");
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
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("Error");
                builder.setMessage("Please check your connection");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                if (s.length()==0){
                    return;
                }
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
                    if (json_response.get("response").equals("search") && json_response.get("status").equals("berhasil")) {
                        String saldo_str = json_response.get("isi").toString();
                        Integer saldo = Integer.parseInt(saldo_str);

                        SharedPreferences.Editor user_pref_editor = getSharedPreferences("USER_PREF", MODE_PRIVATE).edit();
                        user_pref_editor.putInt("saldo",saldo);
                        user_pref_editor.apply();

                        saldo_text.setText("Saldo : Rp " + saldo_str);
                    } else {
                        Functions.CreateAlertDialogBox(HomeActivity.this,"Error","Please check your credentials");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
