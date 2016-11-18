package com.github.harariyaffa.gotac;


public class DBConstants {

    public static final String LOG_TAG = "SQLite_Test";

    public static final String DATABASE_NAME = "myMovies.db";
    public static final int DATABASE_VERSION = 1;

    //============table for movies instances ===============
    class Movie {
        public static final String MOVIE_TABLE_NAME = "movie";

        public static final String MOVIE_ID = "_id";
        public static final String MOVIE_TITLE = "title";
        public static final String MOVIE_DESCRIPTION = "description";
        public static final String MOVIE_GENRE = "genre";
        public static final String MOVIE_IMG_URL = "url";
    }

}

