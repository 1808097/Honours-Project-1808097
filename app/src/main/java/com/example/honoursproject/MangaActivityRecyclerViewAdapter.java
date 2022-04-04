package com.example.honoursproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class MangaActivityRecyclerViewAdapter extends RecyclerView.Adapter<MangaActivityRecyclerViewAdapter.MangaActivityViewHolder>{

    private Context context;
    public static RecyclerView.Adapter adapter;

    public ArrayList<String[]> chapters;

    public MangaActivityRecyclerViewAdapter(Context context, ArrayList<String[]> chapters) {
        super();
        this.context = context;
        this.chapters = chapters;

        adapter=this;
    }

    @NonNull
    @Override
    public MangaActivityRecyclerViewAdapter.MangaActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(this.context).inflate(R.layout.activity_manga_recycler_view, parent, false);
        MangaActivityRecyclerViewAdapter.MangaActivityViewHolder viewHolder = new MangaActivityRecyclerViewAdapter.MangaActivityViewHolder(itemView, this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MangaActivityRecyclerViewAdapter.MangaActivityViewHolder holder, final int position) {
        TextView tv_chapter_title = holder.itemView.findViewById(R.id.tv_chapter_title);
        tv_chapter_title.setText(chapters.get(position)[1]);

        TextView tv_chapter_number = holder.itemView.findViewById(R.id.tv_chapter_number);
        tv_chapter_number.setText(chapters.get(position)[2]);

        Button button = holder.itemView.findViewById(R.id.btn_chapter_recycler_button);
        button.setVisibility(View.VISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReadingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("CHAPTER_ID", chapters.get(position)[0]);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    class MangaActivityViewHolder extends RecyclerView.ViewHolder {
        private View itemView;
        private MangaActivityRecyclerViewAdapter adapter;

        public MangaActivityViewHolder(@NonNull View itemView, MangaActivityRecyclerViewAdapter adapter) {
            super(itemView);
            this.itemView = itemView;
            this.adapter = adapter;
        }
    }
}
