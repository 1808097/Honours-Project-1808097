package com.example.honoursproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import com.example.honoursproject.Data.ConstantValues;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor prefEditor;

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

        Spinner sp_chapter_language = (Spinner)findViewById(R.id.sp_chapter_language);
        switch (sharedPreferences.getString(ConstantValues.CHOSEN_LANGUAGE, "en")) {
            case "en":
                sp_chapter_language.setSelection(0);
                break;
            case "zh":
                sp_chapter_language.setSelection(1);
                break;
            case "hr":
                sp_chapter_language.setSelection(2);
                break;
            case "fr":
                sp_chapter_language.setSelection(3);
                break;
            case "de":
                sp_chapter_language.setSelection(4);
                break;
            case "id":
                sp_chapter_language.setSelection(5);
                break;
            case "it":
                sp_chapter_language.setSelection(6);
                break;
            case "jp":
                sp_chapter_language.setSelection(7);
                break;
            case "ko":
                sp_chapter_language.setSelection(8);
                break;
            case "pl":
                sp_chapter_language.setSelection(9);
                break;
            case "pt":
                sp_chapter_language.setSelection(10);
                break;
            case "ru":
                sp_chapter_language.setSelection(11);
                break;
            case "tr":
                sp_chapter_language.setSelection(12);
                break;
        }
        sp_chapter_language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        prefEditor.putString(ConstantValues.CHOSEN_LANGUAGE, "en");
                        break;
                    case 1:
                        prefEditor.putString(ConstantValues.CHOSEN_LANGUAGE, "zh");
                        break;
                    case 2:
                        prefEditor.putString(ConstantValues.CHOSEN_LANGUAGE, "hr");
                        break;
                    case 3:
                        prefEditor.putString(ConstantValues.CHOSEN_LANGUAGE, "fr");
                        break;
                    case 4:
                        prefEditor.putString(ConstantValues.CHOSEN_LANGUAGE, "de");
                        break;
                    case 5:
                        prefEditor.putString(ConstantValues.CHOSEN_LANGUAGE, "id");
                        break;
                    case 6:
                        prefEditor.putString(ConstantValues.CHOSEN_LANGUAGE, "it");
                        break;
                    case 7:
                        prefEditor.putString(ConstantValues.CHOSEN_LANGUAGE, "jp");
                        break;
                    case 8:
                        prefEditor.putString(ConstantValues.CHOSEN_LANGUAGE, "ko");
                        break;
                    case 9:
                        prefEditor.putString(ConstantValues.CHOSEN_LANGUAGE, "pl");
                        break;
                    case 10:
                        prefEditor.putString(ConstantValues.CHOSEN_LANGUAGE, "pt");
                        break;
                    case 11:
                        prefEditor.putString(ConstantValues.CHOSEN_LANGUAGE, "ru");
                        break;
                    case 12:
                        prefEditor.putString(ConstantValues.CHOSEN_LANGUAGE, "tr");
                        break;
                }
                prefEditor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            //empty
            }
        });

        Spinner sp_chapter_buttons = (Spinner)findViewById(R.id.sp_chapter_buttons);
        if(!sharedPreferences.getBoolean(ConstantValues.BUTTON_POSITION, false)){
            sp_chapter_buttons.setSelection(1);
        }
        sp_chapter_buttons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        prefEditor.putBoolean(ConstantValues.BUTTON_POSITION, true);
                        prefEditor.apply();
                    }
                    else{
                        prefEditor.putBoolean(ConstantValues.BUTTON_POSITION, false);
                        prefEditor.apply();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    //empty
                }
        });

        Spinner sp_reading_order = (Spinner)findViewById(R.id.sp_reading_order);
        if(!sharedPreferences.getBoolean(ConstantValues.READING_ORDER, false)){
            sp_reading_order.setSelection(1);
        }
        sp_reading_order.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    prefEditor.putBoolean(ConstantValues.READING_ORDER, true);
                    prefEditor.apply();
                }
                else{
                    prefEditor.putBoolean(ConstantValues.READING_ORDER, false);
                    prefEditor.apply();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //empty
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