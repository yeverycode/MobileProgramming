package com.example.tomato;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Set default fragment and title on initial start
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new TimeTableFragment()).commit();
            getSupportActionBar().setTitle("시간표");
        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_timetable) {
                    selectedFragment = new TimeTableFragment();
                } else if (itemId == R.id.navigation_todo) {
                    selectedFragment = new TodoFragment();
                } else if (itemId == R.id.navigation_timer) {
                    selectedFragment = new TimerFragment();
                } else if (itemId == R.id.navigation_memo) {
                    selectedFragment = new MemoFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    // Set the toolbar title to the selected item's title
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(item.getTitle());
                    }
                }
                return true;
            };
}
