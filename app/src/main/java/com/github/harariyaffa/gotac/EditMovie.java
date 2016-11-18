package com.github.harariyaffa.gotac;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class EditMovie extends AppCompatActivity implements TextView.OnEditorActionListener {

    String strMovieTitle, strMovieDes, strMovieGnr, strMovieImgUrl;
    Boolean isItemClick;

    Intent intent;
    EditText titleET, descriptionET, gnrET, imgUrlET;
    Movie movie;
    MoviesHandler handler;
    Boolean isFromOnline;
    ScrollView mScrollView;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editmovie);
        init();

        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.drawable.icon_logo);
        toolbar.setLogoDescription(getString(R.string.app_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("  edit movie");
        intent = getIntent();
        Bundle extras = intent.getExtras();

        isItemClick = extras.getBoolean(MovieList.CLICK_ITEM_MODE);
        isFromOnline = getIntent().getBooleanExtra(SearchMovieOnline.ONLINE_MODE, false);

        if (isItemClick) {
            movie = (Movie) extras.get(MovieList.MOVIE_SELECTED);
            displayMovie(movie);
        }
        if (isFromOnline) {
            isItemClick = false;
            movie = (Movie) extras.get(MovieList.MOVIE_SELECTED);
            displayMovie(movie);
        }
    }

    public void init() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        titleET = (EditText) findViewById(R.id.title_et);
        gnrET = (EditText) findViewById(R.id.ganre_et);
        descriptionET = (EditText) findViewById(R.id.description_et);
        imgUrlET = (EditText) findViewById(R.id.img_url_et);
        handler = new MoviesHandler(this);
        mScrollView = (ScrollView)findViewById(R.id.scrollView);
    }

    public void displayMovie(Movie movie) {
        strMovieTitle = movie.getTitle();
        strMovieGnr = movie.getGenre();
        strMovieDes = movie.getDescription();
        strMovieImgUrl= movie.getImgUrl();

        titleET.setText(strMovieTitle);
        gnrET.setText(strMovieGnr);
        descriptionET.setText(strMovieDes);
        imgUrlET.setText(strMovieImgUrl);

        titleET.setOnEditorActionListener(this);
        gnrET.setOnEditorActionListener(this);
        descriptionET.setOnEditorActionListener(this);
        imgUrlET.setOnEditorActionListener(this);
    }

    public void saveBtnClick(View v) {
        saveClick();
    }

    private void saveClick() {
        Movie movie = saveDetailsOfMovie();
        if (movie != null) {
            if (!isItemClick) {//we came from online search or fab click (add movie manual)
                if(!checkMovieExists(movie))
                    handler.addMovie(movie);
                else
                    Toast.makeText(this, "this movie title already exists", Toast.LENGTH_SHORT).show();
            } else {//we came from item click
                intent = getIntent();
                Bundle bundle = intent.getExtras();
                long idOfCurrentMovie = bundle.getLong(MovieList.MOVIE_ID);
                handler.updateMovie(movie, idOfCurrentMovie);
            }
            Intent i = new Intent(this, MovieList.class);
            startActivity(i);
            finish();
        }
        else
            Toast.makeText(getApplicationContext(),"please enter at least title",Toast.LENGTH_LONG).show();
    }

    private boolean checkMovieExists(Movie movie) {
            boolean isMovieExists = handler.Exists(movie.getTitle());//if false the movie don't exists in db
            if (isMovieExists) {
                return true;
            }
        return false;
    }

    public void cancelClick(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        finish();
    }

    public Movie saveDetailsOfMovie() {
        strMovieTitle = titleET.getText().toString();
        if(strMovieTitle.equals("")){
            return null;
        }
        strMovieGnr = gnrET.getText().toString();
        strMovieDes = descriptionET.getText().toString();
        strMovieImgUrl = imgUrlET.getText().toString();
        if(strMovieImgUrl.equals("")){
            strMovieImgUrl="http://myfirstchat.com/bookcity2/covers2/7646.PNG";
        }
        return new Movie(strMovieTitle, strMovieDes, strMovieGnr, strMovieImgUrl);
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
           /* case R.id.logOut:
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                return true;*/
        }
        return false;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        switch (actionId){
            case EditorInfo.IME_ACTION_NEXT:{
                switch (v.getId()){
                    case R.id.title_et:
                        gnrET.requestFocus();
                        break;
                    case R.id.ganre_et:
                        descriptionET.requestFocus();
                        break;
                    case R.id.description_et:
                        imgUrlET.requestFocus();
                        break;
                }
             return true;}
            case EditorInfo.IME_ACTION_DONE: {
                saveClick();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent(getApplicationContext(), MovieList.class);
        startActivity(backIntent);
        finish();
    }
}

    //boolean firstInvocation = true;

    /*public final String TITLE_EDIT_TEXT_STATE = "titleEditTextState";
    public final String GANRE_EDIT_TEXT_STATE = "ganreEditTextState";
    public final String DESCRIPTION_EDIT_TEXT_STATE = "descriptionEditTextState";
    public final String IMG_URL_EDIT_TEXT_STATE = "imgUrlEditTextState";*/





