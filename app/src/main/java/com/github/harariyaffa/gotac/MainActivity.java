package com.github.harariyaffa.gotac;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements TextView.OnEditorActionListener, View.OnClickListener {

    public static final String USER_NAME="userName";
    public static final String IS_USER_EXISTS="isUserExists";

    Boolean isUserExists = false;
    String strUserName;
    EditText userNameET;
    Intent intent;
    ImageView iconImg;
    TextView titleName;
    Button registerBtn;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Allema Free Demo.ttf");
        titleName.setTypeface(typeface);
        userNameET.setOnEditorActionListener(this);
        isUserExists = preferences.getBoolean(IS_USER_EXISTS, isUserExists);
        if(isUserExists){
            intent=new Intent(this,MovieList.class);
            startActivity(intent);
            finish();
        }
        registerBtn.setOnClickListener(this);
    }
    public void init(){
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        userNameET = (EditText)findViewById(R.id.userName_et);
        iconImg = (ImageView) findViewById(R.id.icon_img);
        titleName = (TextView)findViewById(R.id.app_titleTV);
        registerBtn = (Button)findViewById(R.id.registerBtn);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch (actionId){
            case EditorInfo.IME_ACTION_SEND:{
                Intent i = new Intent(getApplicationContext(), MovieList.class);
                startActivity(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        strUserName = userNameET.getText().toString();
        if(strUserName.length() <= 0){
            userNameET.setError(" need at least one character ");
        }else {
        isUserExists = true;
        editor = preferences.edit();
        editor.putBoolean(IS_USER_EXISTS, isUserExists);
        editor.putString(USER_NAME,strUserName);
        editor.commit();
        intent = new Intent(this,MovieList.class);
        startActivity(intent);
        finish();
        }
    }
}

/*  try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.github.harariyaffa.gotac",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }*/

  /*  FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) view.findViewById(R.id.usersettings_fragment_login_button);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() { ... });*/