package com.example.honoursproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

public class ReadingActivity extends AppCompatActivity {

    private ImageView iv;

    private Bitmap bitmap;

    private JSONArray chapterData;

    private String imageFileName;
    private String mangaPageUrl;
    private String hash;

    private Intent launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        iv = (ImageView)findViewById(R.id.iv_manga_test);

        launcher = getIntent();

        Uri uri = Uri.parse("https://api.mangadex.org/at-home/server/" + launcher.getStringExtra("CHAPTER_ID"));
        Uri.Builder pagesBuilder = uri.buildUpon();
        String pagesUrl = pagesBuilder.build().toString();

        StringRequest pagesRequest = new StringRequest(Request.Method.GET, pagesUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            JSONObject chapter = json.getJSONObject("chapter");

                            hash = chapter.getString("hash");
                            chapterData = chapter.getJSONArray("data");

                            imageFileName = chapterData.getString(0);

                            mangaPageUrl = "https://uploads.mangadex.org/data/" + hash + "/" + imageFileName;

                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try  {
                                        bitmap = BitmapFactory.decodeStream((InputStream)new URL(mangaPageUrl).getContent());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            thread.start();

                            try{
                                thread.join();
                            }
                            catch(Exception e){

                            }

                            iv.setImageBitmap(bitmap);


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

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(pagesRequest);
    }
}