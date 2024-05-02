package com.example.recipesearch;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.example.recipesearch.data.RecipeData;
import com.example.recipesearch.data.RecipeDataDAO;
import com.example.recipesearch.data.RecipeDatabase;
import com.example.recipesearch.databinding.ActivityMainBinding;
import com.example.recipesearch.ui.RecipeSearchViewModel;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    RecipeDataDAO rDAO;
    RecipeSearchViewModel recipeModel;
    ArrayList<RecipeData> data = new ArrayList<RecipeData>();
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

    }
}