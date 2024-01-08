package com.sp.learntogether;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.AppBarConfigurationKt;
import androidx.navigation.ui.NavigationUI;

import com.sp.learntogether.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AppBarConfiguration appBar;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navController = NavHostFragment.findNavController(getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main));
        appBar = new AppBarConfiguration.Builder(
                R.id.navigation_dashboard,
                R.id.navigation_home,
                R.id.navigation_notifications
        )
                .setOpenableLayout(binding.drawerLayout)
                .build();

        NavigationUI.setupWithNavController(binding.navDrawer, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, appBar);

        NavigationUI.setupWithNavController(binding.navView, navController);


    }


    @Override
    public boolean onSupportNavigateUp() {

        return NavigationUI.navigateUp(navController, appBar) || super.onSupportNavigateUp();
    }
}