package com.example.honoursproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
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
import java.util.Calendar;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MangaActivity extends AppCompatActivity {

    public static final int CHAPTER_NUMBER_LIMIT = 10;
    public static final String CHOSEN_LANGUAGE = "en";

    private TextView tv_title;
    private TextView tv_author;
    private TextView tv_artist;
    private TextView tv_genres;

    private MangaActivityRecyclerViewAdapter adapter;

    private String[][] chapters = new String[CHAPTER_NUMBER_LIMIT][];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga);

        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_author = (TextView)findViewById(R.id.tv_author);
        tv_artist = (TextView)findViewById(R.id.tv_artist);
        tv_genres = (TextView)findViewById(R.id.tv_genres);

        Intent launcher = getIntent();

        Uri uri = Uri.parse("https://api.mangadex.org/manga");
        Uri.Builder mangaBuilder = uri.buildUpon();
        mangaBuilder.appendQueryParameter("ids[]", launcher.getStringExtra("MANGA_ID"));
        String mangaUrl = mangaBuilder.build().toString();

        StringRequest mangaRequest = new StringRequest(Request.Method.GET, mangaUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            JSONArray data = json.getJSONArray("data");

                            JSONObject firstManga = data.getJSONObject(0);
                            JSONObject attributes = firstManga.getJSONObject("attributes");

                            JSONObject title = attributes.getJSONObject("title");
                            tv_title.setText(title.getString("en"));

                            JSONArray tags = attributes.getJSONArray("tags");

                            String genres = "";
                            for(int i = 0; i < tags.length(); i++){
                                JSONObject object = tags.getJSONObject(i);
                                JSONObject objectAttributes = object.getJSONObject("attributes");
                                if(objectAttributes.getString("group").equals("genre")){
                                    JSONObject name = objectAttributes.getJSONObject("name");
                                    genres += "(" + name.getString("en") + ")";
                                }
                            }

                            tv_genres.setText(genres);

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
                        Toast.makeText(getApplicationContext(), "Something Went Wrong 1", Toast.LENGTH_LONG).show();
                    }
                });


        uri = Uri.parse("https://api.mangadex.org/author/" + launcher.getStringExtra("AUTHOR_ID"));
        Uri.Builder authorBuilder = uri.buildUpon();
        String authorUrl = authorBuilder.build().toString();

        StringRequest authorRequest = new StringRequest(Request.Method.GET, authorUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            JSONObject data = json.getJSONObject("data");

                            JSONObject attributes = data.getJSONObject("attributes");

                            tv_author.setText(attributes.getString("name"));

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
                        Toast.makeText(getApplicationContext(), "Something Went Wrong 2", Toast.LENGTH_LONG).show();
                    }
                });


        uri = Uri.parse("https://api.mangadex.org/author/" + launcher.getStringExtra("ARTIST_ID"));
        Uri.Builder artistBuilder = uri.buildUpon();
        String artistUrl = artistBuilder.build().toString();

        StringRequest artistRequest = new StringRequest(Request.Method.GET, artistUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            JSONObject data = json.getJSONObject("data");

                            JSONObject attributes = data.getJSONObject("attributes");

                            tv_artist.setText(attributes.getString("name"));

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
                        Toast.makeText(getApplicationContext(), "Something Went Wrong 3", Toast.LENGTH_LONG).show();
                    }
                });

        uri = Uri.parse("https://api.mangadex.org/chapter");
        Uri.Builder chapterBuilder = uri.buildUpon();
        chapterBuilder.appendQueryParameter("manga", launcher.getStringExtra("MANGA_ID"));
        chapterBuilder.appendQueryParameter("limit", Integer.toString(CHAPTER_NUMBER_LIMIT));
        chapterBuilder.appendQueryParameter("translatedLanguage[]", CHOSEN_LANGUAGE);
        String chapterUrl = chapterBuilder.build().toString();

        StringRequest chapterRequest = new StringRequest(Request.Method.GET, chapterUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            JSONArray data = json.getJSONArray("data");

                            for(int i = 0; i < data.length(); i++){
                                JSONObject chapter = data.getJSONObject(i);
                                JSONObject attributes = chapter.getJSONObject("attributes");

                                chapters[i] = new String[]{chapter.getString("id"), attributes.getString("title")};
                            }

                            RecyclerView rv = findViewById(R.id.rv_chapter_list);
                            adapter = new MangaActivityRecyclerViewAdapter(getApplicationContext(), chapters);
                            rv.setAdapter(adapter);
                            rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

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
                        Toast.makeText(getApplicationContext(), "Something Went Wrong 1", Toast.LENGTH_LONG).show();
                    }
                });

        //send request
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(mangaRequest);
        queue.add(authorRequest);
        queue.add(artistRequest);
        queue.add(chapterRequest);
    }

}