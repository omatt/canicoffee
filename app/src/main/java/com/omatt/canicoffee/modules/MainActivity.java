package com.omatt.canicoffee.modules;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.omatt.canicoffee.R;
import com.omatt.canicoffee.modules.about.AboutFragment;
import com.omatt.canicoffee.modules.coffeetime.CanICoffeeFragment;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            changeFragment(new CanICoffeeFragment(), true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                changeFragment(new AboutFragment(), false);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Change Fragment
     * clearStack true will clear the FragmentTransaction stack
     *
     * @param fragment   Fragment
     * @param clearStack boolean
     */
    public void changeFragment(Fragment fragment, boolean clearStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        if (clearStack)
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        else fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
