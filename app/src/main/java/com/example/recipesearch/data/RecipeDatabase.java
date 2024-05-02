package com.example.recipesearch.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {RecipeData.class}, version = 1)
public abstract class RecipeDatabase extends RoomDatabase {
    public abstract RecipeDataDAO rDAO();
}
