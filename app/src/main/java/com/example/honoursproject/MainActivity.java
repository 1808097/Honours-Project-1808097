package com.example.honoursproject;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;

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
        tv_submit.setText(query);
        return true;
    }
}