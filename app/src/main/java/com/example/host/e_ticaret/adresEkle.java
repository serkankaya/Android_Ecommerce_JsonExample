package com.example.host.e_ticaret;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

public class adresEkle extends AppCompatActivity {
EditText il,ilce,mahalle,adresbilgisi,kapino,notbilgi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adres_ekle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        il=(EditText)findViewById(R.id.iltxt);
        ilce=(EditText)findViewById(R.id.ilcetxt);
        mahalle=(EditText)findViewById(R.id.mahalletxt);
        adresbilgisi=(EditText)findViewById(R.id.adresbilgisitxt);
        kapino=(EditText)findViewById(R.id.kapinotxt);
        notbilgi=(EditText)findViewById(R.id.notbilgisitxt);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if (getParentActivityIntent()==null){
                    finish();
                    Intent git=new Intent(adresEkle.this,adreslerim.class);
                    startActivity(git);
                }else{
                    NavUtils.navigateUpFromSameTask(this);
                }


        }
        return super.onOptionsItemSelected(item);
    }
    public void adresekle(View v){
        String ili = il.getText().toString().trim();
        String ilcesi = ilce.getText().toString().trim();
        String mahallesi = mahalle.getText().toString().trim();
        String adresbilgi = adresbilgisi.getText().toString().trim();
        String kapinosu = kapino.getText().toString().trim();
        String notbilgisi = notbilgi.getText().toString().trim();
        if (ili.equals("")){
            Toast.makeText(adresEkle.this, "Lütfen İl Bilgisini Giriniz ...", Toast.LENGTH_SHORT).show();
            il.requestFocus();
        }else if (ilcesi.equals("")){
            Toast.makeText(adresEkle.this, "Lütfen İlçe Bilgisini Giriniz ...", Toast.LENGTH_SHORT).show();
            ilce.requestFocus();
        }else if (mahallesi.equals("")){
            Toast.makeText(adresEkle.this, "Lütfen Mahalle Bilgisini Giriniz ...", Toast.LENGTH_SHORT).show();
            mahalle.requestFocus();
        }else if (adresbilgi.equals("")){
            Toast.makeText(adresEkle.this, "Lütfen Adres Bilgisini Giriniz ...", Toast.LENGTH_SHORT).show();
            adresbilgisi.requestFocus();
        }else if (kapinosu.equals("")){
            Toast.makeText(adresEkle.this, "Lütfen Kapı Nosu Bilgisini Giriniz ...", Toast.LENGTH_SHORT).show();
            kapino.requestFocus();
        }else if (notbilgisi.equals("")){
            Toast.makeText(adresEkle.this, "Lütfen Not Bilgisini Giriniz ...", Toast.LENGTH_SHORT).show();
            notbilgi.requestFocus();
        }
        else{
            String url = "http://jsonbulut.com/json/addressAdd.php?ref=7b7392076900968d8e4ad78351ad55d3&musterilerID="+adreslerim.musteriID+"&il="+ili+"&ilce="+ilcesi+"&Mahalle="+mahallesi+"&adres="+adresbilgi+"&kapiNo="+kapinosu+"&notBilgi="+notbilgisi;
            new adresekle(url, this).execute();

        }

    }

    class adresekle extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pro;
        String data = "";
        String url = "";

        public adresekle(String url, Activity ac) {
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
                String sonuc=arr.toString();
                //denetim yapılıyor
                if (sonuc.contains("true")) {
                    Toast.makeText(getApplication(), "Adres Ekleme Başarılı", Toast.LENGTH_SHORT).show();
                    finish();
                    Intent git=new Intent(adresEkle.this,adreslerim.class);
                    startActivity(git);

                } else {
                    Toast.makeText(getApplication(), "Adres Ekleme Başarısız", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Log.d("Adres Ekleme Hata var:",e.toString());
            }


            Log.d("Gelen Data : ", data);
        }
    }
}
