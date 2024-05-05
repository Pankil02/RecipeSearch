package com.example.recipesearch;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.example.recipesearch.data.RecipeData;
import com.example.recipesearch.data.RecipeDataDAO;
import com.example.recipesearch.data.RecipeDatabase;
import com.example.recipesearch.databinding.ActivityRecipeFavoritesBinding;
import com.example.recipesearch.ui.RecipeSearchViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RecipeFavorites extends AppCompatActivity {

    RecyclerView.Adapter myAdapter;
    ActivityRecipeFavoritesBinding binding;
    ArrayList<RecipeData> data;
    //    SunData sunData = new SunData();
    RecipeSearchViewModel recipeModel;
    RecipeDataDAO rDAO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_favorites);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding = ActivityRecipeFavoritesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        binding.favouriteRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        recipeModel = new ViewModelProvider(this).get(RecipeSearchViewModel.class);
        Executor thread = Executors.newSingleThreadExecutor();
        thread.execute(() ->
        {
            RecipeDatabase db = Room.databaseBuilder(getApplicationContext(), RecipeDatabase.class, "Recipes-Database").build();
            rDAO = db.rDAO();

            data.addAll(rDAO.getAllData()); //Once you get the data from database

            runOnUiThread(() -> {
                recipeModel.Rdata.postValue(data);
                binding.favouriteRecyclerView.setAdapter(myAdapter);
                myAdapter.notifyDataSetChanged();
            });
        });

        data = recipeModel.Rdata.getValue();

        if(data == null)
        {
            recipeModel.Rdata.postValue( data = new ArrayList<RecipeData>());
        }

        myAdapter = new RecyclerView.Adapter<RecipeFavorites.MyRowHolder>() {
            @NonNull
            @Override
            public RecipeFavorites.MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_show, parent, false);
                // Create and return a new instance of MyRowHolder with the inflated layout
                return new MyRowHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull RecipeFavorites.MyRowHolder holder, int position) {
                RecipeData obj = data.get(position);
                holder.recipeTitle.setText(String.valueOf(obj.getTitle()));
                holder.recipeId.setText(String.valueOf(obj.getItemId()));
                Glide.with(holder.itemView.getContext())
                        .load(obj.getImageUrl())
                        .into(holder.recipeImage);
            }

            @Override
            public int getItemCount() {
                return data != null ? data.size() : 0;
            }

        };
        binding.favouriteRecyclerView.setAdapter(myAdapter);

    }
    public class MyRowHolder extends RecyclerView.ViewHolder {
        TextView recipeTitle;
        TextView recipeId;
        ImageView recipeImage;

        public MyRowHolder(@NonNull View itemView) {
            super(itemView);

            recipeId = itemView.findViewById(R.id.recipeIdText);
            recipeTitle= itemView.findViewById(R.id.recipeTitleText);
            recipeImage = itemView.findViewById(R.id.recipeImage);

            itemView.findViewById(R.id.deleteIcon).setOnClickListener(clk->{
                int position = getAbsoluteAdapterPosition();

                AlertDialog.Builder builder = new AlertDialog.Builder( RecipeFavorites.this );
                builder.setMessage("You want to delete this recipe")
                        .setTitle("Question: ")
                        .setNegativeButton ("No", (dialog, cl) -> {})
                        .setPositiveButton ( "Yes", (dialog, cl) -> {
                            RecipeData removeRecipe = data.get(position);
                            data.remove(position);
                            myAdapter.notifyItemRemoved(position);

                            Executor thread = Executors.newSingleThreadExecutor();
                            thread.execute(() -> {
                                removeRecipe.id =  rDAO.deleteMessage(removeRecipe);
                            });

                            Snackbar.make(recipeId,"You deleted recipe # "+ position,Snackbar.LENGTH_LONG)
                                    .setAction("Undo", click-> {
                                        data.add(position, removeRecipe);
                                        myAdapter.notifyItemInserted(position);
                                        // Add the removed data back to the database
                                        Executor undoThread = Executors.newSingleThreadExecutor();
                                        undoThread.execute(() -> {
                                            removeRecipe.id = (int) rDAO.insertMessage(removeRecipe);
                                        });
                                    })
                                    .show();
                        })
                        .create().show();

            });

            itemView.setOnClickListener(p->{
                int position = getAbsoluteAdapterPosition();
                RecipeData rr = data.get(position);
                String idd= rr.getItemId();
                Intent intent = new Intent(RecipeFavorites.this, RecipeSummary.class);

                intent.putExtra("id",idd);

                startActivity(intent);

            });



        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Delete all messages from the database
        if(item.getItemId() ==R.id.delIcon){

            AlertDialog.Builder builder = new AlertDialog.Builder( RecipeFavorites.this );


            builder.setMessage("Are You sure you want to delete all saved data From database")
                    .setTitle("DELETE")
                    .setNegativeButton ("No", (dialog, cl) -> {})
                    .setPositiveButton ( "Yes", (dialog, cl) -> {
                        Executor thread = Executors.newSingleThreadExecutor();
                        thread.execute(() -> {
                            for (RecipeData message : data) {
                                rDAO.deleteMessage(message);
                            }
                            runOnUiThread(() -> {
                                data.clear();
                                myAdapter.notifyDataSetChanged();
                                Toast.makeText(RecipeFavorites.this, "All messages deleted from the database", Toast.LENGTH_SHORT).show();
                            });
                        });
                    })
                    .create().show();

            return true;

        } else if(item.getItemId() ==R.id.helpIcon){

            AlertDialog.Builder builder = new AlertDialog.Builder( RecipeFavorites.this );

            builder.setMessage("Welcome to our food search app! Use the search bar to find delicious recipes. " +
                            "Tap on a recipe to view its details. Swipe left to delete recipes you no longer need. Enjoy exploring new culinary delights!" )
                    .setTitle("Helpful")
                    .setNegativeButton ("No", (dialog, cl) -> {})
                    .setPositiveButton ( "Yes", (dialog, cl) -> {})
                    .create().show();
        }
        return true;
    }


}