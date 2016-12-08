package ssjk.cafein;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SplashActivity extends Activity {
    private DatabaseOpenHelper database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previouslyStarted = prefs.getBoolean(getString(R.string.pref_previously_started), false);
        database = new DatabaseOpenHelper(this);
        //check if databse is already analyzed
//      /*
        if(!previouslyStarted) {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(getString(R.string.pref_previously_started), Boolean.TRUE);
            //this is only used when the applicaion is being launched for the first time
            initialize(getApplicationContext());
            updatesuggestion(getApplicationContext());
            edit.apply();
        }
//       */
        /*
        initialize(getApplicationContext());
        updatesuggestion(getApplicationContext());
       */
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //메인으로 넘어가기
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }



    public static void initialize(Context ctx) {
        File folder = new File("/data/data/ssjk.cafein/" + "databases");
        folder.mkdirs();
        File outfile = new File("/data/data/ssjk.cafein/" + "databases/" + "cafesd1.db");
        //If cafesd1.db exist in filepath, delete and get new make a new file. Copy database in asset folder to filepath
        if (outfile.exists()){
            outfile.delete();
            outfile = new File("/data/data/ssjk.cafein/" + "databases/" + "cafesd1.db");
        }
        if (outfile.length() <= 0) {
            AssetManager assetManager = ctx.getResources().getAssets();
            try {
                InputStream is = assetManager.open("cafe.sqlite", AssetManager.ACCESS_BUFFER);
                long filesize = is.available();
                byte [] tempdata = new byte[(int)filesize];
                is.read(tempdata);
                is.close();
                outfile.createNewFile();
                FileOutputStream fo = new FileOutputStream(outfile);
                fo.write(tempdata);
                fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void updatesuggestion(Context context) {
        //opening database cafedb from the sdcard
        //SQLiteDatabase cafedb = openOrCreateDatabase("cafesd1.db", Context.MODE_PRIVATE, null);
        Toast.makeText(context,"Analyzing Data... Only on the first launch",Toast.LENGTH_SHORT).show();
        String select = "SELECT * FROM Cafein";
        Cursor cur = database.searchDatabase(select);
        cur.moveToFirst();
        while (!cur.getString(1).equals("EOF")) {
            // ur.getString(1) gets cafe name
            String text = cur.getString(1);
            String select1 = String.format("SELECT * FROM TABLE_SUGGESTION WHERE FIELD_SUGGESTION= '%s'", text);
            Cursor cur1 = database.searchDatabase(select1);
            if (cur1.getCount() == 0) {
                //insert name of cafe to suggestion when there is no overlapping field
                ContentValues values = new ContentValues();
                values.put("FIELD_SUGGESTION", text);
                values.put("FIELD_TYPE", "CAFE");
                values.put("NAME_ENG", cur.getString(10));
                values.put("Latitude", cur.getDouble(11));
                values.put("Longitude", cur.getDouble(12));
                database.insertSuggestion(values);
            }
            cur1.close();
            while (text.equals(cur.getString(1)))
                cur.moveToNext();
        }
        select = ("SELECT * FROM Cafein");
        cur = database.searchDatabase(select);
        cur.moveToFirst();
        while (!cur.getString(1).equals("EOF")) {
            String text2 = cur.getString(3);
            if (text2 != null) {
                String select1 = String.format("SELECT * FROM TABLE_SUGGESTION WHERE FIELD_SUGGESTION= '%s'", text2);
                Cursor cur1 = database.searchDatabase(select1);
                if (cur1.getCount() == 0) {
                    ContentValues values = new ContentValues();
                    values.put("FIELD_SUGGESTION", text2);
                    values.put("FIELD_TYPE", cur.getString(9));
                    String select2 = String.format("SELECT * FROM Cafein WHERE Drink_Name= '%s'", text2);
                    Cursor cur2 = database.searchDatabase(select2);
                    values.put("ITEM_NUM", cur2.getCount());
                    database.insertSuggestion(values);
                    cur2.close();
                }
                cur1.close();

            }
            cur.moveToNext();
        }
        cur.close();
    }
}
