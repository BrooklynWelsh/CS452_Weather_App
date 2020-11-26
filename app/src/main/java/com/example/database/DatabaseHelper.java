package com.example.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLInput;
import java.util.Arrays;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper
{
    private static DatabaseHelper instance = null;
    private static String TAG = "DataBaseHelper"; // Tag just for the LogCat window
    //destination path (location) of our database on device
    private static String DB_PATH = "";
    private static String DB_NAME ="CityDB.sqlite3";// Database name
    private static int DB_VERSION = 1;
    private SQLiteDatabase mDataBase;
    private Context context;

    public DatabaseHelper( Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    public static DatabaseHelper getHelper(Context context){
        if(instance == null)    {
            instance = new DatabaseHelper(context.getApplicationContext());
            try {
                instance.createDatabase();
            } catch (IOException e) {
                Toast.makeText(context, "We encountered a problem when querying the database.", Toast.LENGTH_LONG);
                return null;
            }
        }
        return instance;
     }

    public SQLiteDatabase getDatabase() {
        return mDataBase;
    }

    public void createDatabase() throws IOException {
        //If database not exists copy it from the assets
        File dbFile = context.getDatabasePath(DB_NAME);
        if (!dbFile.exists()) {
            try {
                SQLiteDatabase checkDB = context.openOrCreateDatabase(DB_NAME, context.MODE_PRIVATE, null);
                if (checkDB != null) {
                    checkDB.close();
                }
                copyDatabase(dbFile);
            } catch (IOException e) {
                throw new RuntimeException("Error creating source database", e);
            }
            }
        mDataBase = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
        }
    //Check that the database exists here: /data/data/your package/databases/Da Name 
    private boolean checkDataBase()
    {
        File dbFile = new File(DB_PATH + DB_NAME);
        //Log.v("dbFile", dbFile + "   "+ dbFile.exists()); 
        return dbFile.exists();
    }

    //Copy the database from assets 
    private void copyDatabase(File dbFile) throws IOException
    {
        Log.d("ASSETS", Arrays.toString(context.getAssets().list("")));
        InputStream mInput = context.getAssets().open(DB_NAME);
        File outputFile = context.getDatabasePath(DB_NAME);
        OutputStream mOutput = new FileOutputStream(outputFile);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer))>0)
        {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    @Override
    public synchronized void close()
    {
        if(mDataBase != null)
            mDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

} 