package com.example.honoursproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.ArrayList;

public class SearchActivityRecyclerViewAdapter extends RecyclerView.Adapter<SearchActivityRecyclerViewAdapter.SearchActivityViewHolder>{

    private Context context;
    private static RecyclerView.Adapter adapter;

    private ArrayList<String[]> mangas;

    private String coverFileName;

    private Bitmap bitmap;

    public SearchActivityRecyclerViewAdapter(Context context, ArrayList<String[]> mangas) {
        super();
        this.context = context;
        this.mangas = mangas;

        adapter=this;
    }

    @NonNull
    @Override
    public SearchActivityRecyclerViewAdapter.SearchActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(this.context).inflate(R.layout.activity_search_recycler_view, parent, false);
        SearchActivityRecyclerViewAdapter.SearchActivityViewHolder viewHolder = new SearchActivityRecyclerViewAdapter.SearchActivityViewHolder(itemView, this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchActivityRecyclerViewAdapter.SearchActivityViewHolder holder, final int position) {
        TextView tv = holder.itemView.findViewById(R.id.tv_title);
        tv.setText(mangas.get(position)[1]);

        final ImageView iv = holder.itemView.findViewById(R.id.iv_cover);

        Uri uri = Uri.parse("https://api.mangadex.org/cover/" + mangas.get(position)[4]);
        Uri.Builder builder = uri.buildUpon();
        String url = builder.build().toString();

        StringRequest mangaRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            JSONObject data = json.getJSONObject("data");
                            JSONObject attributes = data.getJSONObject("attributes");
                            coverFileName = attributes.getString("fileName");

                            final String url = "https://uploads.mangadex.org/covers/" + mangas.get(position)[0] + "/" + coverFileName;

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

                        } catch (JSONException e) {
                            Toast.makeText(context, "Error using API", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_LONG).show();
                    }
                });

        //send request
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(mangaRequest);


        Button button = holder.itemView.findViewById(R.id.btn_read);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MangaActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                intent.putExtra("MANGA_ID", mangas.get(position)[0]);
                intent.putExtra("AUTHOR_ID", mangas.get(position)[2]);
                intent.putExtra("ARTIST_ID", mangas.get(position)[3]);
                intent.putExtra("COVER_FILE_NAME", coverFileName);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mangas.size();
    }

    class SearchActivityViewHolder extends RecyclerView.ViewHolder {
        private View itemView;
        private SearchActivityRecyclerViewAdapter adapter;

        public SearchActivityViewHolder(@NonNull View itemView, SearchActivityRecyclerViewAdapter adapter) {
            super(itemView);
            this.itemView = itemView;
            this.adapter = adapter;
        }
    }
}
