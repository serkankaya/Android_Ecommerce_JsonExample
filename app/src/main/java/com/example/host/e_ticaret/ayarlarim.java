package com.example.host.e_ticaret;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

public class ayarlarim extends AppCompatActivity {
    EditText ad, soyad, tel, mail, eSifre,ySifre,ySifreTekrar;
    SharedPreferences sha;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayarlarim);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ad = (EditText) findViewById(R.id.adGuncelleTxt);
        soyad = (EditText) findViewById(R.id.soyadGuncelleTxt);
        tel = (EditText) findViewById(R.id.telGuncelleTxt);
        mail = (EditText) findViewById(R.id.mailGuncelleTxt);
        ySifre = (EditText) findViewById(R.id.yeniSifreGuncelleTxt);
        ySifreTekrar = (EditText) findViewById(R.id.yeniSifreTekrarGuncelleTxt);

        sha=getSharedPreferences("KullaniciCerezler",MODE_PRIVATE);
        editor=sha.edit();


        ad.setText(sha.getString("Adi",""));
        soyad.setText(sha.getString("Soyadi",""));
        tel.setText(sha.getString("Telefon",""));
        mail.setText(sha.getString("Mail",""));
        ySifre.requestFocus();
        /*Toast.makeText(ayarlarim.this, "KullaniciID : "+ sha.getString("ID",""), Toast.LENGTH_SHORT).show();*/

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getParentActivityIntent() == null) {
                    finish();
                } else {
                    NavUtils.navigateUpFromSameTask(this);
                }


        }
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.sepetim) {
            Intent git3=new Intent(ayarlarim.this,sepet.class);
            finish();
            startActivity(git3);
            return true;
        }
        if (id == R.id.adreslerim) {
            Intent git=new Intent(ayarlarim.this,adreslerim.class);
            finish();
            startActivity(git);
            return true;
        }
        if (id == R.id.ayarlarim) {

            return true;
        }
        if (id == R.id.cikis) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ayarlarim.this);
            builder.setTitle("Çıkış İşlemi");
            builder.setMessage("Çıkış Yapmak İstediğinizden Emin misiniz ?");
            builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Yes Durumunda
                    editor.remove("ID");
                    editor.remove("Adi");
                    editor.remove("Soyadi");
                    editor.remove("Mail");
                    editor.remove("Telefon");
                    editor.commit();

                    Intent git = new Intent(ayarlarim.this, girisEkrani.class);
                    finish();
                    startActivity(git);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // No Durumunda

                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void guncelle(View v) {
        String adi = ad.getText().toString().trim();
        String soyadi = soyad.getText().toString().trim();
        String telefonu = tel.getText().toString().trim();
        String maili = mail.getText().toString().trim();
        String yenisifresi = ySifre.getText().toString().trim();
        String yenisifresitekrar = ySifreTekrar.getText().toString().trim();
        if (adi.equals("")){
            Toast.makeText(ayarlarim.this, "Lütfen Adınızı Giriniz ...", Toast.LENGTH_SHORT).show();
            ad.requestFocus();
        }else if (soyadi.equals("")){
            Toast.makeText(ayarlarim.this, "Lütfen Soyadınızı Giriniz ...", Toast.LENGTH_SHORT).show();
            soyad.requestFocus();
        }else if (maili.equals("")){
            Toast.makeText(ayarlarim.this, "Lütfen Mailinizi Giriniz ...", Toast.LENGTH_SHORT).show();
            mail.requestFocus();
        }else if (telefonu.equals("")){
            Toast.makeText(ayarlarim.this, "Lütfen Telefon Numaranızı Giriniz ...", Toast.LENGTH_SHORT).show();
            tel.requestFocus();
        }else if (!yenisifresi.equals(yenisifresitekrar)){
            Toast.makeText(ayarlarim.this, "Şifre Tekrarı Uyuşmuyor ...", Toast.LENGTH_SHORT).show();
            ySifre.setText("");
            ySifreTekrar.setText("");
            ySifre.requestFocus();
        }else{

            String url = "http://jsonbulut.com/json/userSettings.php?ref=7b7392076900968d8e4ad78351ad55d3&userName="+adi+"&userSurname="+soyadi+"&userMail="+maili+"&userPhone="+telefonu+"&userPass="+yenisifresi+"&userId="+sha.getString("ID","");
            Log.d("Yazildi : ",url);
            new guncelle(url, this).execute();
        }


    }

    //json sunucu ziyareti gerçekleşiyor ...
    class guncelle extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pro;
        String data = "";
        String url = "";

        public guncelle(String url, Activity ac) {
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
                    //Güncelleme başarılı aşağıdaki işlemleri yap
                    editor.putString("Adi",ad.getText().toString().trim());
                    editor.putString("Soyadi",soyad.getText().toString().trim());
                    editor.putString("Mail",mail.getText().toString().trim());
                    editor.putString("Telefon",tel.getText().toString().trim());
                    editor.commit();
                    finish();
                    onBackPressed();

                    /*Intent git=new Intent(getApplication(),anaKategoriler.class);
                    startActivity(git);*/

                } else {
                    Toast.makeText(getApplication(), oj.getString("mesaj"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Log.d("Ayarlarim JsonHata var:",e.toString());
            }


            Log.d("Gelen Data : ", data);
        }
    }
}
