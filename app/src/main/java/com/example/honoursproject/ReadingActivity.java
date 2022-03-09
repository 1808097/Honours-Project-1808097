package com.example.honoursproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
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

import java.io.Console;
import java.io.InputStream;
import java.net.URL;

public class ReadingActivity extends AppCompatActivity implements View.OnClickListener{

    private int currentPage;

    private Bitmap bitmap;

    private JSONArray chapterData;
    private String hash;

    private Intent launcher;

    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        currentPage = 0;

        iv = (ImageView)findViewById(R.id.iv_manga_test);

        launcher = getIntent();

        Button btn_goto_left_page = (Button)findViewById(R.id.btn_goto_left_page);
        btn_goto_left_page.setBackgroundColor(Color.TRANSPARENT);
        btn_goto_left_page.setOnClickListener(this);

        Button btn_goto_right_page = (Button)findViewById(R.id.btn_goto_right_page);
        btn_goto_right_page.setBackgroundColor(Color.TRANSPARENT);
        btn_goto_right_page.setOnClickListener(this);

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

                            showMangaPage(chapterData.getString(currentPage));

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

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_goto_left_page){
            if(currentPage==0){
                System.out.println("TEST LEFT NOTHING");
            }
            else{
                currentPage--;
                try{
                    System.out.println("LEFT UPDATE");
                    showMangaPage(chapterData.getString(currentPage));
                } catch (JSONException e) {

                }
            }
        }
        else{
            if(currentPage==chapterData.length()-1){
                System.out.println("TEST RIGHT NOTHING");
            }
            else{
                currentPage++;
                try{
                    showMangaPage(chapterData.getString(currentPage));
                } catch (JSONException e) {

                }
            }
        }
    }

    private void showMangaPage(String imageFileName){
        System.out.println("TEST READING STARTED");

        final String url = "https://uploads.mangadex.org/data/" + hash + "/" + imageFileName;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    bitmap = BitmapFactory.decodeStream((InputStream)new URL(url).getContent());
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
        System.out.println("TEST READING OVER");
    }
}