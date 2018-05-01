package com.example.michellema.learnersd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by michellema on 2018-03-26.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DBNAME = "mydabs.db";
    private static final int VERSISON = 1;

    private static final String TABLE_NAME = "myVocabulary";
    public static final String ID = "_id";
    public static final String USER = "user";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String VOCABULARY = "Vocabulary";
    public static final String DIFFICULTY = "difficulty";
    public static final String EXPLANATION = "Explanation";


    private SQLiteDatabase myDB;

    public DBHelper(Context context){
        super(context, DBNAME, null, VERSISON);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryTable = "CREATE TABLE " + TABLE_NAME +
                " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USER + " TEXT NOT NULL, "
                + EMAIL + " TEXT NOT NULL, "
                + PASSWORD + " TEXT NOT NULL, "
                + VOCABULARY + " TEXT NOT NULL, "
                + DIFFICULTY + " REAL NOT NULL, "
                + EXPLANATION + " TEXT NOT NULL " + ")";

        db.execSQL(queryTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void openDB(){
        myDB = getWritableDatabase();
    }

    public void closeDB(){
        if(myDB != null && myDB.isOpen()) {
            myDB.close();
        }
    }

    public long insert(int id, String user, String email, String password, String vocabulary, float difficulty, String explanation){
        ContentValues values = new ContentValues();
        if(id != -1) {
            values.put(ID, id);
            values.put(USER, user);
            values.put(EMAIL, email);
            values.put(PASSWORD, password);
            values.put(VOCABULARY, vocabulary);
            values.put(DIFFICULTY, difficulty);
            values.put(EXPLANATION, explanation);
        }

        return myDB.insert(TABLE_NAME, null, values);
    }

    public long update(int id, float difficulty){
        ContentValues values = new ContentValues();
        //values.put(VOCABULARY, vocabulary);
        values.put(DIFFICULTY, difficulty);

        String where = ID + " = " + id;

        return myDB.update(TABLE_NAME, values, where, null);
    }

    public long delete(int id){
        String where = ID + " = " + id;

        return myDB.delete(TABLE_NAME, where, null);
    }

    public Cursor getAllRecords(String user){
        String query;
        if(user.equals("default_user")){
            query = "SELECT * FROM " + TABLE_NAME;
        }else{
            query = "SELECT * FROM " + TABLE_NAME + " WHERE USER = \"" + user + "\"";
        }
        return myDB.rawQuery(query, null);

    }

    public Cursor getDataBasedOnDifficulty(String user, float difficulty){
        String query;
        if(user.equals("default_user")){
            query = "SELECT * FROM " + TABLE_NAME + " WHERE DIFFICULTY = " + difficulty;
        }else{
            query = "SELECT * FROM " + TABLE_NAME + " WHERE USER = \"" + user + "\" AND DIFFICULTY = " + difficulty;
        }
        return myDB.rawQuery(query, null);
    }

    public Cursor getUsers(){
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE VOCABULARY = \"NO DATA\"";

        return myDB.rawQuery(query, null);
    }
}

