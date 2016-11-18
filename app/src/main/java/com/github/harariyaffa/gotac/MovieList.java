package com.github.harariyaffa.gotac;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class MovieList extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener,
        AdapterView.OnItemClickListener/*MenuItem.OnMenuItemClickListener*/{

    public static final String MOVIE_SELECTED ="movie";
    public static final String CLICK_ITEM_MODE ="itemClick";
    public static final String MOVIE_ID="idOfMovie";

    Context context;
    SharedPreferences preferences;

    String strUserName;

    TextView userNameTV;
    ListView movieList;
    Intent intent;
    CustomCursorAdapter movieAdapter;
    MoviesHandler handler;
    Toolbar toolbar;
    View emptyView;

    @TargetApi(Build.VERSION_CODES.M)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        init();

        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.drawable.icon_logo);
        toolbar.setLogoDescription(getString(R.string.app_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("  virtual library");
        movieList.setEmptyView(emptyView);
        strUserName = preferences.getString(MainActivity.USER_NAME, MainActivity.USER_NAME);
        userNameTV.setText(strUserName);
        registerForContextMenu(movieList);
        handler = new MoviesHandler(this);
    }
    public void init(){
        userNameTV = (TextView)findViewById(R.id.userName_tv);
        context = getApplicationContext();
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        movieList = (ListView)findViewById(R.id.moviesListView);
        emptyView = findViewById(R.id.empty_listview);
    }

    //=======fab click=========
    public void addMovie(View v){
        PopupMenu popupMenu = new PopupMenu(this, v);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    //when short click on item movie performed we go to edit mode by pass the
    //current movie selected
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Movie theMovie = getMovieInIndex(position);
        editMovie(theMovie, id);
    }

    public void editMovie(Movie theMovie, long idOfMovie){
        intent = new Intent(this,EditMovie.class);
        intent.putExtra(MOVIE_SELECTED,theMovie);
        intent.putExtra(CLICK_ITEM_MODE, true);
        intent.putExtra(MOVIE_ID, idOfMovie);
        startActivity(intent);
        finish();
    }
    public Movie getMovieInIndex(int index){
        Cursor c = movieAdapter.getCursor();
        c.moveToPosition(index);
        String strTitle = c.getString(c.getColumnIndex(DBConstants.Movie.MOVIE_TITLE));
        String strDes = c.getString(c.getColumnIndex(DBConstants.Movie.MOVIE_DESCRIPTION));
        String strGnr = c.getString(c.getColumnIndex(DBConstants.Movie.MOVIE_GENRE));
        String strImgUrl = c.getString(c.getColumnIndex(DBConstants.Movie.MOVIE_IMG_URL));

        return new Movie(strTitle, strDes, strGnr, strImgUrl);
    }

    @Override
    protected void onResume (){
        super.onResume();
        Cursor cursor =handler.getAllMovie();
        if (movieAdapter == null) {
            movieAdapter = new CustomCursorAdapter(this,cursor);
        }
        movieAdapter.changeCursor(cursor);
        movieList.setAdapter(movieAdapter);
        movieList.setOnItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_movie_manual:
                intent=new Intent(this, EditMovie.class);
                intent.putExtra(CLICK_ITEM_MODE, false);
                startActivity(intent);
                return true;
            case R.id.add_movie_online:
                intent=new Intent(this,SearchMovieOnline.class);
                startActivity(intent);
                return true;
        }
        return false;
    }

    //========OptionsMenu===========
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return(super.onCreateOptionsMenu(menu));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.deleteAll:
                handler.deleteAllMovies();
                onResume();
                return true;
            case R.id.exit:
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
          /*  case R.id.logOut:
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                return true;*/
        }
        return false;
    }
    //===========ContextMenu============
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_manu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
        return;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        super.onContextItemSelected(item);
        AdapterView.AdapterContextMenuInfo menuInfo;
        menuInfo =(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int index = menuInfo.position;
        Cursor c = movieAdapter.getCursor();
        c.moveToPosition(index);
        int id = c.getInt(c.getColumnIndex(DBConstants.Movie.MOVIE_ID));
        Movie theMovie=getMovieInIndex(index);
        switch (item.getItemId()) {
            case R.id.deleteItem:
                handler.deletMovie(id);
                onResume();
                return(true);
            case R.id.editItem:
                editMovie(theMovie,id);
                return(true);
        }
       return(false);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //==========cursor Adapter==========

    public class CustomCursorAdapter extends CursorAdapter {
        public CustomCursorAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View rootView = inflater.inflate(R.layout.layout_of_item, parent, false);
            return rootView;
        }
        @Override
        public void bindView(View rootView, Context context, Cursor cursor) {

            TextView editTitle = (TextView) rootView.findViewById(R.id.title_id);
            ImageView image = (ImageView) rootView.findViewById(R.id.thumbnail);
            String title = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.Movie.MOVIE_TITLE));
            String imgUrl = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.Movie.MOVIE_IMG_URL));
            editTitle.setText(title);
            Glide.with(getApplicationContext())
                    .load(imgUrl)
                    .placeholder(R.drawable.defualt_img_cover)
                    .thumbnail(0.1f)
                    .centerCrop()
                    .into(image);
        }
    }
}

