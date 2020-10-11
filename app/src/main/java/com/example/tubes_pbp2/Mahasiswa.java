package com.example.tubes_pbp2;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingConversion;

public class Mahasiswa extends BaseObservable {
    public String email;
    public String nama;
    public String jenis;
    public String kamar;
    public String lama;

    public Mahasiswa() { }

    public Mahasiswa(String email, String nama, String jenis, String kamar, String lama) {
        this.email = email;
        this.nama = nama;
        this.jenis = jenis;
        this.kamar = kamar;
        this.lama = lama;
    }

    @Bindable
    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
        notifyPropertyChanged(BR.jenis);
    }

    @Bindable
    public String getKamar() {
        return kamar;
    }

    public void setKamar(String kamar) {
        this.kamar = kamar;
        notifyPropertyChanged(BR.kamar);
    }

    @Bindable
    public String getLama() {
        return lama;
    }

    public void setLama(String lama) {
        this.lama = lama;
        notifyPropertyChanged(BR.lama);
    }

    @Bindable
    public String getEmail() {return email; }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);
    }

    @Bindable
    public String getNama() {return nama; }

    public void setNama(String nama) {
        this.nama = nama;
        notifyPropertyChanged(BR.nama);
    }
}
