package me.jaipawar.fetchcodingexercise;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class MainActivity extends AppCompatActivity {

    public static final String BASE_URL = "https://fetch-hiring.s3.amazonaws.com/";
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private ProgressBar progressBar;
    private List<Item> itemList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemAdapter = new ItemAdapter(new ArrayList<>());
        recyclerView.setAdapter(itemAdapter);
        fetchJsonData();
    }

    private void fetchJsonData() {
        progressBar.setVisibility(View.VISIBLE);
        RetrofitInstance.api.getItems().enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    itemList = response.body();
                    itemList.removeIf(item -> item.getName() == null || item.getName().trim().isEmpty());
                    Collections.sort(itemList, Comparator.comparing(Item::getListId).thenComparing(Item::getName));
                    itemAdapter = new ItemAdapter(itemList);
                    recyclerView.setAdapter(itemAdapter);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Log.e("MainActivity", "Error fetching JSON" + t.getMessage(), t);
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}

// Retrofit API Interface
interface ApiService {
    @GET("hiring.json")
    Call<List<Item>> getItems();
}

class RetrofitInstance {
    static final ApiService api = new Retrofit.Builder()
            .baseUrl(MainActivity.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService.class);
}