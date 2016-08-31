package com.example.host.e_ticaret;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

public class kayit extends AppCompatActivity {
    EditText ad, soyad, tel, mail, sifre;
    SharedPreferences sha;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit);

        ad = (EditText) findViewById(R.id.adtxt);
        soyad = (EditText) findViewById(R.id.soyadtxt);
        tel = (EditText) findViewById(R.id.teltxt);
        mail = (EditText) findViewById(R.id.mailtxt);
        sifre = (EditText) findViewById(R.id.sifretxt);
        sha=getSharedPreferences("KullaniciCerezler",MODE_PRIVATE);
        editor=sha.edit();
    }

    public void fncKayitYap(View v) {
        String adi = ad.getText().toString().trim();
        String soyadi = soyad.getText().toString().trim();
        String telefonu = tel.getText().toString().trim();
        String maili = mail.getText().toString().trim();
        String sifresi = sifre.getText().toString().trim();
        if (adi.equals("")){
            Toast.makeText(kayit.this, "Lütfen Adınızı Giriniz ...", Toast.LENGTH_SHORT).show();
            ad.requestFocus();
        }else if (soyadi.equals("")){
            Toast.makeText(kayit.this, "Lütfen Soyadınızı Giriniz ...", Toast.LENGTH_SHORT).show();
            soyad.requestFocus();
        }else if (telefonu.equals("")){
            Toast.makeText(kayit.this, "Lütfen Telefon Numaranızı Giriniz ...", Toast.LENGTH_SHORT).show();
            tel.requestFocus();
        }else if (maili.equals("")){
            Toast.makeText(kayit.this, "Lütfen Mailinizi Giriniz ...", Toast.LENGTH_SHORT).show();
            mail.requestFocus();
        }else if (sifresi.equals("")){
            Toast.makeText(kayit.this, "Lütfen Şifrenizi Giriniz ...", Toast.LENGTH_SHORT).show();
            sifre.requestFocus();
        }else{
            String url = "http://jsonbulut.com/json/userRegister.php?ref=7b7392076900968d8e4ad78351ad55d3&userName=" + adi + "&userSurname=" + soyadi + "&userPhone=" + telefonu + "&userMail=" + maili + "&userPass=" + sifresi + "";
            Log.d("Yazildi : ",url);
            new girisYap(url, this).execute();
        }


    }

    class girisYap extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pro;
        String data = "";
        String url = "";

        public girisYap(String url, Activity ac) {
            this.url = url;
            pro = new ProgressDialog(ac);
            pro.setMessage("Lütfen Bekleyiniz ...");
            pro.show();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... param) {
            try {
                data = Jsoup.connect(url).ignoreContentType(true).execute().body();
            } catch (Exception ex) {
                Log.d("Json hatası : ", ex.toString());
            } finally {
                pro.dismiss();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void o) {
            super.onPostExecute(o);
            // data çözümleme
            try {
                JSONObject obj = new JSONObject(data);
                JSONArray arr = obj.getJSONArray("user");
                JSONObject oj = arr.getJSONObject(0);
                //denetim yapılıyor
                if (oj.getBoolean("durum")) {
                    Toast.makeText(getApplication(), oj.getString("mesaj"), Toast.LENGTH_SHORT).show();
                    //kayıt başarılı aşağıdaki işlemleri yap
                    editor.putString("ID",oj.getString("kullaniciId"));
                    editor.putString("Adi",ad.getText().toString().trim());
                    editor.putString("Soyadi",soyad.getText().toString().trim());
                    editor.putString("Mail",mail.getText().toString().trim());
                    editor.putString("Telefon",tel.getText().toString().trim());
                    editor.commit();

                    Log.d("Giriş ID : ", oj.getString("kullaniciId"));
                    finish();
                    Intent git=new Intent(kayit.this,anaKategoriler.class);
                    startActivity(git);
                } else {
                    Toast.makeText(getApplication(), oj.getString("mesaj"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Log.d("Kayıt Hata var:",e.toString());
            }


            Log.d("Gelen Data : ", data);
        }
    }
}
