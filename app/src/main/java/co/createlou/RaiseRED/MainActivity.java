package co.createlou.RaiseRED;

import android.support.annotation.NonNull;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;

import android.widget.TextView;
import android.graphics.Color;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.FileDownloadTask;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";

    //declaring auth
    private FirebaseAuth mAuth;

    //declaring auth_listener
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        TabActivity tabhost = new TabActivity();
        //instantiating onStartup intent for usage in setting start activity
        final Intent openingIntent = new Intent(MainActivity.this, OpeningActivity.class);

        //instantiating auth
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    setupToolBar();
                    setupTabHost();
                    setupProfilePage();
                } else {
                    // User is signed out
                    startActivity(openingIntent);
                    finish();
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

    }

    public void setupToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Chat");
        }
    }

    public void setupTabHost() {

        //Initialize Tab Host
        TabHost tabhost = (TabHost) findViewById(R.id.tabhost);

        tabhost.setup();
        //Tab 1
        addTab("Chat", R.drawable.tab1_selector, R.id.tab1);
        //Tab 2
        addTab("Events", R.drawable.tab2_selector, R.id.tab2);
        //Tab 3
        addTab("Guide", R.drawable.tab3_selector, R.id.tab3);
        //Tab 4
        addTab("Profile", R.drawable.tab4_selector, R.id.tab4);

        for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++) {
            tabhost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#f2f1ef")); //unselected
        }
    }
    private void setupProfilePage(){
        ImageView imageView = (ImageView) findViewById(R.id.imageView2);
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://raisered-39293.appspot.com");
        StorageReference profileRef = storageRef.child("images/"+user.getUid());
        Glide.with(this).using(new FirebaseImageLoader()).load(profileRef).into(imageView);
    }

    private void addTab(String label, int drawableId, int tabID) {
        //Initialize Tab Host
        final TabHost tabhost = (TabHost) findViewById(R.id.tabhost);
        TabHost.TabSpec spec = tabhost.newTabSpec(label);

        View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, tabhost.getTabWidget(), false);
        TextView title = (TextView) tabIndicator.findViewById(R.id.title);
        title.setText(label);
        title.setTextColor(Color.parseColor("#c4c4c4"));

        ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
        icon.setImageResource(drawableId);

        spec.setIndicator(tabIndicator);
        spec.setContent(tabID);
        tabhost.addTab(spec);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void signOut(View view) {
        FirebaseAuth.getInstance().signOut();

        if(LoginManager.getInstance() == null){

        }else{
            LoginManager.getInstance().logOut();
        }
    }

}