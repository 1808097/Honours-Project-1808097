package com.example.honoursproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.MODE_PRIVATE;

public class MangaActivityRecyclerViewAdapter extends RecyclerView.Adapter<MangaActivityRecyclerViewAdapter.ListPlantViewHolder>{

    public static final String PLANT_ID = "plantId";

    private Context context;
    private PlantDatabase database;
    private Plant plant;
    private SharedPreferences sharedPreferences;

    public static RecyclerView.Adapter adapter;

    public PlantListRecyclerViewAdapter(Context context) {
        super();
        this.context = context;
        database = PlantDatabase.getDatabase(context);
        adapter=this;
        sharedPreferences = context.getSharedPreferences(MainActivity.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
    }

    @NonNull
    @Override
    public PlantListRecyclerViewAdapter.ListPlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(this.context).inflate(R.layout.activity_plant_list_recycler_view, parent, false);
        PlantListRecyclerViewAdapter.ListPlantViewHolder viewHolder = new PlantListRecyclerViewAdapter.ListPlantViewHolder(itemView, this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlantListRecyclerViewAdapter.ListPlantViewHolder holder, int position) {
        plant = database.plantDao().getAllPlants().get(position);

        //displays plant data
        TextView tv = holder.itemView.findViewById(R.id.tv_recycler_name);
        tv.setText(database.plantDao().getAllPlants().get(position).getName());

        tv = holder.itemView.findViewById(R.id.tv_recycler_species);
        tv.setText(database.plantDao().getAllPlants().get(position).getSpecies());

        tv = holder.itemView.findViewById(R.id.tv_recycler_location);
        tv.setText(database.plantDao().getAllPlants().get(position).getLocation());

        tv = holder.itemView.findViewById(R.id.tv_recycler_water);
        //displays amount of water needed if data is present
        if(plant.getWaterAmountNeeded()==0){
            tv.setText(context.getString(R.string.tv_recycler_no_water));
        }
        //displays different measurement units
        else if(sharedPreferences.getBoolean(MainActivity.PREFERRED_LIQUID_UNIT, true)){
            tv.setText(context.getString(R.string.tv_recycler_water, Functions.convertToLiters(plant.getWaterAmountNeeded()), "L"));
        }
        else{
            tv.setText(context.getString(R.string.tv_recycler_water, Functions.convertToPints(plant.getWaterAmountNeeded()), "pt"));
        }

        tv = holder.itemView.findViewById(R.id.tv_recycler_warning);
        tv.setText(context.getString(R.string.tv_recycler_warning ,Functions.convertToRemainingTime(database.plantDao().getAllPlants().get(position).getTimeLastWatered().getTime())));


        Button button = holder.itemView.findViewById(R.id.btn_change);
        button.setOnClickListener(new PlantListClickListener(plant, position));

        button = holder.itemView.findViewById(R.id.btn_delete);
        button.setOnClickListener(new PlantListClickListener(plant, position));
    }

    @Override
    public int getItemCount() {
        return database.plantDao().getAllPlants().size();
    }

    class ListPlantViewHolder extends RecyclerView.ViewHolder {
        private View itemView;
        private PlantListRecyclerViewAdapter adapter;

        public ListPlantViewHolder(@NonNull View itemView, PlantListRecyclerViewAdapter adapter) {
            super(itemView);
            this.itemView = itemView;
            this.adapter = adapter;
        }
    }

    class PlantListClickListener implements View.OnClickListener{
        private Plant plant;
        private int position;
        public PlantListClickListener(Plant plant, int position){
            this.plant=plant;
            this.position=position;
        }
        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.btn_change){
                //creates new create activity to modify plant as user wishes
                Intent intent = new Intent(context, CreatePlantActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(PLANT_ID, plant.getId());
                context.startActivity(intent);
            }
            else{
                //removes plant from database, updates recycler list
                database.plantDao().delete(plant);

                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, getItemCount());
                adapter.notifyDataSetChanged();
            }
        }
    }
}
