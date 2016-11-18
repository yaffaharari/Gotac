package com.github.harariyaffa.gotac;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SearchMovieOnline extends AppCompatActivity implements TextView.OnEditorActionListener {

    public static final String URL = "http://www.omdbapi.com/?";
    public static final String ONLINE_MODE = "backFromOnLine";
    //public static final String THE_PATH_KEY = "thePathKey";
    //public static final int REQUEST_CODE = 5;

    EditText serchEditText;
    String strSerch;
    Toolbar toolbar;
    MoviesHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2_activity_from_internet_search);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.drawable.icon_logo);
        toolbar.setLogoDescription(getString(R.string.app_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("  Search movie online");
        //setFontTitleToolBar();
        serchEditText.setOnEditorActionListener(this);
    }

    public void init() {
        serchEditText = (EditText) findViewById(R.id.searchEditText);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
    }

    public void goBtnClick(View v) {
       goSearch();
    }

    private void goSearch() {
        strSerch = serchEditText.getText().toString().replaceAll(" ", "%20");

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            SearchRequest searchRequest = new SearchRequest(this);
            searchRequest.execute(URL + "s=" + strSerch);
        } else {
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelClick(View v) {
        Intent intent = new Intent(this, MovieList.class);
        startActivity(intent);
        finish();
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
   /*         case R.id.logOut:
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                return true;*/
        }
        return false;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(EditorInfo.IME_ACTION_SEARCH == actionId){
            goSearch();
            InputMethodManager inputMethodManager = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent(getApplicationContext(), MovieList.class);
        startActivity(backIntent);
        finish();
    }

    //============Search AsyncTask class===========

    class SearchRequest extends AsyncTask<String, Integer, ArrayList<Movie>> {

        Activity activity;
        ProgressDialog mDialog;
        ProgressBar mProgressBar;
        String strError;

        public SearchRequest(Activity activity) {
            this.activity = activity;
            mDialog=new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mDialog.setCancelable(true);
            mDialog.setMessage("Loading...");
            mDialog.setProgress(0);
            mDialog.show();
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

            String theSearchWord = sendHttpRequest(params[0]);
            ArrayList<Movie> movies = responseProcess(theSearchWord);

            return movies;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> result) {
            if (result.size() <= 0) {
                mDialog.dismiss();
                Toast.makeText(getApplicationContext(), strError, Toast.LENGTH_LONG).show();
            } else {
                final MyArrayListAdapter myArrayListAdapter;
                ListView movieList = (ListView) findViewById(R.id.listView);
                myArrayListAdapter = new MyArrayListAdapter(SearchMovieOnline.this,
                        R.layout.layout_of_item, result);
                mDialog.dismiss();
                movieList.setAdapter(myArrayListAdapter);
                movieList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Movie theMovie = myArrayListAdapter.getItem(position);
                        Intent intent = new Intent(SearchMovieOnline.this, EditMovie.class);
                        Serializable serializable = theMovie;
                        intent.putExtra(MovieList.MOVIE_SELECTED, serializable);
                        intent.putExtra(ONLINE_MODE, true);
                        startActivity(intent);
                        //intent.putExtra(THE_PATH_KEY, REQUEST_CODE);
                    }
                });
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //mProgressBar.setProgress(values[0]);
            mDialog.setProgress(values[0]);
        }

        public String sendHttpRequest(String srcText) {
            BufferedReader input = null;
            HttpURLConnection httpCon = null;
            StringBuilder response = new StringBuilder();
            try {
                java.net.URL url = new URL(srcText);
                httpCon = (HttpURLConnection) url.openConnection();
                if (httpCon.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e("TEST", "Cannot connect to: " + srcText);
                    return null;
                }
                input = new BufferedReader(
                        new InputStreamReader(httpCon.getInputStream()));
                String line;
                while ((line = input.readLine()) != null) {
                    response.append(line + "\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (httpCon != null) {
                    httpCon.disconnect();
                }
            }
            return response.toString();
        }

        public ArrayList<Movie> responseProcess(String strSearch) {
            int totalSize = 0;
            try {
                ArrayList<Movie> movieArrayList = new ArrayList<Movie>();
                JSONObject fullResponse = new JSONObject(strSearch);
                String response = fullResponse.getString("Response");
                if(response.equals("False")){
                    strError = fullResponse.getString("Error");
                    Log.d("error of", strError);
                    return movieArrayList;
                }
                //int  totalResults = fullResponse.getInt("totalResults");
                JSONArray jsonArray = fullResponse.getJSONArray("Search");
                totalSize = jsonArray.length();
                for (int i = 0; i < totalSize; i++) {
                    publishProgress((int) (((float)i * 100f) / (float)totalSize));
                    //(int)(((float)transfer * 100f) / (float)total)
                    JSONObject singleResult = jsonArray.getJSONObject(i);
                    String imdbID = singleResult.getString("imdbID");
                    String theMovie = sendHttpRequest(URL + "i=" + imdbID);
                    JSONObject jsonObject = new JSONObject(theMovie);
                    Movie theMovieFromJson = new Movie(jsonObject);
                    movieArrayList.add(theMovieFromJson);
                    // String name = singleResult.getString("Title");
                }
                return movieArrayList;

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        //====================ArrayList Adapter=================

        class MyArrayListAdapter extends ArrayAdapter<Movie> {
            View v;

            public MyArrayListAdapter(Context context, int resource, List<Movie> objects) {
                super(context, resource, objects);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                v = convertView;
                if (v == null) {
                    LayoutInflater vi = LayoutInflater.from(SearchMovieOnline.this);
                    v = vi.inflate(R.layout.layout_of_item, null);
                }
                Movie theMovie = getItem(position);

                TextView editTitle = (TextView) v.findViewById(R.id.title_id);
                ImageView image = (ImageView)v.findViewById(R.id.thumbnail);

                editTitle.setText(theMovie.getTitle());

                DrawableRequestBuilder<String> thumbnailRequest = Glide
                        .with( getContext() )
                        .load( theMovie.getImgUrl() );

                Glide.with(getContext())
                        .load(theMovie.getImgUrl())
                        .thumbnail(thumbnailRequest)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .centerCrop()
                        .into(image);

                return v;
            }
        }
    }
}


/* private void setFontTitleToolBar() {
        try {
            Field f = toolbar.getClass().getDeclaredField("mTitleTextView");
            titleTextView = (TextView)f.get(toolbar);
            f.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();}
        catch (IllegalAccessException e) {
            e.printStackTrace();}
       // getSupportActionBar().setTitle("  Search movie online");
    }*/


     /*   RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL + "s=" + strSerch,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(),Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(request);*/