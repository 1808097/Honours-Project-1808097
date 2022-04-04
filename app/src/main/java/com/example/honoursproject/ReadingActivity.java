package com.example.honoursproject;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.honoursproject.Data.ConstantValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;

public class ReadingActivity extends AppCompatActivity implements View.OnClickListener {

    private int currentPage;

    private Bitmap bitmap;

    private JSONArray chapterData;
    private String hash;

    private Intent launcher;
    private ImageView iv_page;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(ConstantValues.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        if(sharedPreferences.getBoolean(ConstantValues.BUTTON_POSITION, true)){
            setContentView(R.layout.activity_reading_bottom);
        }
        else{
            setContentView(R.layout.activity_reading_top);
        }

        currentPage = 0;

        iv_page = (ImageView)findViewById(R.id.iv_page);
        iv_page.setOnTouchListener(new ZoomInZoomOut());

        launcher = getIntent();

        Button btn_goto_left_page = (Button) findViewById(R.id.btn_goto_left_page);
        btn_goto_left_page.setOnClickListener(this);

        Button btn_goto_right_page = (Button) findViewById(R.id.btn_goto_right_page);
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

                            if (hash.isEmpty()) {
                                Toast.makeText(getApplicationContext(), "This chapter is unavailable to read", Toast.LENGTH_LONG).show();
                                finish();
                            }
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
                        Toast.makeText(getApplicationContext(), "Could not retrieve chapter", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(pagesRequest);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_goto_left_page) {
            if (currentPage == 0) {
                //Do nothing
            } else {
                currentPage--;
                try {
                    showMangaPage(chapterData.getString(currentPage));
                } catch (JSONException e) {

                }
            }
        } else {
            if (currentPage == chapterData.length() - 1) {
                //Do nothing
            } else {
                currentPage++;
                try {
                    showMangaPage(chapterData.getString(currentPage));
                } catch (JSONException e) {

                }
            }
        }
    }


    private void showMangaPage(String imageFileName) {
        final String url = "https://uploads.mangadex.org/data/" + hash + "/" + imageFileName;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        try {
            thread.join();
        } catch (Exception e) {

        }

        iv_page.setImageBitmap(bitmap);
    }

    class ZoomInZoomOut extends Activity implements View.OnTouchListener {
        private static final String TAG = "Touch";
        @SuppressWarnings("unused")
        private static final float MIN_ZOOM = 1f, MAX_ZOOM = 1f;

        // These matrices will be used to scale points of the image
        Matrix matrix = new Matrix();
        Matrix savedMatrix = new Matrix();

        // The 3 states (events) which the user is trying to perform
        static final int NONE = 0;
        static final int DRAG = 1;
        static final int ZOOM = 2;
        int mode = NONE;

        // these PointF objects are used to record the point(s) the user is touching
        PointF start = new PointF();
        PointF mid = new PointF();
        float oldDist = 1f;

        /**
         * Called when the activity is first created.
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_reading_bottom);
            //ImageView view = (ImageView) findViewById(R.id.iv_page);
            //view.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ImageView view = (ImageView) v;
            view.setScaleType(ImageView.ScaleType.MATRIX);
            float scale;

            dumpEvent(event);
            // Handle touch events here...

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:   // first finger down only
                    matrix.set(view.getImageMatrix());
                    savedMatrix.set(matrix);
                    start.set(event.getX(), event.getY());
                    Log.d(TAG, "mode=DRAG"); // write to LogCat
                    mode = DRAG;
                    break;

                case MotionEvent.ACTION_UP: // first finger lifted

                case MotionEvent.ACTION_POINTER_UP: // second finger lifted

                    mode = NONE;
                    Log.d(TAG, "mode=NONE");
                    break;

                case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down

                    oldDist = spacing(event);
                    Log.d(TAG, "oldDist=" + oldDist);
                    if (oldDist > 5f) {
                        savedMatrix.set(matrix);
                        midPoint(mid, event);
                        mode = ZOOM;
                        Log.d(TAG, "mode=ZOOM");
                    }
                    break;

                case MotionEvent.ACTION_MOVE:

                    if (mode == DRAG) {
                        matrix.set(savedMatrix);
                        matrix.postTranslate(event.getX() - start.x, event.getY() - start.y); // create the transformation in the matrix  of points
                    } else if (mode == ZOOM) {
                        // pinch zooming
                        float newDist = spacing(event);
                        Log.d(TAG, "newDist=" + newDist);
                        if (newDist > 5f) {
                            matrix.set(savedMatrix);
                            scale = newDist / oldDist; // setting the scaling of the
                            // matrix...if scale > 1 means
                            // zoom in...if scale < 1 means
                            // zoom out
                            matrix.postScale(scale, scale, mid.x, mid.y);
                        }
                    }
                    break;
            }

            view.setImageMatrix(matrix); // display the transformation on screen

            return true; // indicate event was handled
        }

        /*
         * --------------------------------------------------------------------------
         * Method: spacing Parameters: MotionEvent Returns: float Description:
         * checks the spacing between the two fingers on touch
         * ----------------------------------------------------
         */

        private float spacing(MotionEvent event) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float) Math.sqrt(x * x + y * y);
        }

        /*
         * --------------------------------------------------------------------------
         * Method: midPoint Parameters: PointF object, MotionEvent Returns: void
         * Description: calculates the midpoint between the two fingers
         * ------------------------------------------------------------
         */

        private void midPoint(PointF point, MotionEvent event) {
            float x = event.getX(0) + event.getX(1);
            float y = event.getY(0) + event.getY(1);
            point.set(x / 2, y / 2);
        }

        /**
         * Show an event in the LogCat view, for debugging
         */
        private void dumpEvent(MotionEvent event) {
            String names[] = {"DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE", "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?"};
            StringBuilder sb = new StringBuilder();
            int action = event.getAction();
            int actionCode = action & MotionEvent.ACTION_MASK;
            sb.append("event ACTION_").append(names[actionCode]);

            if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) {
                sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
                sb.append(")");
            }

            sb.append("[");
            for (int i = 0; i < event.getPointerCount(); i++) {
                sb.append("#").append(i);
                sb.append("(pid ").append(event.getPointerId(i));
                sb.append(")=").append((int) event.getX(i));
                sb.append(",").append((int) event.getY(i));
                if (i + 1 < event.getPointerCount())
                    sb.append(";");
            }

            sb.append("]");
            Log.d("Touch Events ---------", sb.toString());
        }
    }

}