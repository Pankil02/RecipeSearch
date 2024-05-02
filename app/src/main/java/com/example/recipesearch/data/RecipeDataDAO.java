package com.example.recipesearch.data;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecipeDataDAO {
    @Insert
    public long insertMessage(RecipeData d);

    @Query("Select * from RecipeData")
    public List<RecipeData> getAllData();

    @Delete
    int deleteMessage(RecipeData d);

}
