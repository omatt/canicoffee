package com.omatt.canicoffee;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.omatt.canicoffee.modules.coffeetime.CanICoffeeFragment;
import com.omatt.canicoffee.utils.GlobalValues;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new CanICoffeeFragment())
                    .commit();
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
        Fragment aboutFragment = getSupportFragmentManager().findFragmentByTag(GlobalValues.FRAG_TAG_ABOUT);
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            if (aboutFragment == null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(R.id.container, new AboutFragment(), GlobalValues.FRAG_TAG_ABOUT);
                ft.addToBackStack(GlobalValues.FRAG_TAG_MAIN);
                ft.commit();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
