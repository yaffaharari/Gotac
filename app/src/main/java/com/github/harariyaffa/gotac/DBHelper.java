package com.github.harariyaffa.gotac;


        import android.content.Context;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteException;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory
            factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(DBConstants.LOG_TAG, "Creating all the tables");
        String CREATE_MOVIES_TABLE = " CREATE TABLE " + DBConstants.Movie.MOVIE_TABLE_NAME
                + "("
                + DBConstants.Movie.MOVIE_ID + " INTEGER PRIMARY KEY autoincrement, "
                + DBConstants.Movie.MOVIE_TITLE + " TEXT, "
                + DBConstants.Movie.MOVIE_GENRE + " TEXT, "
                + DBConstants.Movie.MOVIE_DESCRIPTION + " TEXT, "
                + DBConstants.Movie.MOVIE_IMG_URL + " TEXT )";

        try {
            db.execSQL(CREATE_MOVIES_TABLE);
        } catch (SQLiteException ex) {
            Log.e(DBConstants.LOG_TAG, "Create table exception: " +
                    ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.w(DBConstants.LOG_TAG, "Upgrading database from version " + oldVersion +
                " to " + newVersion + ", which will destroy all old date");
        db.execSQL("DROP TABLE IF EXISTS " + DBConstants.Movie.MOVIE_TABLE_NAME);
        onCreate(db);
    }
}
