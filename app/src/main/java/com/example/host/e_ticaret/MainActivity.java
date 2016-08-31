package com.example.host.e_ticaret;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
SharedPreferences sha;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sha=getSharedPreferences("KullaniciCerezler",MODE_PRIVATE);

        Thread git=new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);

                    String userIDKontrol=sha.getString("ID","");
                    if (!userIDKontrol.equals("")){
                        Intent i=new Intent(MainActivity.this,anaKategoriler.class);
                        startActivity(i);
                    }else if (userIDKontrol.equals("")){
                        Intent i=new Intent(MainActivity.this,girisEkrani.class);
                        startActivity(i);

                    }

                }catch (Exception ex){
                    Log.d("Açılış Hatası : ",ex.toString());
                }finally {
                    finish();
                }
            }
        };
        git.start();




        }



    }

