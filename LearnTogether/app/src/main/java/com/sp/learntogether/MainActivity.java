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
import com.sp.learntogether.ui.notifications.NotificationsFragment;
import com.sp.learntogether.ui.home.HomeFragment;
import com.sp.learntogether.ui.dashboard.DashboardFragment;

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
                R.id.navigation_dashboard,
                R.id.navigation_home,
                R.id.navigation_notifications,
                R.id.navigation_Profile
        )
                .setOpenableLayout(binding.drawerLayout)
                .build();

//        NavigationUI.setupWithNavController(binding.navDrawer, navController);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBar);
//
//        NavigationUI.setupWithNavController(binding.navView, navController);

        botNavView = binding.navView;
        botNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if(itemId == R.id.navigation_tutorials){
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    openFragment(new DashboardFragment());
                    return true;
                }
                else if(itemId == R.id.navigation_Plan){
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    openFragment(new HomeFragment());
                    return true;
                }
                else if(itemId == R.id.navigation_Community){
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    openFragment(new NotificationsFragment());
                    return true;
                }
                else{
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    openFragment(new ProfileFragment());
                }
                return false;
            }
        });

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