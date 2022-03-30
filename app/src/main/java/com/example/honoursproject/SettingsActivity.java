package com.example.honoursproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.honoursproject.Data.ConstantValues;
import com.example.honoursproject.Data.Manga;

import java.util.List;

import static android.content.res.Configuration.UI_MODE_NIGHT_MASK;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor prefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences(ConstantValues.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        prefEditor = sharedPreferences.edit();

        Switch sw_darkmode = (Switch)findViewById(R.id.sw_darkmode);
        if(sharedPreferences.getBoolean(ConstantValues.DARKMODE, true)){
            sw_darkmode.setChecked(true);
        }
        sw_darkmode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    prefEditor.putBoolean(ConstantValues.DARKMODE, true);
                    prefEditor.apply();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    prefEditor.putBoolean(ConstantValues.DARKMODE, false);
                    prefEditor.apply();
                }
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
    }
}