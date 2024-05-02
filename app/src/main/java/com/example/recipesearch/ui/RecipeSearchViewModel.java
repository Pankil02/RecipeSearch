package com.example.recipesearch.ui;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.recipesearch.data.RecipeData;

import java.util.ArrayList;

public class RecipeSearchViewModel extends ViewModel {
    public MutableLiveData<ArrayList<RecipeData>> Rdata = new MutableLiveData<>();
}
