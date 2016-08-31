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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class urunListesi extends AppCompatActivity {
    SharedPreferences sha;
    SharedPreferences.Editor edit;
    ListView list;
    TextView uyari;
    String kontrol="";
    List<urun> ls = new ArrayList<>();
    ArrayList<String> urunBaslikExtra = new ArrayList<String>();
    ArrayList<String> urunFiyatExtra = new ArrayList<String>();
    ArrayList<String> urunIDExtra = new ArrayList<String>();
    ArrayList<String> urunKisaAciklamaExtra = new ArrayList<String>();
    ArrayList<String> urunDetayExtra = new ArrayList<String>();
    static HashMap<String, String> urunResimUrlExtra = new HashMap<String, String>();
    static ArrayList<ArrayList<String>> resimler = new ArrayList<>();
    static int tiklanan = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urun_listesi);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        sha = getSharedPreferences("KullaniciCerezler", MODE_PRIVATE);
        edit = sha.edit();
        uyari = (TextView) findViewById(R.id.uyari2txt);
        uyari.setText("");
        list = (ListView) findViewById(R.id.urunListesiListView);
        list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        tiklanan = i;
                        Intent git = new Intent(urunListesi.this, urunDetay.class);
                        git.putExtra("urunBaslik", urunBaslikExtra.get(i));
                        git.putExtra("Fiyat", urunFiyatExtra.get(i));
                        git.putExtra("kisaAciklama", urunKisaAciklamaExtra.get(i));
                        git.putExtra("detay", urunDetayExtra.get(i));
                        git.putExtra("urunID", urunIDExtra.get(i));
                        startActivity(git);


                    }
                });
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String gelen = getIntent().getExtras().getString("altkategoriID", "");
        String url = "http://jsonbulut.com/json/product.php?ref=7b7392076900968d8e4ad78351ad55d3&start=0&count=100&categoryId=" + gelen;
        new urunListele(url, this).execute();
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
            Intent git3 = new Intent(urunListesi.this, sepet.class);
            startActivity(git3);
            return true;
        }
        if (id == R.id.adreslerim) {
            Intent git = new Intent(urunListesi.this, adreslerim.class);
            startActivity(git);
            return true;
        }
        if (id == R.id.ayarlarim) {
            Intent git = new Intent(urunListesi.this, ayarlarim.class);
            startActivity(git);
            return true;
        }
        if (id == R.id.cikis) {
            AlertDialog.Builder builder = new AlertDialog.Builder(urunListesi.this);
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

                    Intent git = new Intent(urunListesi.this, girisEkrani.class);
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

    class urunListele extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pro;
        String data = "";
        String url = "";

        public urunListele(String url, Activity ac) {
            this.url = url;
            pro = new ProgressDialog(ac);
            pro.setMessage("Lütfen Bekleyiniz !");
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
                Log.d("Json Hatası ", ex.toString());
            } finally {
                pro.dismiss();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void o) {
            super.onPostExecute(o);
            ls.clear();
            urunResimUrlExtra.clear();
            urunBaslikExtra.clear();
            urunFiyatExtra.clear();
            urunKisaAciklamaExtra.clear();
            urunDetayExtra.clear();
            urunIDExtra.clear();
            try {
                JSONObject obj = new JSONObject(data);
                JSONArray aurunler = obj.getJSONArray("Products");
                JSONObject ourunler = aurunler.getJSONObject(0);
                JSONArray abilgiler = ourunler.getJSONArray("bilgiler");
                int resimSayisi = 0;
                resimler.clear();
                for (int j = 0; j < abilgiler.length(); j++) {
                    JSONObject obilgiler = abilgiler.getJSONObject(j);
                    String urunBaslik = obilgiler.getString("productName");
                    String urunID = obilgiler.getString("productId");
                    String urunFiyat = obilgiler.getString("price");
                    String kaciklama = obilgiler.getString("brief");
                    String aciklama = obilgiler.getString("description");
                    JSONArray aUrunResim = obilgiler.getJSONArray("images");
                    ArrayList<String> re = new ArrayList<>();
                    for (int k = 0; k < aUrunResim.length(); k++) {
                        JSONObject ourunResimUrlExtra = aUrunResim.getJSONObject(k);
                        String surunResimUrlExtra = ourunResimUrlExtra.getString("normal");
                        re.add(surunResimUrlExtra);
                    }
                    resimler.add(re);
                    JSONObject oUrunResim = aUrunResim.getJSONObject(0);
                    String kurunResimUrl = oUrunResim.getString("thumb");
                    String burunResimUrl = "";
                    burunResimUrl = oUrunResim.getString("normal");
                    kontrol=burunResimUrl;
                    urunBaslikExtra.add(urunBaslik);
                    urunIDExtra.add(urunID.trim());
                    urunFiyatExtra.add(urunFiyat.trim());
                    urunKisaAciklamaExtra.add(kaciklama.trim());
                    urunDetayExtra.add(aciklama.trim());

                    urun ur = new urun("Ürün Adı : " + urunBaslik, burunResimUrl, " Fiyat : " + urunFiyat + " TL");
                    ls.add(ur);


                }
                urunListesiIcinAdapter adp = new urunListesiIcinAdapter(urunListesi.this, ls);
                list.setAdapter(adp);

            } catch (JSONException e) {
                e.printStackTrace();

            }
            if (kontrol.equals("")) {
                uyari.setText("Kategoride Ürün Bulunmamaktadır");
            }
        }
    }

}
