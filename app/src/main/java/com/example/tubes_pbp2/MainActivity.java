package com.example.tubes_pbp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tubes_pbp2.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import static com.example.tubes_pbp2.R.layout.activity_main;

public class MainActivity extends AppCompatActivity {

    //SHARED PREFERENCES
    private SharedPreferences preferences;
    public static final int mode= Activity.MODE_PRIVATE;

    private TextInputEditText editEmail, editPassword;
    private String emailText="";
    private String passwordText="";
    private MaterialButton signUpBtn, signInBtn;
    private FirebaseAuth firebaseAuth;

    private Mahasiswa mhs;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        editEmail = findViewById(R.id.email);
        editPassword = findViewById(R.id.password);
        signUpBtn = findViewById(R.id.signUpBtn);
        signInBtn = findViewById(R.id.signInBtn);
        loadPreferences();
        setCredentials();

        //Data Binding
        mhs = new Mahasiswa();
        binding.setMhs(mhs);
        binding.setActivity(this);

        //Firebase Authentitacion
        firebaseAuth = FirebaseAuth.getInstance();

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email, password;
                email = editEmail.getText().toString();
                password = editPassword.getText().toString();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(MainActivity.this,"Email invalid", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(TextUtils.isEmpty(password)){
                    Toast.makeText(MainActivity.this,"Password invalid", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(password.length()<6){
                    Toast.makeText(MainActivity.this,"Password kependekan", Toast.LENGTH_SHORT).show();
                    return;
                }
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Berhasil", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Gagal", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email, password;
                email = editEmail.getText().toString();
                password = editPassword.getText().toString();
                savePreferences();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(MainActivity.this,"Email invalid", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(TextUtils.isEmpty(password)){
                    Toast.makeText(MainActivity.this,"Password invalid", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(password.length()<6){
                    Toast.makeText(MainActivity.this,"Password kependekan", Toast.LENGTH_SHORT).show();
                    return;
                }
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this,"Berhasil", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(MainActivity.this, SignOut.class);
                            Gson gson = new Gson();
                            String strMhs = gson.toJson(mhs);
                            i.putExtra("objMhs", strMhs); //MENYISIPKAN DATA JSON STRING KE INTENT
                            startActivity(i);
                        } else {
                            Toast.makeText(MainActivity.this, "Gagal", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void setCredentials(){
        TextInputEditText editEmail= findViewById(R.id.email);
        TextInputEditText editPassword= findViewById(R.id.password);
        editEmail.setText(emailText);
        editPassword.setText(passwordText);
    }

    private void loadPreferences(){
        String name="credentials";
        preferences=getSharedPreferences(name,mode);
        if(preferences!=null){
            emailText=preferences.getString("emailText", "");
            passwordText=preferences.getString("passwordText","");
        }
    }

    private void savePreferences(){
        TextInputEditText editEmail= findViewById(R.id.email);
        TextInputEditText editPassword= findViewById(R.id.password);
        SharedPreferences.Editor editor=preferences.edit();
        if(!editEmail.getText().toString().isEmpty()&&!editPassword.getText().toString().isEmpty()){
            editor.putString("emailText", editEmail.getText().toString());
            editor.putString("passwordText",editPassword.getText().toString());
            editor.apply();
            Toast.makeText(this,"credentials saved",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"credentials not yet saved",Toast.LENGTH_SHORT).show();
        }
    }
}