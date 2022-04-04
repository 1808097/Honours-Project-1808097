package com.example.honoursproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.honoursproject.Data.ConstantValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private SearchView sv_search;
    private Spinner sp_search_setting;

    private SearchActivityRecyclerViewAdapter adapter;

    private String manga_id;
    private String title;
    private String author;
    private String artist;
    private String cover_id;

    private String query;

    private ArrayList<String[]> mangas;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        SharedPreferences sharedPreferences = getSharedPreferences(ConstantValues.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        if(!sharedPreferences.getBoolean(ConstantValues.DARKMODE, true)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        sv_search = (SearchView)findViewById(R.id.sv_search);
        sv_search.setOnQueryTextListener(this);

        query = "title";

        sp_search_setting = (Spinner)findViewById(R.id.sp_search_setting);
        sp_search_setting.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    query = "title";
                }
                else if(position == 1){
                    query = "authors[]";
                }
                else if(position == 2){
                    query = "artists[]";
                }
                else if(position == 3){
                    query = "includedTags[]";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //empty
            }
        });

        Button btn_search = (Button)findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                finish();
                startActivity(intent);
            }
        });

        Button btn_favourites = (Button)findViewById(R.id.btn_favourites);
        btn_favourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FavouritesActivity.class);
                finish();
                startActivity(intent);
            }
        });

        Button btn_settings = (Button)findViewById(R.id.btn_settings);
        btn_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                finish();
                startActivity(intent);
            }
        });

        mangas = new ArrayList<>();

        RecyclerView rv = findViewById(R.id.rv_manga_list);
        adapter = new SearchActivityRecyclerViewAdapter(getApplicationContext(), mangas);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(final String query){
        if (sp_search_setting.getSelectedItemPosition() == 0) {
            searchMangas(query);

        } else if (sp_search_setting.getSelectedItemPosition() == 3) {

            Uri uri = Uri.parse("https://api.mangadex.org/manga/tag");
            Uri.Builder builder = uri.buildUpon();
            String url = builder.build().toString();

            StringRequest mangaRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject json = new JSONObject(response);
                                JSONArray data = json.getJSONArray("data");

                                String genre_id = "";

                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject tag = data.getJSONObject(i);

                                    JSONObject attributes = tag.getJSONObject("attributes");

                                    JSONObject name = attributes.getJSONObject("name");

                                    if(query.equals(name.getString("en").toLowerCase())){
                                        genre_id = tag.getString("id");
                                    }
                                }

                                if(genre_id.equals("")){
                                    Toast.makeText(getApplicationContext(), "No such genre found", Toast.LENGTH_LONG).show();
                                }
                                else{
                                    searchMangas(genre_id);
                                }

                            } catch (JSONException e) {
                                Toast.makeText(getApplicationContext(), "Error using API", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //toast in case API returns nothing. Recommends using another name to find plant
                            Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_LONG).show();
                        }
                    });

            //send request
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(mangaRequest);

        } else {

            Uri uri = Uri.parse("https://api.mangadex.org/author");
            Uri.Builder builder = uri.buildUpon();
            builder.appendQueryParameter("name", query);
            String url = builder.build().toString();

            StringRequest mangaRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject json = new JSONObject(response);
                                JSONArray data = json.getJSONArray("data");

                                String author_id = "";

                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject tag = data.getJSONObject(i);

                                    JSONObject attributes = tag.getJSONObject("attributes");

                                    if (query.equals(attributes.getString("name").toLowerCase())) {
                                        author_id = tag.getString("id");
                                    }
                                }

                                if(author_id.equals("")){
                                    if(sp_search_setting.getSelectedItemPosition() == 1){
                                        Toast.makeText(getApplicationContext(), "No such author found", Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(), "No such artist found", Toast.LENGTH_LONG).show();
                                    }
                                }
                                else{
                                    searchMangas(author_id);
                                }

                            } catch (JSONException e) {
                                Toast.makeText(getApplicationContext(), "Error using API", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //toast in case API returns nothing. Recommends using another name to find plant
                            Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_LONG).show();
                        }
                    });

            //send request
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(mangaRequest);
        }
        return true;
    }

    public void searchMangas(String query){
        mangas.clear();

        Uri uri = Uri.parse("https://api.mangadex.org/manga");
        Uri.Builder builder = uri.buildUpon();
        builder.appendQueryParameter(this.query, query);
        String url = builder.build().toString();

        StringRequest mangaRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            JSONArray data = json.getJSONArray("data");

                            for(int i=0; i < data.length(); i++){
                                JSONObject manga = data.getJSONObject(i);

                                manga_id = manga.getString("id");

                                JSONObject attributes = manga.getJSONObject("attributes");
                                JSONObject titleObject = attributes.getJSONObject("title");

                                title = titleObject.getString("en");

                                JSONArray relationships = manga.getJSONArray("relationships");

                                for(int j = 0; j < relationships.length(); j++){
                                    JSONObject object = relationships.getJSONObject(j);

                                    if(object.getString("type").equals("author")){
                                        author = object.getString("id");
                                    }
                                    else if(object.getString("type").equals("artist")){
                                        artist = object.getString("id");
                                    }
                                    else if(object.getString("type").equals("cover_art")){
                                        cover_id = object.getString("id");
                                    }

                                }
                                mangas.add(new String[]{manga_id, title, author, artist, cover_id});
                            }
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Error using API", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //toast in case API returns nothing. Recommends using another name to find plant
                        Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_LONG).show();
                    }
                });

        //send request
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(mangaRequest);
    }
}