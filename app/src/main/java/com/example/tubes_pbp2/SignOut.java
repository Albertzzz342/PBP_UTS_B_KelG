package com.example.tubes_pbp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tubes_pbp2.databinding.ActivitySignOutBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class SignOut extends AppCompatActivity
implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener
{
    private static final String TAG = "SignOutActivity";
    private static final int REQUEST_CODE = 100;

    //Data Binding
    private Mahasiswa mhs;
    private ActivitySignOutBinding binding;

    //Map
    private FloatingActionButton mapFab;

    //Dropdown
    private String[] list_jenis = {"Kos Eksklusif", "Kos Biasa"};
    private String[] list_kamarMandi = {"Kamar Mandi Dalam", "Kamar Mandi Luar"};
    private String[] list_lamaSewa = {"1 bulan", "6 bulan", "1 tahun"};

    //Notifikasi
    private String CHANNEL_ID = "Channel 1";
    private MaterialButton signOutBtn, profileBtn, pilihPaketBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Data Binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_out);
        String strMhs = getIntent().getStringExtra("objMhs");
        Gson gson = new Gson();
        mhs = gson.fromJson(strMhs, Mahasiswa.class);
        binding.setMhs(mhs);
        binding.setActivity(this);

        //Map
        Maps();

        //Dropdown
        DropdownJenis();
        DropdownKamarMandi();
        DropdownLamaSewa();

        //Notifikasi
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Halo")
                .setContentText("Selamat datang di kos saya")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
        SignOut();
        ProfileBtn();
        PilihPaket();
    }

    private void Maps(){
        LocationPermission();
        mapFab = findViewById(R.id.fab_map);
        mapFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent maps = new Intent(SignOut.this, Peta.class);
                startActivity(maps);
            }
        });
    }

    @AfterPermissionGranted(REQUEST_CODE)
    private void LocationPermission(){
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION};

        if(EasyPermissions.hasPermissions(this, permission)){
            Toast.makeText(this, "Akses Lokasi diizinkan", Toast.LENGTH_SHORT).show();
        }else {
            EasyPermissions.requestPermissions(this, "Akses Lokasi tidak diizinkan",
                    REQUEST_CODE, permission);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(REQUEST_CODE, permissions,grantResults,this);
    }


    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }


    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)){
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel 1";
            String description = "This is Channel 1";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void SignOut()
    {
        signOutBtn = findViewById(R.id.signOutBtn);
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                createNotificationChannel();
                NotificationCompat.Builder builder = new NotificationCompat.Builder(SignOut.this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle("Selamat tinggal")
                        .setContentText("Jangan lupa bayar !!!")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(0, builder.build());
                finish();
            }
        });
    }

    private void ProfileBtn(){
        profileBtn = findViewById(R.id.profileBtn);
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(SignOut.this, ProfileActivity.class);
                startActivity(profile);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    private void DropdownJenis(){
        AutoCompleteTextView dropdown_jenis = (AutoCompleteTextView) findViewById(R.id.dropdown_jenis);
        ArrayAdapter<String> adapter_jenis = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, list_jenis);
        dropdown_jenis.setAdapter(adapter_jenis);
        dropdown_jenis.setOnItemSelectedListener(this);
        dropdown_jenis.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String itemJenis = adapterView.getItemAtPosition(i).toString();
            }
        });
    }

    private void DropdownKamarMandi(){
        AutoCompleteTextView dropdown_kamarMandi = (AutoCompleteTextView) findViewById(R.id.dropdown_kamarMandi);
        ArrayAdapter<String> adapter_kamarMandi = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, list_kamarMandi);
        dropdown_kamarMandi.setAdapter(adapter_kamarMandi);
        dropdown_kamarMandi.setOnItemSelectedListener(this);
        dropdown_kamarMandi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String itemKamarMandi = adapterView.getItemAtPosition(i).toString();
            }
        });
    }

    private void DropdownLamaSewa(){
        AutoCompleteTextView dropdown_lamaSewa = (AutoCompleteTextView) findViewById(R.id.dropdown_lamaSewa);
        ArrayAdapter<String> adapter_lamaSewa = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, list_lamaSewa);
        dropdown_lamaSewa.setAdapter(adapter_lamaSewa);
        dropdown_lamaSewa.setOnItemSelectedListener(this);
        dropdown_lamaSewa.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String itemLamaSewa = adapterView.getItemAtPosition(i).toString();
            }
        });
    }

    private void PilihPaket(){
        pilihPaketBtn = findViewById(R.id.pilihPaketBtn);
        pilihPaketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pembayaran = new Intent(SignOut.this, PembayaranActivity.class);
                Gson gson = new Gson();
                String strMhs = gson.toJson(mhs);
                pembayaran.putExtra("objMhs", strMhs); //MENYISIPKAN DATA JSON STRING KE INTENT
                startActivity(pembayaran);
            }
        });
    }
}