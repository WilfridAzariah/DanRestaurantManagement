package com.example.cobapplj;

public class History {

    String orderno;
    String timestamp;
    String paket;
    String quantity;
    String harga;
    String notes;
    String status;

    public History(){
        this.orderno = "";
        this.timestamp = "";
        this.paket = "";
        this.quantity = "";
        this.harga = "";
        this.notes = "";
        this.status = "";
    }

    public History (String orderno,String timestamp,String paket,String quantity,String harga,String notes,String status){
        this.orderno = orderno;
        this.timestamp = timestamp;
        this.paket = paket;
        this.quantity = quantity;
        this.harga = harga;
        this.notes = notes;
        this.status = status;
    }

    public String getOrderno() {
        return orderno;
    }

    public String getTimestamp(){
        return this.timestamp;
    }

    public String getPaket(){
        return this.paket;
    }

    public String getQuantity(){
        return this.quantity;
    }

    public String getHarga() {
        return this.harga;
    }

    public String getNotes() {
        return notes;
    }

    public String getStatus() {
        return status;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public void setTimestamp(String timestamp){
        this.timestamp = timestamp;
    }

    public void setPaket(String paket) {
        this.paket = paket;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPesanan(){
        String pesanan = "";

        String[] list_paket = paket.split(";");
        String[] list_quantity = quantity.split(";");

        if (list_paket.length==1){
            pesanan = paket + ":" + quantity;
        } else if (list_paket.length==2){
            pesanan = list_paket[0] + ":" + list_quantity[0] + ", " + list_paket[1] + ":" + list_quantity[1];
        } else if (list_paket.length==3){
            pesanan = list_paket[0] + ":" + list_quantity[0] + ", " + list_paket[1] + ":" + list_quantity[1] + ", " + list_paket[2] + ":" + list_quantity[2];
        }

        return pesanan;
    }

    public String FormatToHistoryLine(){
        return this.orderno + "|" + this.timestamp + "|" + this.paket + "|" + this.quantity + "|" + this.harga + "|" + this.status + "|" + this.notes + "|\n";
    }
}
