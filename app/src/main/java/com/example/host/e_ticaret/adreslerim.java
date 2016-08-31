package com.example.host.e_ticaret;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.util.ArrayList;

public class adreslerim extends AppCompatActivity {
    SharedPreferences sha;
    SharedPreferences.Editor edit;
    static String musteriID = "";
    static String kontrol = "";
    ListView adresgoruntule;
    Button sil;
    ArrayList<urun> adresListesi = new ArrayList<urun>();
    static ArrayList<String> adresID = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adreslerim);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        adresgoruntule = (ListView) findViewById(R.id.adreslist);
        sil = (Button) findViewById(R.id.adresiSilbtn);
        sha = getSharedPreferences("KullaniciCerezler", MODE_PRIVATE);
        edit = sha.edit();
        musteriID = sha.getString("ID", "");
        adreslistele();

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
            Intent git3=new Intent(adreslerim.this,sepet.class);
            finish();
            startActivity(git3);
            return true;
        }
        if (id == R.id.adreslerim) {

            return true;
        }
        if (id == R.id.ayarlarim) {
            Intent git = new Intent(adreslerim.this, ayarlarim.class);
            finish();
            startActivity(git);
            return true;
        }
        if (id == R.id.cikis) {
            AlertDialog.Builder builder = new AlertDialog.Builder(adreslerim.this);
            builder.setTitle("Çıkış İşlemi");
            builder.setMessage("Çıkış Yapmak İstediğinizden Emin misiniz ?");
            builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Yes Durumunda
                    edit.remove("ID");
                    edit.remove("Adi");
                    edit.remove("Soyadi");
                    edit.remove("Mail");
                    edit.remove("Telefon");
                    edit.commit();

                    Intent git = new Intent(adreslerim.this, girisEkrani.class);
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


    public void adreseklemeislemi(View v) {
        finish();
        Intent i = new Intent(adreslerim.this, adresEkle.class);
        startActivity(i);
    }

    public void adreslistele() {
        adresID.clear();
        String url = "http://jsonbulut.com/json/addressList.php?ref=7b7392076900968d8e4ad78351ad55d3&musterilerID=" + musteriID;
        new adresler(url, this).execute();


    }

    class adresler extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pro;
        String data = "";
        String url = "";

        public adresler(String url, Activity ac) {
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
                JSONArray announcements = obj.getJSONArray("announcements").getJSONArray(0);
                JSONArray announcements2 = obj.getJSONArray("announcements").getJSONArray(0);
                //denetim yapılıyor
                for (int j = 0; j < announcements.length(); j++) {
                    JSONObject veri = announcements.getJSONObject(j);
                    String jjmusteriID = veri.getString("musterilerID");
                    String adressID = veri.getString("id");
                    String il = veri.getString("il");
                    String ilce = veri.getString("ilce");
                    String mahalle = veri.getString("Mahalle");
                    String adres = veri.getString("adres");
                    String kapino = veri.getString("kapiNo");
                    String not = veri.getString("not");
                    String tarih = veri.getString("tarih");
                    String duzenlenmisAdres = "("+(j + 1) + ". Adres)" + mahalle + " " + adres + " " + kapino + " " + ilce + " " + il + " \n" + "Not :" + not + "\nEklenme Tarihi : " + tarih;
                    urun ur = new urun(duzenlenmisAdres);
                    adresID.add(adressID);
                    adresListesi.add(ur);
                }
                urunAdapter adp = new urunAdapter(adreslerim.this, adresListesi);
                adresgoruntule.setAdapter(adp);
            } catch (JSONException e) {
                Log.d("Adres Liste Hata var:", e.toString());
                if (e.toString().equals("org.json.JSONException: Value false at 0 of type java.lang.Boolean cannot be converted to JSONArray")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(adreslerim.this);
                    builder.setTitle("Adres Ekleme Yönlendirme");
                    builder.setMessage("Kayıtlı Adres Bilgisi Bulunamadı Adres Eklemek İstermisiniz ?");
                    builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Yes Durumunda
                            finish();
                            Intent i = new Intent(adreslerim.this, adresEkle.class);
                            startActivity(i);
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // No Durumunda
                            finish();
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        }
    }
}
