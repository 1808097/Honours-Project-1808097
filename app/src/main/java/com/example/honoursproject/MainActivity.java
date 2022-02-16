package com.example.honoursproject;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private TextView tv_change;
    private TextView tv_submit;
    private SearchView sv_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_change = (TextView)findViewById(R.id.tv_change);
        tv_submit = (TextView)findViewById(R.id.tv_submit);
        sv_search = (SearchView)findViewById(R.id.sv_search);

        sv_search.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        tv_change.setText(newText);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query){
        Uri uri = Uri.parse("https://api.mangadex.org/manga");

        Uri.Builder builder = uri.buildUpon();

        builder.appendQueryParameter("title", query);

        String url = builder.build().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject json = new JSONObject(response);
                        JSONArray data = json.getJSONArray("data");

                        JSONObject firstManga = data.getJSONObject(0);

                        JSONObject attributes = firstManga.getJSONObject("attributes");

                        JSONObject title = attributes.getJSONObject("title");

                        tv_submit.setText(title.getString("en"));

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

        return true;
    }
}