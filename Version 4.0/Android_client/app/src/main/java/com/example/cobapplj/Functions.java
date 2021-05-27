package com.example.cobapplj;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Functions {
    public static void CreateAlertDialogBox(Context context, String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void CreateNotification(Context context, int id, String title, String message){
        Log.d("Notification",message);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Channel 1","Channel 1",NotificationManager.IMPORTANCE_LOW);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"Channel 1");

            Intent notificationIntent = new Intent(context, HomeActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,id,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentIntent(pendingIntent);
            builder.setSmallIcon(R.drawable.logo1);
            builder.setContentTitle(title);
            builder.setContentText(message);

            notificationManager.notify(id,builder.build());
        }
    }

    public static String HashPassword(String password,String method){
        String hashtext;

        try {
            // getInstance() method is called with algorithm SHA-224
            MessageDigest md = MessageDigest.getInstance(method);

            // digest() method is called
            // to calculate message digest of the input string
            // returned as array of byte
            byte[] messageDigest = md.digest(password.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            hashtext = no.toString(16);

            // Add preceding 0s to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
        }
        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return hashtext;
    }

    public static void WriteTextFile(Context context, String filename, String content){
        try {
            File f = new File(context.getFilesDir() + "/" + filename);
            if (!f.exists()){
                f.createNewFile();
            }

            FileOutputStream fout = new FileOutputStream(context.getFilesDir() + "/" + filename);
            fout.write(content.getBytes());
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String ReadTextFile(Context context, String filename){
        String temp = "";
        try {
            File f = new File(context.getFilesDir() + "/" + filename);
            if (f.exists()){
                FileInputStream fin = new FileInputStream(context.getFilesDir() + "/" + filename);
                int c;

                while( (c = fin.read()) != -1){
                    temp = temp + (char) c;
                }

                fin.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return temp;
    }

    public static void WriteHistoryFile(Context context, List<History> history_list){
        String content = "";
        for (History history : history_list){
            String line = history.FormatToHistoryLine();
            content = content + line;
        }
        WriteTextFile(context,"log.txt",content);
    }

    public static List<History> ReadHistoryFile(Context context){
        List<History> history_list = new ArrayList<History>();
        String content = ReadTextFile(context,"log.txt");
        if (content.length()>0) {
            String[] lines = content.split("\n");
            for (String line : lines){
                String[] history_content = line.split("\\|");
                String orderno = history_content[0];
                String timestamp = history_content[1];
                String paket = history_content[2];
                String quantity = history_content[3];
                String harga = history_content[4];
                String status = history_content[5];

                String notes;
                try {
                    notes = history_content[6];
                } catch (ArrayIndexOutOfBoundsException e){
                    notes = "";
                }

                History history_instance = new History(orderno,timestamp,paket,quantity,harga,notes,status);

                history_list.add(history_instance);
            }
        }

        return history_list;
    }

    public static List<String> CompareHistoryList(List<History> before_list, List<History> after_list){
        List<String> result = new ArrayList<String>();

        for (History before_hist : before_list){
            String orderno1 = before_hist.getOrderno();
            History cohistory = null;
            for (History after_hist : after_list){
                if (after_hist.getOrderno().equals(orderno1)){
                    cohistory = after_hist;
                    break;
                }
            }

            if (cohistory!=null){
                if (!before_hist.getStatus().equals(cohistory.getStatus())){
                    result.add("Order no " + before_hist.getOrderno() + " is " + cohistory.getStatus());
                }
            }
        }

        return result;
    }
}
