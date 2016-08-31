package com.example.host.e_ticaret;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB extends SQLiteOpenHelper {
    private static String dbName = "eticaretdb";
    private static Integer versiyon = 1;

    public SQLiteDatabase oku() {
        return this.getReadableDatabase();
    }

    public SQLiteDatabase yaz() {
        return this.getWritableDatabase();
    }

    public DB(Context context) {
        super(context, dbName + ".db", null, versiyon);
    }

    public DB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, dbName + ".db", null, versiyon);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE `sepet` (\n" +
                "\t`sepet_id`\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\t`musteri_id`\tTEXT,\n" +
                "\t`urun_baslik`\tTEXT,\n" +
                "\t`urun_fiyat`\tTEXT,\n" +
                "\t`resim_url`\tTEXT,\n" +
                "\t`urun_id`\tTEXT\n" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists sepet");
        onCreate(db);
    }

    // data getirme
    public Cursor dataGetir(String tableName,String query){
        return oku().rawQuery("select * from "+tableName+" "+query,null);
    }
    // data getirme
    public int ikilisil(String tableName,String columnName,String val1,String val2){
        return yaz().delete(tableName,columnName,new String[]{val1,val2});
    }

    // data silme
    public int sil(String tableName,String columnName,String val){
        return yaz().delete(tableName,columnName+"=?",new String[]{val});
    }


}
