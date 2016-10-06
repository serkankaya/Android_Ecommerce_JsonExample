package com.example.host.e_ticaret;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;

import java.util.HashMap;

public class urunDetay extends AppCompatActivity {
    SliderLayout mDemoSlider;
    TextView urunBaslik,urunFiyat,urunKisaAciklama;
    WebView detay;
    SharedPreferences sha;
    SharedPreferences.Editor edit;
    Button buton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urun_detay);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sha=getSharedPreferences("KullaniciCerezler",MODE_PRIVATE);
        edit=sha.edit();
        mDemoSlider = (SliderLayout) findViewById(R.id.slider);
        urunBaslik=(TextView)findViewById(R.id.urunBaslikDetaytxt);
        urunFiyat=(TextView)findViewById(R.id.fiyatDetattxt);
        urunKisaAciklama=(TextView)findViewById(R.id.kisaAciklamaDetaytxt);
        detay=(WebView)findViewById(R.id.detayWebView);
        detay.getSettings().setJavaScriptEnabled(true);
        String ebaslik=getIntent().getExtras().getString("urunBaslik","");
        String efiyat="Fiyat : "+getIntent().getExtras().getString("Fiyat","");
        String ekaciklama="Kısa Açıklama : "+getIntent().getExtras().getString("kisaAciklama","");
        String edetay="<html><head>"
                + "<style type=\"text/css\">body{color: black;}"
                + "</style></head>"
                + "<body>"+"Detay : "+getIntent().getExtras().getString("detay","")+ "</body></html>";


        urunBaslik.setText(ebaslik);
        urunFiyat.setText(efiyat+" TL");
        urunKisaAciklama.setText(ekaciklama);
        detay.loadDataWithBaseURL(null, edetay, "text/html", "UTF-8", null);
        HashMap<String, String> url_maps = new HashMap<>();

        /*
        for (String name : urunListesi.urunResimUrlExtra.keySet()) {
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(urunListesi.urunResimUrlExtra.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);
            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);
            mDemoSlider.addSlider(textSliderView);
        }
        */

        for (String item : urunListesi.resimler.get(urunListesi.tiklanan)) {
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .description("Resim")
                    .image(item)
                    .setScaleType(BaseSliderView.ScaleType.Fit);
            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle().putString("extra", item);
            mDemoSlider.addSlider(textSliderView);
        }

        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(6000);
        urunListesi.urunResimUrlExtra.clear();
    }
    @Override
    protected void onStop() {
        mDemoSlider.stopAutoCycle();
        super.onStop();
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
        switch (item.getItemId()){
            case android.R.id.home:
                if (getParentActivityIntent()==null){
                    finish();
                }else{
                    NavUtils.navigateUpFromSameTask(this);
                }
        }
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.sepetim) {
            Intent git3=new Intent(urunDetay.this,sepet.class);
            startActivity(git3);
            return true;
        }
        if (id == R.id.adreslerim) {
            Intent git = new Intent(urunDetay.this, adreslerim.class);
            startActivity(git);
            return true;
        }
        if (id == R.id.ayarlarim) {
            Intent git = new Intent(urunDetay.this, ayarlarim.class);
            startActivity(git);
            return true;
        }
        if (id == R.id.cikis) {
            AlertDialog.Builder builder = new AlertDialog.Builder(urunDetay.this);
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

                    Intent git = new Intent(urunDetay.this, girisEkrani.class);
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
    public void sepeteEkle(View v){
        sha=getSharedPreferences("KullaniciCerezler",MODE_PRIVATE);
        String urunAnaResimUrl="";
        for (String item : urunListesi.resimler.get(urunListesi.tiklanan)) {
            int i=0;
            i++;
            if(i==1){
                urunAnaResimUrl=item;
            }
        }
        String musteriID=sha.getString("ID","");
        String ebaslik=getIntent().getExtras().getString("urunBaslik","");
        String urunID=getIntent().getExtras().getString("urunID","");
        String efiyat=getIntent().getExtras().getString("Fiyat","");
        String edetay="Detay : "+getIntent().getExtras().getString("detay","");
        DB db = new DB(this);
            try {
                db.yaz().execSQL("insert into sepet values(null,'" + musteriID + "','" + ebaslik + "','" + efiyat + "','" + urunAnaResimUrl + "','"+urunID+"')");
                Toast.makeText(urunDetay.this, "Sepete Ürün Eklendi...", Toast.LENGTH_SHORT).show();
                finish();

            } catch (Exception ex) {
                Log.d("Ekleme Hatası : ", ex.toString());
                Toast.makeText(urunDetay.this, "Ekleme Başarısız", Toast.LENGTH_SHORT).show();
            }

    }


}
