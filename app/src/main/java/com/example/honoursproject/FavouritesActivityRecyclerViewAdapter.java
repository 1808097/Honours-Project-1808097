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
import com.example.honoursproject.Data.Manga;
import com.example.honoursproject.Data.MangaDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

public class FavouritesActivityRecyclerViewAdapter extends RecyclerView.Adapter<FavouritesActivityRecyclerViewAdapter.FavouritesActivityViewHolder>{

    private Context context;
    public static RecyclerView.Adapter adapter;

    private MangaDatabase database;

    private Bitmap bitmap;

    public FavouritesActivityRecyclerViewAdapter(Context context) {
        super();
        this.context = context;
        database = MangaDatabase.getDatabase(context);
        adapter=this;
    }

    @NonNull
    @Override
    public FavouritesActivityRecyclerViewAdapter.FavouritesActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(this.context).inflate(R.layout.activity_favourites_recycler_view, parent, false);
        FavouritesActivityRecyclerViewAdapter.FavouritesActivityViewHolder viewHolder = new FavouritesActivityRecyclerViewAdapter.FavouritesActivityViewHolder(itemView, this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FavouritesActivityRecyclerViewAdapter.FavouritesActivityViewHolder holder, final int position) {
        final Manga manga = database.mangaDao().getAllMangas().get(position);

        TextView tv_title = holder.itemView.findViewById(R.id.tv_title);
        tv_title.setText(manga.getTitle());

        TextView tv_author = holder.itemView.findViewById(R.id.tv_author);
        tv_author.setText(manga.getAuthor());

        TextView tv_artist = holder.itemView.findViewById(R.id.tv_artist);
        tv_artist.setText(manga.getArtist());

        final TextView tv_chapter_warning = holder.itemView.findViewById(R.id.tv_chapter_warning);

        Button button = holder.itemView.findViewById(R.id.btn_select);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MangaActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                intent.putExtra("MANGA_ID", manga.getManga_id());
                intent.putExtra("AUTHOR_ID", manga.getAuthor_id());
                intent.putExtra("ARTIST_ID", manga.getArtist_id());
                intent.putExtra("COVER_FILE_NAME", manga.getCover_id());
                context.startActivity(intent);
            }
        });

        button = holder.itemView.findViewById(R.id.btn_delete);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.mangaDao().delete(manga);

                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, getItemCount());
                adapter.notifyDataSetChanged();
            }
        });

        ImageView iv = holder.itemView.findViewById(R.id.iv_cover);

        final String url = "https://uploads.mangadex.org/covers/" + manga.getManga_id() + "/" + manga.getCover_id();

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

        Uri uri = Uri.parse("https://api.mangadex.org/chapter");
        Uri.Builder chapterBuilder = uri.buildUpon();
        chapterBuilder.appendQueryParameter("manga", manga.getManga_id());
        chapterBuilder.appendQueryParameter("translatedLanguage[]", manga.getLanguage());
        String chapterUrl = chapterBuilder.build().toString();

        StringRequest chapterRequest = new StringRequest(Request.Method.GET, chapterUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            JSONArray data = json.getJSONArray("data");

                            if(manga.getChapterNumber() < json.getInt("total")){
                                tv_chapter_warning.setText("New chapters available!");
                            };

                        } catch (JSONException e) {
                            Toast.makeText(context, "Error using API", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //toast in case API returns nothing. Recommends using another name to find plant
                        Toast.makeText(context, "Something Went Wrong 1", Toast.LENGTH_LONG).show();
                    }
                });

        //send request
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(chapterRequest);

    }

    @Override
    public int getItemCount() {
        return database.mangaDao().getAllMangas().size();
    }

    class FavouritesActivityViewHolder extends RecyclerView.ViewHolder {
        private View itemView;
        private FavouritesActivityRecyclerViewAdapter adapter;

        public FavouritesActivityViewHolder(@NonNull View itemView, FavouritesActivityRecyclerViewAdapter adapter) {
            super(itemView);
            this.itemView = itemView;
            this.adapter = adapter;
        }
    }
}
