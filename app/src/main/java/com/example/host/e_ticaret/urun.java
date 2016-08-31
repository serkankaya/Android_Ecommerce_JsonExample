package com.example.host.e_ticaret;

/**
 * Created by Java3 on 8/16/2016.
 */
public class urun {

    public String getDuzenlenmisadres() {
        return duzenlenmisadres;
    }

    public void setDuzenlenmisadres(String duzenlenmisadres) {
        this.duzenlenmisadres = duzenlenmisadres;
    }

    String duzenlenmisadres;
    public String getVidID() {
        return vidID;
    }

    public void setVidID(String vidID) {
        this.vidID = vidID;
    }

    private String baslik,resimUrl,vidID;

    public String getBaslik() {
        return baslik;
    }

    public void setBaslik(String baslik) {
        this.baslik = baslik;
    }

    public String getResimUrl() {
        return resimUrl;
    }

    public void setResimUrl(String resimUrl) {
        this.resimUrl = resimUrl;
    }

    public urun(String baslik, String resimUrl,String vidID) {
        this.baslik = baslik;
        this.resimUrl = resimUrl;
        this.vidID=vidID;
    }
    public urun(String duzenlenmisadres){
        this.duzenlenmisadres=duzenlenmisadres;
    }
}
