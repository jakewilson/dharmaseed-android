package org.dharmaseed.androidapp;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ListView talkListView;
    TalkListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        talkListView = (ListView) findViewById(R.id.talksListView);
        ArrayList<String> talkTitles = new ArrayList<String>();
        talkTitles.add("The merits of cute girl tickling!");
        talkTitles.add("Hello there");
        adapter = new TalkListViewAdapter(NavigationActivity.this, talkTitles);
        talkListView.setAdapter(adapter);
        new DataFetcherTask().execute();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        ListView talksListView = (ListView) findViewById(R.id.talksListView);
//        talksListView.setSelection(0);
//        talksListView.set

//        try {
//            MediaPlayer mediaPlayer = new MediaPlayer();
//            mediaPlayer.setDataSource("http://dharmaseed.org/teacher/305/talk/32388/20160204-Kate_Munding-IMCB-sila_virtue_additional_focus_to_wise_and_harmonious_speech_and_communication.mp3");
//            mediaPlayer.prepare();
//            mediaPlayer.start();
//        } catch (IOException e) {
//
//        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_talks) {
            Log.i("nav", "going to talks");
        } else if (id == R.id.nav_teachers) {

        } else if (id == R.id.nav_centers) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    class DataFetcherTask extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... params) {

            ArrayList<String> result = new ArrayList<>();

            OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("edition", "2016-02-06")
                    .addFormDataPart("detail", "1")
                    .build();
            Request request = new Request.Builder()
                    .url("http://www.dharmaseed.org/api/1/talks/")
                    .post(formBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    JSONObject json = new JSONObject(response.body().string());
                    JSONObject talks = json.getJSONObject("items");
                    Iterator<String> it = talks.keys();
                    while (it.hasNext()) {
                        String talkId = it.next();
                        JSONObject talk = talks.getJSONObject(talkId);
                        try {
                            String title = talk.getString("title");
                            result.add(title);
                        } catch (JSONException e) {}
                    }
                } else {

                }
            } catch (Exception e) {
                Log.e("dataFetcher", e.toString());
            }

            return result;

        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            super.onPostExecute(result);
            adapter = new TalkListViewAdapter(NavigationActivity.this, result);
            talkListView.setAdapter(adapter);

        }
    }

}
