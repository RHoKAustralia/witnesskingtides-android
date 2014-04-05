package com.drkstr.kingtide;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Simple database access helper class. We just use this to cache data when the device is offline
 */
public class OfflineDBCache {
 
    public static final String KEY_ROWID = "_id";
    public static final String KEY_JSON_STRING = "json_string";
    
    

    
    private DatabaseHelper mDbHelper;
    private static  SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
        "create table offlineTable (_id integer primary key autoincrement, "
        + "json_string integer not null);";
       

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "offlineTable";
    private static final int DATABASE_VERSION = 1;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
          
            db.execSQL("DROP TABLE IF EXISTS offlineTable");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public OfflineDBCache(Context ctx) {
        this.mCtx = ctx;
    }
    
    /**
     * Open the offlineTable database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public OfflineDBCache open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }
    
    /**
     * THis saves the JSON sring if the user is ofline
     * @param the JSON stirng we are saving 
     * @return the row ID of the row in the databse; we are not using this at all in this app
     */
    
    
    public long savePic(String jsonString) {
    	
    	
        ContentValues offlinePic = new ContentValues();
        
        offlinePic.put(KEY_JSON_STRING, jsonString);
        
        return mDb.insert(DATABASE_TABLE, null, offlinePic);
        
    }
    
    /**
     * Return a Cursor over the list of all offlineTable in the database
     * 
     * @return Cursor over all saved JSON strings
     */
    public Cursor fetchAllStrings() {

    	 Cursor mCursor = mDb.query(DATABASE_TABLE,new String[] {KEY_ROWID,KEY_JSON_STRING} , null, null, null, null,null);
    	 
    	 return mCursor;
    }
    
    /**
     * Delete the entry with the given rowId
     * 
     * @param rowId id of entry to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteEntry(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
        
    }
    
    

   
	
}
