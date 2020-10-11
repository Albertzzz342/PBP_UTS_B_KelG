package com.example.tubes_pbp2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import com.example.tubes_pbp2.databinding.ActivityPembayaranBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;

public class PembayaranActivity extends AppCompatActivity {

    //Data Binding
    private Mahasiswa mhs;
    private ActivityPembayaranBinding binding;

    //Hitung
    private MaterialTextView hitung_total;
    private MaterialButton hasilBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran);

        //Data Binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pembayaran);
        String strMhs = getIntent().getStringExtra("objMhs");
        Gson gson = new Gson();
        mhs = gson.fromJson(strMhs, Mahasiswa.class);
        binding.setMhs(mhs);
        binding.setActivity(this);

        Hitung();
    }

    private void Hitung(){
        double jenis;
        double fasilitas;
        double lama;
        String hasil;

        if (mhs.jenis.equals("Kos Biasa")){
            jenis=400000;
        }else {
            jenis=600000;
        }

        if (mhs.kamar.equals("Kamar Mandi Dalam")){
            fasilitas=100000;
        }else {
            fasilitas=0;
        }

        if (mhs.lama.equals("1 bulan")){
            lama=(jenis+fasilitas);
        }else if (mhs.lama.equals("6 bulan")){
            lama=(jenis+fasilitas)*6;
        }else {
            lama=(jenis+fasilitas)*12;
        }

        hasil = Double.toString(lama);
        hasilBtn = findViewById(R.id.coba);
        hasilBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitung_total = (MaterialTextView)findViewById(R.id.hitung_total);
                hitung_total.setText("Rp. " +hasil);
            }
        });
    }
}