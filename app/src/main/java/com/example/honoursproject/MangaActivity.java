package com.example.honoursproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
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

public class MangaActivity extends AppCompatActivity {

    private TextView tv_title;
    private TextView tv_author;
    private TextView tv_artist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga);

        Intent launcher = getIntent();

        Uri uri = Uri.parse("https://api.mangadex.org/chapter");
        Uri.Builder builder = uri.buildUpon();

        builder.appendQueryParameter("manga", launcher.getStringExtra("MANGA_ID"));
        builder.appendQueryParameter("limit", "100");

        String url = builder.build().toString();

        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_author = (TextView)findViewById(R.id.tv_author);
        tv_artist = (TextView)findViewById(R.id.tv_artist);

        tv_title.setText(launcher.getStringExtra("MANGA_ID"));
        tv_author.setText(launcher.getStringExtra("AUTHOR"));
        tv_artist.setText(launcher.getStringExtra("ARTIST"));


        /*
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
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

                            btn_submit.setText(title.getString("en"));

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
        queue.add(stringRequest);

        */
    }



}