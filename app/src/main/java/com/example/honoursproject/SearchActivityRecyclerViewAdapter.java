package com.example.honoursproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchActivityRecyclerViewAdapter extends RecyclerView.Adapter<SearchActivityRecyclerViewAdapter.SearchActivityViewHolder>{

    public static final int MANGA_NUMBER_LIMIT = 10;

    private Context context;
    public static RecyclerView.Adapter adapter;

    public ArrayList<String[]> chapters;

    public SearchActivityRecyclerViewAdapter(Context context, ArrayList<String[]> chapters) {
        super();
        this.context = context;
        this.chapters = chapters;

        adapter=this;
    }

    @NonNull
    @Override
    public SearchActivityRecyclerViewAdapter.SearchActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(this.context).inflate(R.layout.activity_manga_recycler_view, parent, false);
        SearchActivityRecyclerViewAdapter.SearchActivityViewHolder viewHolder = new SearchActivityRecyclerViewAdapter.SearchActivityViewHolder(itemView, this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchActivityRecyclerViewAdapter.SearchActivityViewHolder holder, final int position) {
        TextView tv = holder.itemView.findViewById(R.id.tv_title);
        ImageView iv = holder.itemView.findViewById(R.id.iv_cover);



        Button button = holder.itemView.findViewById(R.id.btn_read);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MangaActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                intent.putExtra("MANGA_ID", MANGA_ID);
                intent.putExtra("AUTHOR_ID", AUTHOR);
                intent.putExtra("ARTIST_ID", ARTIST);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chapters.size();
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
