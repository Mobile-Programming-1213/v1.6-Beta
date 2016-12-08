package ssjk.cafein;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by wqe13 on 2016-11-30.
 */

class DatabaseOpenHelper {
    private static String DB_PATH = "/data/data/ssjk.cafein/databases/";
    private static String DB_NAME = "cafesd1.db";
    private SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);

    DatabaseOpenHelper(Context c){

    }

    Cursor searchDatabase (String field){
        return db.rawQuery(field, null);
    }

    void insertSuggestion (ContentValues values){
        db.insert("TABLE_SUGGESTION", null, values);
    }

}
