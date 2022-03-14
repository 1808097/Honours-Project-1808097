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

    private String manga_id;
    private String title;
    private String author;
    private String artist;
    private String cover_id;

    private ArrayList<String[]> mangas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        sv_search = (SearchView)findViewById(R.id.sv_search);
        sv_search.setOnQueryTextListener(this);

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

        return true;
    }
}