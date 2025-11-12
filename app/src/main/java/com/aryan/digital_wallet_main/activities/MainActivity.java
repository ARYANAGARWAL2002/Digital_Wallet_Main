    package com.aryan.digital_wallet_main.activities;
    import android.content.Intent;
    import android.os.Bundle;
    import android.view.MenuItem;
    import android.view.View;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.fragment.app.Fragment;

    import com.aryan.digital_wallet_main.R;
    import com.aryan.digital_wallet_main.fragments.HomeFragment;
    import com.aryan.digital_wallet_main.fragments.CardListFragment;
    import com.aryan.digital_wallet_main.fragments.SettingsFragment;
    import com.aryan.digital_wallet_main.utils.SecurityHelper;
    import com.google.android.material.bottomnavigation.BottomNavigationView;
    import com.google.android.material.floatingactionbutton.FloatingActionButton;

    public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener {

        private BottomNavigationView bottomNavigationView;
        private FloatingActionButton fabAddCard;
        private SecurityHelper securityHelper;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


            securityHelper = new SecurityHelper(this);


            if (!securityHelper.isLoggedIn()) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return;
            }

            setContentView(R.layout.activity_main);

            bottomNavigationView = findViewById(R.id.bottom_navigation);
            fabAddCard = findViewById(R.id.fab_add_card);

            bottomNavigationView.setOnItemSelectedListener(this);
            fabAddCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, AddCardActivity.class);
                    startActivity(intent);
                }
            });

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, new HomeFragment())
                        .commit();
            }
        }

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.navigation_cards) {
                selectedFragment = new CardListFragment();
            } else if (itemId == R.id.navigation_settings) {
                selectedFragment = new SettingsFragment();
            }

            if (selectedFragment != null) {
                try {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.nav_host_fragment, selectedFragment)
                            .commit();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error loading fragment", Toast.LENGTH_SHORT).show();
                }
            }
            return false;
        }
    }