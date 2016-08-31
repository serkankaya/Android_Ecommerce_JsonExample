package com.example.host.e_ticaret;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.util.List;

/**
 * Created by Java3 on 8/16/2016.
 */
public class urunAdapter extends BaseAdapter{
    private LayoutInflater inf;
    private List<urun> urls;
    private Activity ac;
    public urunAdapter(Activity ac,List<urun> urls){
        inf=(LayoutInflater)ac.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.urls=urls;
        this.ac=ac;


    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public Object getItem(int i) {
        return urls.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public static int tikla = -1;
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View view1;
        tikla = i;
        view1=inf.inflate(R.layout.adresrow,null);
        TextView vtxt=(TextView)view1.findViewById(R.id.adresbilgisitxt);
        ImageView vresim=(ImageView)view1.findViewById(R.id.imageView3);
        final Button adresSilButonu=(Button)view1.findViewById(R.id.adresiSilbtn);
        adresSilButonu.setTag(tikla);

        adresSilButonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ac);
                builder.setTitle("Adres Silme İşlemi");
                builder.setMessage("Seçilen Adresi Silmek İstediğinizden Emin misiniz ?");
                builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Yes Durumunda

                        tikla=Integer.valueOf(adresSilButonu.getTag().toString());
                        String url="http://jsonbulut.com/json/addressDelete.php?ref=7b7392076900968d8e4ad78351ad55d3&musterilerID="+adreslerim.musteriID+"&adresID="+adreslerim.adresID.get(tikla);
                        new adressil(url,ac).execute();
                        adreslerim.adresID.clear();
                        ac.finish();
                        Intent git=new Intent(ac.getApplication(),adreslerim.class);
                        ac.startActivity(git);
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
            }
        });
        urun ul=urls.get(i);
        vtxt.setText(ul.getDuzenlenmisadres());
        Picasso.with(ac).load(ul.getResimUrl()).into(vresim);
        return view1;
    }
    class adressil extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pro;
        String data = "";
        String url = "";

        public adressil(String url, Activity ac) {
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
                JSONArray arr = obj.getJSONArray("announcements");
                String sonuc=arr.toString();
                Log.d("Sonuc : " , sonuc);

                    Toast.makeText(ac, "Silme İşlemi Başarılı", Toast.LENGTH_SHORT).show();


            } catch (JSONException e) {
                Log.d("Kayıt Hata var:",e.toString());
            }finally {
                pro.dismiss();
            }


            Log.d("Gelen Data : ", data);
        }
    }
}
