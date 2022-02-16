package com.example.honoursproject;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

public class ReadingActivity extends AppCompatActivity {

    private ImageView iv;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        iv = (ImageView)findViewById(R.id.iv_manga_test);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    bitmap = BitmapFactory.decodeStream((InputStream)new URL("https://uploads.mangadex.org/data/0ef5fef468663e9aefacc5e3611e8915/d1-9e2d4dc4bc972d1bd3951ece3a24a3ae0afb0c54e253a0ffda875073f3428063.jpg").getContent());
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

    }
}