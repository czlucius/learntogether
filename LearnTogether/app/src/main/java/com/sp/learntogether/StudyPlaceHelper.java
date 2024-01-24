package com.sp.learntogether;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StudyPlaceHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME ="studyplaces.db";
    private static final int SCHEMA_VERSION = 1;
    public StudyPlaceHelper(Context context){
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE studyplaces_table (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, description TEXT, imgURI TEXT, lat REAL, lon REAL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("drop Table if exists studyplaces_table");
    }

    public Cursor getAll(){
        SQLiteDatabase database = this.getWritableDatabase();
        return (database.rawQuery("SELECT * FROM studyplaces_table", null));
    }

    public void insert(String name, String description, String imgURI, double lat, double lon){
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("description", description);
        cv.put("imgURI", imgURI);
        cv.put("lat", lat);
        cv.put("lon", lon);

        getWritableDatabase().insert("studyplaces_table", null, cv);
    }


}
