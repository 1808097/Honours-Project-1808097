package com.example.honoursproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.honoursproject.Data.ConstantValues;
import com.example.honoursproject.Data.Manga;
import com.example.honoursproject.Data.MangaDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MangaActivity extends AppCompatActivity implements View.OnClickListener {

    private MangaDatabase database;

    private int currentChapters;
    private int totalChapters;

    private TextView tv_title;
    private TextView tv_author;
    private TextView tv_artist;
    private TextView tv_genres;

    private String author;
    private String artist;
    private String title;

    private MangaActivityRecyclerViewAdapter adapter;

    private Intent launcher;

    private ArrayList<String[]> chapters = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga);

        database = MangaDatabase.getDatabase(getApplicationContext());

        launcher = getIntent();

        currentChapters = 0;

        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_author = (TextView)findViewById(R.id.tv_author);
        tv_artist = (TextView)findViewById(R.id.tv_artist);
        tv_genres = (TextView)findViewById(R.id.tv_genres);

        Button btn_chapters_left = (Button)findViewById(R.id.btn_chapters_left);
        btn_chapters_left.setOnClickListener(this);

        Button btn_chapters_right = (Button)findViewById(R.id.btn_chapters_right);
        btn_chapters_right.setOnClickListener(this);

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

        Switch checkBox = (Switch) findViewById(R.id.sw_favourite);

        List<Manga> mangaList = database.mangaDao().getAllMangas();
        for(Manga manga : mangaList){
            if(manga.getManga_id().equals(launcher.getStringExtra("MANGA_ID"))){
                checkBox.setChecked(true);
            }
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Manga manga = new Manga();
                    manga.setManga_id(launcher.getStringExtra("MANGA_ID"));
                    manga.setTitle(title);
                    manga.setAuthor_id(launcher.getStringExtra("AUTHOR_ID"));
                    manga.setAuthor(author);
                    manga.setArtist_id(launcher.getStringExtra("ARTIST_ID"));
                    manga.setArtist(artist);
                    manga.setCover_id(launcher.getStringExtra("COVER_FILE_NAME"));
                    database.mangaDao().insert(manga);
                }
                else{
                    database.mangaDao().deleteManga(launcher.getStringExtra("MANGA_ID"));
                }
            }

        });

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

                            JSONObject titleJSON = attributes.getJSONObject("title");
                            title = titleJSON.getString("en");
                            tv_title.setText(title);

                            JSONArray tags = attributes.getJSONArray("tags");

                            String genres = "";
                            for(int i = 0; i < tags.length(); i++){
                                JSONObject object = tags.getJSONObject(i);
                                JSONObject objectAttributes = object.getJSONObject("attributes");
                                if(objectAttributes.getString("group").equals("genre")){
                                    JSONObject name = objectAttributes.getJSONObject("name");
                                    genres += ", " + name.getString("en");
                                }
                            }

                            tv_genres.setText(genres.substring(2));

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

                            author = attributes.getString("name");
                            tv_author.setText(author);

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

                            artist = attributes.getString("name");
                            tv_artist.setText(artist);

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
        chapterBuilder.appendQueryParameter("limit", Integer.toString(ConstantValues.CHAPTER_NUMBER_LIMIT));
        chapterBuilder.appendQueryParameter("translatedLanguage[]", ConstantValues.CHOSEN_LANGUAGE);
        String chapterUrl = chapterBuilder.build().toString();

        StringRequest chapterRequest = new StringRequest(Request.Method.GET, chapterUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            JSONArray data = json.getJSONArray("data");

                            totalChapters = json.getInt("total");

                            for(int i = 0; i < data.length(); i++){
                                JSONObject chapter = data.getJSONObject(i);
                                JSONObject attributes = chapter.getJSONObject("attributes");

                                chapters.add(new String[]{chapter.getString("id"), attributes.getString("title")});
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

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_chapters_left){
            if(currentChapters==0){
                //Do nothing
            }
            else{
                currentChapters--;
                getChapterList();
            }
        }
        else{
            if((currentChapters+1)*ConstantValues.CHAPTER_NUMBER_LIMIT>totalChapters){
                //Do nothing
            }
            else{
                currentChapters++;
                getChapterList();
            }
        }

    }

    public void getChapterList(){
        Uri uri = Uri.parse("https://api.mangadex.org/chapter");
        Uri.Builder chapterBuilder = uri.buildUpon();
        chapterBuilder.appendQueryParameter("manga", launcher.getStringExtra("MANGA_ID"));
        chapterBuilder.appendQueryParameter("limit", Integer.toString(ConstantValues.CHAPTER_NUMBER_LIMIT));
        chapterBuilder.appendQueryParameter("translatedLanguage[]", ConstantValues.CHOSEN_LANGUAGE);
        chapterBuilder.appendQueryParameter("offset", Integer.toString(currentChapters*ConstantValues.CHAPTER_NUMBER_LIMIT));
        String chapterUrl = chapterBuilder.build().toString();

        StringRequest chapterRequest = new StringRequest(Request.Method.GET, chapterUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            JSONArray data = json.getJSONArray("data");

                            chapters.clear();

                            for(int i = 0; i < data.length(); i++){
                                JSONObject chapter = data.getJSONObject(i);
                                JSONObject attributes = chapter.getJSONObject("attributes");

                                chapters.add(new String[]{chapter.getString("id"), attributes.getString("title")});
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
                        Toast.makeText(getApplicationContext(), "Something Went Wrong 1", Toast.LENGTH_LONG).show();
                    }
                });

        //send request
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(chapterRequest);
    }

}