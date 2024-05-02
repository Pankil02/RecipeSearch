package com.example.recipesearch;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.recipesearch.data.RecipeData;
import com.example.recipesearch.data.RecipeDataDAO;
import com.example.recipesearch.data.RecipeDatabase;
import com.example.recipesearch.databinding.ActivityMainBinding;
import com.example.recipesearch.ui.RecipeSearchViewModel;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    RecipeDataDAO rDAO;
    RecipeSearchViewModel recipeModel;
    ArrayList<RecipeData> data = new ArrayList<RecipeData>();
    RecyclerView.Adapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.recyclerviewMain.setLayoutManager(new LinearLayoutManager(this));
        setSupportActionBar(binding.toolbar);

        recipeModel = new ViewModelProvider(this).get(RecipeSearchViewModel.class);

        if(data == null) {
            recipeModel.Rdata.postValue( data = new ArrayList<RecipeData>());
        }

        data = recipeModel.Rdata.getValue();

        Executor thread = Executors.newSingleThreadExecutor();
        thread.execute(() ->
        {
            RecipeDatabase db = Room.databaseBuilder(getApplicationContext(), RecipeDatabase.class, "Recipes-Database").build();
            rDAO = db.rDAO();

        });

        myAdapter= new RecyclerView.Adapter<MyRowHolder>() {
            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_show, parent, false);
                // Create and return a new instance of MyRowHolder with the inflated layout
                return new MyRowHolder(view);
            }

            @SuppressLint("CheckResult")
            @Override
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {
                RecipeData obj =data.get(position);
                holder.recipeTitle.setText(String.valueOf(obj.getTitle()));
                holder.recipeId.setText(String.valueOf(obj.getItemId()));

                Glide.with(holder.itemView.getContext())
                        .load(obj.getImageUrl())
                        .into(holder.recipeImage);
//
            }

            @Override
            public int getItemCount() {
                return data != null ? data.size() : 0;
            }

        };
        binding.recyclerviewMain.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();


        binding.button.setOnClickListener(c->{
            recipeModel.Rdata.setValue(data);
            myAdapter.notifyDataSetChanged();

        });
        getRecipes();
    }

    public void getRecipes() {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url="https://api.spoonacular.com/recipes/complexSearch?query=" + binding.searchText.getText().toString() + "&apiKey=75601041d8844b8fa58a85e5572a78c0";

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray results=response.getJSONArray("results");
                            data = new ArrayList<RecipeData>();
                            for(int i=0;i<results.length();i++) {

                                JSONObject recipeObject =results.getJSONObject(i);

                                String iteemid = recipeObject.getString("id");
                                String title1 = recipeObject.getString("title");
                                String imageUrl1 = recipeObject.getString("image");

                                RecipeData singleData = new RecipeData(iteemid, title1, imageUrl1);

                                data.add(singleData);
                            }
                        } catch (JSONException e) {
                            Log.e("JSON Parse Error", "Error parsing JSON response: " + e.getMessage());
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"Error in Fecting Api..",Toast.LENGTH_SHORT).show();
                Log.e("api","onErrorResponse: "+ error.getLocalizedMessage());
            }
        });
        queue.add(stringRequest);
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

            });
            itemView.findViewById(R.id.fav).setOnClickListener(p->{

            });

            itemView.setOnClickListener(p->{

            });

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }


}