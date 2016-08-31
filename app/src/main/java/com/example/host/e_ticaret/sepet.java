package com.example.host.e_ticaret;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

public class sepet extends AppCompatActivity {
    SharedPreferences sha;
    SharedPreferences.Editor edit;
    ListView list;
    List<urun> ls = new ArrayList<>();
    static ArrayList<String> sepetID = new ArrayList<String>();
    TextView siparistoplami, uyari;
    Button siparisTamamlaBtn;
    ArrayList<String> urunID = new ArrayList<>();
    String musteriID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sepet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        sepetID.clear();
        list = (ListView) findViewById(R.id.sepetlistview);
        siparistoplami = (TextView) findViewById(R.id.siparistoplami);
        uyari = (TextView) findViewById(R.id.uyaritxt);
        siparisTamamlaBtn = (Button) findViewById(R.id.siparistamamlaBtn);
        uyari.setText("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sha = getSharedPreferences("KullaniciCerezler", MODE_PRIVATE);
        edit = sha.edit();
        sepetVerileriniGetir();
        siparisTamamlaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //musteriID,urunID[],productID=0;
                String url3 = "",siparisVerilenUrunler="";


                    for (int h = 0; h < urunID.size(); h++) {
                        url3 +=urunID.get(h).toString()+",";
                    }

                    if (!url3.equals("")){
                        siparisVerilenUrunler=url3.substring(0,url3.length()-1);
                        Log.d("urunler : ",siparisVerilenUrunler);
                        String url = "http://jsonbulut.com/json/orderForm.php?ref=7b7392076900968d8e4ad78351ad55d3&customerId=" + musteriID + "&productId="+siparisVerilenUrunler+"&html="+siparisVerilenUrunler;
                        Log.d("Url : ",url);
                        new siparisTamamlama(url, sepet.this).execute();
                    }else {
                        Toast.makeText(sepet.this, "Sepette Ürün Bulunmamaktadır", Toast.LENGTH_SHORT).show();
                    }
            }
        });


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
            return true;
        }
        if (id == R.id.adreslerim) {
            Intent git = new Intent(sepet.this, adreslerim.class);
            finish();
            startActivity(git);
            return true;
        }
        if (id == R.id.ayarlarim) {
            Intent git = new Intent(sepet.this, ayarlarim.class);
            finish();
            startActivity(git);
            return true;
        }
        if (id == R.id.cikis) {
            AlertDialog.Builder builder = new AlertDialog.Builder(sepet.this);
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

                    Intent git = new Intent(sepet.this, girisEkrani.class);
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

    public void sepetVerileriniGetir() {
        sepetID.clear();
        sepetAdapter.tikla = -1;

        DB db = new DB(this);
        musteriID = sha.getString("ID", "");
        try {
            Cursor cr = db.yaz().rawQuery("select * from sepet where musteri_id='" + musteriID + "'", null);
            double toplamucret = 0;
            while (cr.moveToNext()) {
                urun ur = new urun("Ürün Adı :" + cr.getString(2), cr.getString(4), "Fiyat :" + cr.getString(3) + " TL");
                String tempSepetID = cr.getString(0);
                urunID.add(cr.getString(5));
                sepetID.add(tempSepetID);
                tempSepetID = "";
                ls.add(ur);
                toplamucret += Double.valueOf(cr.getString(3));
            }
            siparistoplami.setText("Toplam Tutar : " + toplamucret + " TL");
            if (toplamucret == 0) {
                uyari.setText("Sepette Ürün Bulunmamaktadır");

            }
            toplamucret = 0;
            sepetAdapter adp = new sepetAdapter(sepet.this, ls);
            list.setAdapter(adp);
        } catch (Exception ex) {
            Log.d("Sepete Veri Hatası : ", ex.toString());
        }
    }

    class siparisTamamlama extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pro;
        String data = "";
        String url = "";

        public siparisTamamlama(String url, Activity ac) {
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
                Log.d("data",data);
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

            try {
                JSONObject obj = new JSONObject(data);
                JSONArray order=obj.getJSONArray("order");
                JSONObject oorder=order.getJSONObject(0);
                Boolean durum=Boolean.valueOf(oorder.getString("durum"));
                String mesaj=oorder.getString("mesaj");
                if (durum){
                    Toast.makeText(sepet.this, mesaj, Toast.LENGTH_SHORT).show();
                    DB db = new DB(sepet.this);
                    try {
                        int sonuc = db.sil("sepet", "musteri_id",musteriID);
                        if (sonuc > 0) {
                            sepet.this.finish();
                            Intent git = new Intent(sepet.this, sepet.class);
                            sepet.this.startActivity(git);
                        } else {
                            Toast.makeText(sepet.this, "Silme İşlemi Sırasında Hata Oluştu ...", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception ex) {
                        Log.d("SepetBoşaltma Hatası : ", ex.toString());
                    }
                }else {
                    Toast.makeText(sepet.this, mesaj, Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
