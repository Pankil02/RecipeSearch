package com.example.recipesearch.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class RecipeData {

    @PrimaryKey(autoGenerate=true)
    @ColumnInfo(name="id")
    public int id;

    @ColumnInfo(name="ItemID")
    public String itemId;
    @ColumnInfo(name="Title")
    String title;
    @ColumnInfo(name="ImageURL")
    String imageUrl;

    public RecipeData() {
    }

    public RecipeData(String itemId, String title, String imageUrl) {
        this.itemId = itemId;
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
