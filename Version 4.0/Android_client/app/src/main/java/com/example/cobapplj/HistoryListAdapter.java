package com.example.cobapplj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class HistoryListAdapter extends ArrayAdapter<History> {

    private List<History> listHistory;
    private Context context;
    private int layout;

    public HistoryListAdapter(Context context, int layout, List<History> listHistory){
        super(context, layout, listHistory);
        this.listHistory = listHistory;
        this.context = context;
        this.layout = layout;
    }

    // https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView
    // https://agung-setiawan.com/android-membuat-custom-listview/
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        HistoryHolder holder;

        View v = convertView;

        if (v == null) {
            v = LayoutInflater.from(context).inflate(layout, parent, false);
        }

        holder = new HistoryHolder();
        holder.history_pesanan = v.findViewById(R.id.history_pesanan);
        holder.history_time = v.findViewById(R.id.history_time);
        holder.history_harga = v.findViewById(R.id.history_harga);
        holder.history_notes = v.findViewById(R.id.history_notes);
        holder.history_status = v.findViewById(R.id.history_status);
        holder.history_orderno = v.findViewById(R.id.history_orderno);

        // Ambil data scheduler
        History history = listHistory.get(position);

//        // Ubah timestamp menjadi tanggal lalu tampilkan
//        Date date = new Date(history.getTimestamp()*1000L);
//        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
//        String string_date = df.format(date);

        holder.history_time.setText(history.getTimestamp());
        holder.history_pesanan.setText("Pesanan : "+history.getPesanan());
        holder.history_harga.setText("Harga : Rp "+history.getHarga());
        holder.history_notes.setText("Notes : "+history.getNotes());
        holder.history_status.setText("Status : "+history.getStatus());
        holder.history_orderno.setText("Orderno : "+history.getOrderno());

        return v;
    }

    static class HistoryHolder{
        TextView history_orderno;
        TextView history_time;
        TextView history_pesanan;
        TextView history_harga;
        TextView history_notes;
        TextView history_status;
    };
}
