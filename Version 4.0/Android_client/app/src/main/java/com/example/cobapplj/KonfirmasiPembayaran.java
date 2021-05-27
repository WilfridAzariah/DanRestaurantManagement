package com.example.cobapplj;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
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

public class KonfirmasiPembayaran extends AppCompatActivity {

    private String pesanan;
    private String quantity;
    private String notes;

    private String username;
    private String password;

    private int total;
    private int saldo;
    private int sisaSaldo;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfirmasi_pembayaran);

        //Print total pembayaran
        Intent rIntent = getIntent();
        total = rIntent.getIntExtra("totalPembayaran", 0);
        pesanan = rIntent.getStringExtra("pesanan");
        quantity = rIntent.getStringExtra("quantity");
        notes = rIntent.getStringExtra("notes");

        SharedPreferences user_pref = getSharedPreferences("USER_PREF",MODE_PRIVATE);
        username = user_pref.getString("username","");
        saldo = user_pref.getInt("saldo",-1);

        TextView txtTotal=findViewById(R.id.totalTxt);
        String totalStr=String.format("%,d", total);
        txtTotal.setText("Rp "+totalStr);

        TextView txtSaldo=findViewById(R.id.saldoTxt);
        String saldoStr=String.format("%,d", saldo);
        txtSaldo.setText("Rp "+saldoStr);

        sisaSaldo=saldo-total;
        TextView txtSisa=findViewById(R.id.sisaSaldoTxt);
        String sisaSaldoStr=String.format("%,d", sisaSaldo);
        txtSisa.setText("Rp "+sisaSaldoStr);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    public void onKonfirmasi(View view){
        //Cek Password dan saldo. kalau benar, pindah ke activity selanjutnya
        if (sisaSaldo<0){
            Functions.CreateAlertDialogBox(KonfirmasiPembayaran.this,"Gagal","Maaf saldo anda tidak cukup");
        } else {
            EditText editTextPassword = (EditText) findViewById(R.id.editTextPassword);
            password = editTextPassword.getText().toString();

            if (password.length()==0){
                Functions.CreateAlertDialogBox(KonfirmasiPembayaran.this,"Error","Please insert password");
            } else {
                String hashtext = Functions.HashPassword(password, "SHA-224");

                JSONObject send_json = new JSONObject();
                try {
                    send_json.put("request", "pesan");
                    send_json.put("username", username);
                    send_json.put("password", hashtext);
                    send_json.put("paket", pesanan);
                    send_json.put("quantity", quantity);
                    send_json.put("notes", notes);
                    send_json.put("totalHarga", total);

                    String json_string = send_json.toString();

                    progressBar.setVisibility(View.VISIBLE);

                    new NetworkTask().execute(json_string);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
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

        public void CancelRefresh(){

        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("Asynctask",s);
            if (s.equals("timeout")){
                Functions.CreateAlertDialogBox(KonfirmasiPembayaran.this,"Error","Please check your connection");
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
                    if (json_response.get("response").equals("pesan") && json_response.get("status").equals("berhasil")) {
                        String order_no = json_response.get("orderno").toString();
                        AlertDialog.Builder builder = new AlertDialog.Builder(KonfirmasiPembayaran.this);
                        builder.setTitle("Berhasil");
                        builder.setMessage("Pesananmu sudah tercatat dengan order no "+order_no);
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(KonfirmasiPembayaran.this,HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        Functions.CreateAlertDialogBox(KonfirmasiPembayaran.this,"Error","Sepertinya password salah");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Functions.CreateAlertDialogBox(KonfirmasiPembayaran.this,"Maaf","Sepertinya terjadi kesalahan. Silakan coba lagi.");
                }
            }
            progressBar.setVisibility(View.GONE);
        }
    }
}