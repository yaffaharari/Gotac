package com.github.harariyaffa.gotac;

        import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MoviesHandler {

    private DBHelper dbhelper;

    public MoviesHandler(Context context) {
        dbhelper = new DBHelper(context, DBConstants.DATABASE_NAME, null,
                DBConstants.DATABASE_VERSION);
    }

    SQLiteDatabase db;
    //=============
    public void addMovie(Movie movie)
    {
        db = dbhelper.getWritableDatabase();

        ContentValues newMovieValues = new ContentValues();

        // newMovieValues.put(DBConstants.MOVIE_ID, movie.get_id());
        newMovieValues.put(DBConstants.Movie.MOVIE_TITLE, movie.getTitle());
        newMovieValues.put(DBConstants.Movie.MOVIE_GENRE, movie.getGenre());
        newMovieValues.put(DBConstants.Movie.MOVIE_DESCRIPTION, movie.getDescription());
        newMovieValues.put(DBConstants.Movie.MOVIE_IMG_URL,movie.getImgUrl());

        //Cursor todoCursor = db.rawQuery("SELECT  * FROM todo_items", null);
        //TodoCursorAdapter todoAdapter = new TodoCursorAdapter(, todoCursor);

        Long addMovie = db.insert(DBConstants.Movie.MOVIE_TABLE_NAME, null, newMovieValues);
        if(addMovie == 1){
            Log.e("error","ERROR in inserting movie");
        }
        db.close();
    }
    //=========
    public void deletMovie(long id){

        db = dbhelper.getWritableDatabase();

       /* String []deletItem=new String[2];
        deletItem[0]=movie.getSubjectMovie();
        deletItem[1]=movie.getDescription();*/
        //int coutRowDelet=db.delete(DBConstants.MOVIES_TABLE_NAME, null, deletItem);
        int coutRowDelet = db.delete(DBConstants.Movie.MOVIE_TABLE_NAME, DBConstants.Movie.MOVIE_ID + "=" +id,null);
        if(coutRowDelet==1){
            Log.e("key",("delet"+coutRowDelet)+"row");//There will always be one in our case
        }
        db.close();


    }
    //==========
    public void updateMovie(Movie newMovie,long id){

        db = dbhelper.getWritableDatabase();

        String title = newMovie.getTitle();
        String ganre = newMovie.getGenre();
        String dec = newMovie.getDescription();
        String imgUrl = newMovie.getImgUrl();

        ContentValues cvUpdate = new ContentValues();

        //cvUpdate.put(DBConstants.MOVIE_ID,newId);//Error is always equal to 0
        cvUpdate.put(DBConstants.Movie.MOVIE_TITLE,title);
        cvUpdate.put(DBConstants.Movie.MOVIE_GENRE,ganre);
        cvUpdate.put(DBConstants.Movie.MOVIE_DESCRIPTION,dec);
        cvUpdate.put(DBConstants.Movie.MOVIE_IMG_URL,imgUrl);
        int i=db.update(DBConstants.Movie.MOVIE_TABLE_NAME,cvUpdate, DBConstants.Movie.MOVIE_ID+ " = " + id, null);
        Log.e("TAG ","the result of update is:"+ i);
        db.close();
    }

    //===========
    public void deleteAllMovies(){
        db = dbhelper.getWritableDatabase();
        db.delete(DBConstants.Movie.MOVIE_TABLE_NAME,null,null);
        db.close();
    }
    //==============

    public Cursor getAllMovie(){
        db = dbhelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT  * FROM " + DBConstants.Movie.MOVIE_TABLE_NAME , null);
/*
        if (cursor != null) {
            cursor.moveToFirst();
        }*/
        return cursor;
    }

    public boolean Exists(String searchItem) {

        db = dbhelper.getReadableDatabase();
        Cursor checkIfExist = db.rawQuery("SELECT *    FROM "+ DBConstants.Movie.MOVIE_TABLE_NAME + " WHERE " + "title  = " + "'" + searchItem + "'", null);
        int count = checkIfExist.getCount();
        if(count <= 0){
            checkIfExist.close();
            return false;
        }
        checkIfExist.close();
        return true;
    }
}
