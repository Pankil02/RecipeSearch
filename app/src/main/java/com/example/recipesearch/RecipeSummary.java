package com.example.recipesearch;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.recipesearch.databinding.ActivityRecipeSummaryBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RecipeSummary extends AppCompatActivity {

    ActivityRecipeSummaryBinding binding;
    String image;
    String summary ;
    String urlT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_summary);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding = ActivityRecipeSummaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        String idd = getIntent().getStringExtra("id");
        getRecipeSummary(idd);


        binding.button.setOnClickListener(c->{
            binding.summaryText.setText(summary);
            binding.Url.setText(urlT);
            Glide.with(this)
                    .load(image)
                    .into(binding.imageBig);

        });
    }
    public void getRecipeSummary(String idd ){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url="https://api.spoonacular.com/recipes/"+ idd +"/information?apiKey=75601041d8844b8fa58a85e5572a78c0";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String imageO = jsonObject.getString("image");
                            String smry = jsonObject.getString("summary");
                            String srl = jsonObject.getString("spoonacularSourceUrl");

                            image = imageO;
                            summary =smry;
                            urlT=srl;

                                binding.summaryText.setText(summary);
                                binding.Url.setText(urlT);
                                Glide.with(RecipeSummary.this)
                                        .load(image)
                                        .into(binding.imageBig);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(stringRequest);

    }
}