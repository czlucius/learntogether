package com.sp.learntogether;

import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.sp.learntogether.ui.profile.ProfileFragment;

import com.google.android.material.navigation.NavigationBarView;
import com.sp.learntogether.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AppBarConfiguration appBar;
    private NavController navController;
    private DrawerLayout drawer;
    FragmentManager fragmentManager;
    BottomNavigationView botNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navController = NavHostFragment.findNavController(getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main));
        appBar = new AppBarConfiguration.Builder(
                R.id.planner_fragment,
                R.id.tutorial_fragment,
                R.id.communities_fragment,
                R.id.profile_fragment,
                R.id.loginFragment
        )
                .setOpenableLayout(binding.drawerLayout)
                .build();

        NavigationUI.setupWithNavController(binding.navDrawer, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, appBar);

        NavigationUI.setupWithNavController(binding.navView, navController);
        binding.navView.setOnItemReselectedListener(v -> {});


        botNavView = binding.navView;


        fragmentManager = getSupportFragmentManager();


    }

    private void openFragment(Fragment fragment){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.nav_host_fragment_activity_main, fragment);
        transaction.commit();
    }


    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBar) || super.onSupportNavigateUp();
    }
}