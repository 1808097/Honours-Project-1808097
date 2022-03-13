package com.example.honoursproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private SearchView sv_search;

    private SearchActivityRecyclerViewAdapter adapter;

    private String MANGA_ID;
    private String AUTHOR;
    private String ARTIST;

    private ArrayList<String[]> mangas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        sv_search = (SearchView)findViewById(R.id.sv_search);
        sv_search.setOnQueryTextListener(this);

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
    public boolean onQueryTextSubmit(String query){
        Uri uri = Uri.parse("https://api.mangadex.org/manga");

        Uri.Builder builder = uri.buildUpon();

        builder.appendQueryParameter("title", query);

        String url = builder.build().toString();

        StringRequest mangaRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject json = new JSONObject(response);
                        JSONArray data = json.getJSONArray("data");

                        JSONObject firstManga = data.getJSONObject(0);

                        MANGA_ID = firstManga.getString("id");

                        JSONObject attributes = firstManga.getJSONObject("attributes");
                        JSONObject title = attributes.getJSONObject("title");

                        JSONArray relationships = firstManga.getJSONArray("relationships");

                        for(int i = 0; i < relationships.length(); i++){
                            JSONObject object = relationships.getJSONObject(i);

                            if(object.getString("type").equals("author")){
                                AUTHOR = object.getString("id");
                            }
                            else if(object.getString("type").equals("artist")){
                                ARTIST = object.getString("id");
                            }

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

        return true;
    }
}