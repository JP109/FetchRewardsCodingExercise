package me.jaipawar.fetchcodingexercise;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import android.view.MenuInflater;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ProgressBar;
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
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private ProgressBar progressBar;
    private List<Item> itemList = new ArrayList<>();
    private SharedPreferences sharedPreferences;

    public static final String BASE_URL = "https://fetch-hiring.s3.amazonaws.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load saved theme preference
        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(isDarkMode ?
                AppCompatDelegate.MODE_NIGHT_YES :
                AppCompatDelegate.MODE_NIGHT_NO);

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
                    itemList.sort(Comparator.comparingInt(Item::getListId)
                            .thenComparing(item -> extractNumericValue(item.getName()))
                            .thenComparing(Item::getName, String.CASE_INSENSITIVE_ORDER));
                    itemAdapter = new ItemAdapter(itemList);
                    recyclerView.setAdapter(itemAdapter);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // Set the correct title based on the current mode
        MenuItem toggleItem = menu.findItem(R.id.action_toggle_darkmode);
        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);
        toggleItem.setTitle(isDarkMode ? "Toggle Light Mode" : "Toggle Dark Mode");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_toggle_darkmode) {
            boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);

            // Toggle and save preference
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("dark_mode", !isDarkMode);
            editor.apply();

            // Apply the theme and restart activity
            AppCompatDelegate.setDefaultNightMode(isDarkMode ?
                    AppCompatDelegate.MODE_NIGHT_NO :
                    AppCompatDelegate.MODE_NIGHT_YES);

            recreate(); // Restart activity to apply changes
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private int extractNumericValue(String name) {
        try {
            // Extract first number found in the string
            java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\\d+").matcher(name);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group());
            }
        } catch (NumberFormatException e) {
            Log.e("SortingError", "Failed to extract number from: " + name);
        }
        return Integer.MAX_VALUE; // If no number found, push it to the end
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