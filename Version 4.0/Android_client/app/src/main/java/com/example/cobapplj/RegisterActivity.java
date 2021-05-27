package com.example.cobapplj;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

public class RegisterActivity extends AppCompatActivity {

    ProgressBar progressBar;
    String username;
    String password;
    String confirm_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText username_field = (EditText)findViewById(R.id.username_field);
        final EditText password_field = (EditText)findViewById(R.id.password_field);
        final EditText confirm_password_field = (EditText)findViewById(R.id.confirm_password_field);

        progressBar = (ProgressBar)findViewById(R.id.progress_bar);


        Button register_button = (Button)findViewById(R.id.register_button);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("socket","register");

                username = username_field.getText().toString();
                password = password_field.getText().toString();
                confirm_password = confirm_password_field.getText().toString();

                if (username.length()==0 || password.length()==0 || confirm_password_field.length()==0) {
                    progressBar.setVisibility(View.GONE);
                    Functions.CreateAlertDialogBox(RegisterActivity.this, "Error", "Please fill every field");
                } else if (password.length()<8||confirm_password.length()<8){
                    Functions.CreateAlertDialogBox(RegisterActivity.this,"Error","Password must be 8 characters or more");
                } else if (password.equals(confirm_password)){
                    String hashtext = Functions.HashPassword(password,"SHA-224");

                    JSONObject send_json = new JSONObject();
                    try {
                        send_json.put("request","register");
                        send_json.put("username",username);
                        send_json.put("password",hashtext);

                        String json_string = send_json.toString();

                        progressBar.setVisibility(View.VISIBLE);

                        new NetworkTask().execute(json_string);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Functions.CreateAlertDialogBox(RegisterActivity.this,"Error","Please insert the same password");
                }

            }
        });

        TextView register_login_text = (TextView)findViewById(R.id.register_login_text);
        register_login_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
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
                Functions.CreateAlertDialogBox(RegisterActivity.this,"Error","Please check your connection");
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
                    if (json_response.get("response").equals("register") && json_response.get("status").equals("berhasil")) {
                        SharedPreferences.Editor user_pref_editor = getSharedPreferences("USER_PREF", MODE_PRIVATE).edit();
                        user_pref_editor.putString("username", username);
                        user_pref_editor.putInt("saldo",0);
                        user_pref_editor.apply();

                        MainActivity.StartRefresh(getApplicationContext());

                        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Functions.CreateAlertDialogBox(RegisterActivity.this,"Error","Username sudah digunakan");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Functions.CreateAlertDialogBox(RegisterActivity.this,"Maaf","Sepertinya terjadi kesalahan. Silakan coba lagi.");
                }
            }
            progressBar.setVisibility(View.GONE);
        }
    }


}