package com.example.cobapplj;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class PesanActivity extends AppCompatActivity {
    public final int harga1=39000;
    public final int harga2=46000;
    public final int harga3=42000;
    public int qty1=0;
    public int qty2=0;
    public int qty3=0;
    public int total;
    public String notesStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesan);
    }

    public void onPlus1Clk(View view){
        qty1+=1;
        TextView txtQty1=findViewById(R.id.qty1);
        txtQty1.setText(Integer.toString(qty1));
        //update harga
        total=qty1*harga1+qty2*harga2+qty3*harga3;
        TextView txtTotal=findViewById(R.id.totalHarga);
        String totalStr=String.format("%,d", total);
        txtTotal.setText("Rp "+totalStr);
    }

    public void onMin1Clk(View view){
        qty1=(qty1-1);
        if (qty1<0){
            qty1=0;
        }
        TextView txtQty1=findViewById(R.id.qty1);
        txtQty1.setText(Integer.toString(qty1));
        //update harga
        total=qty1*harga1+qty2*harga2+qty3*harga3;
        TextView txtTotal=findViewById(R.id.totalHarga);
        String totalStr=String.format("%,d", total);
        txtTotal.setText("Rp "+totalStr);
    }

    public void onPlus2Clk(View view){
        qty2+=1;
        TextView txtQty1=findViewById(R.id.qty2);
        txtQty1.setText(Integer.toString(qty2));
        //update harga
        total=qty1*harga1+qty2*harga2+qty3*harga3;
        TextView txtTotal=findViewById(R.id.totalHarga);
        String totalStr=String.format("%,d", total);
        txtTotal.setText("Rp "+totalStr);
    }

    public void onMin2Clk(View view){
        qty2=(qty2-1);
        if (qty2<0){
            qty2=0;
        }
        TextView txtQty1=findViewById(R.id.qty2);
        txtQty1.setText(Integer.toString(qty2));
        //update harga
        total=qty1*harga1+qty2*harga2+qty3*harga3;
        TextView txtTotal=findViewById(R.id.totalHarga);
        String totalStr=String.format("%,d", total);
        txtTotal.setText("Rp "+totalStr);
    }
    public void onPlus3Clk(View view){
        qty3+=1;
        TextView txtQty1=findViewById(R.id.qty3);
        txtQty1.setText(Integer.toString(qty3));
        //update harga
        total=qty1*harga1+qty2*harga2+qty3*harga3;
        TextView txtTotal=findViewById(R.id.totalHarga);
        String totalStr=String.format("%,d", total);
        txtTotal.setText("Rp "+totalStr);
    }

    public void onMin3Clk(View view){
        qty3=(qty3-1);
        if (qty3<0){
            qty3=0;
        }
        TextView txtQty1=findViewById(R.id.qty3);
        txtQty1.setText(Integer.toString(qty3));
        //update harga
        total=qty1*harga1+qty2*harga2+qty3*harga3;
        TextView txtTotal=findViewById(R.id.totalHarga);
        String totalStr=String.format("%,d", total);
        txtTotal.setText("Rp "+totalStr);
    }

    public void onBayarClk (View view){
        TextView txtNotes=findViewById(R.id.notes);
        notesStr=txtNotes.getText().toString();

        total=qty1*harga1+qty2*harga2+qty3*harga3;

        if (total<=0){
            AlertDialog.Builder builder = new AlertDialog.Builder(PesanActivity.this);
            builder.setTitle("Cek lagi ya");
            builder.setMessage("Belum pesan apapun ya? Yuk pesan sesuatu");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            String pesanan = "";
            String quantity = "";

            if (qty1>0){
                pesanan += "A";
                quantity += qty1;
            }

            if (qty2>0){
                if (pesanan.length()!=0){
                    pesanan += ";";
                    quantity += ";";
                }
                pesanan += "B";
                quantity += qty2;
            }

            if (qty3>0){
                if (pesanan.length()!=0){
                    pesanan += ";";
                    quantity += ";";
                }
                pesanan += "C";
                quantity += qty3;
            }

            Intent intent = new Intent(PesanActivity.this, KonfirmasiPembayaran.class);
            intent.putExtra("totalPembayaran", total);
            intent.putExtra("pesanan",pesanan);
            intent.putExtra("quantity",quantity);
            intent.putExtra("notes",notesStr);
            startActivity(intent);
        }
    }

    public void onBackButtonClk(View view){
        finish();
    }

}