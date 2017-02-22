package com.wulee.selectdistrict;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.wulee.seldistictlibrary.ui.FindPlaceFragment;

public class MainActivity extends AppCompatActivity {

    android.support.v4.app.FragmentManager fragmentManager;
    android.support.v4.app.FragmentTransaction fragmentTransaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        FindPlaceFragment findPlaceFragment = new FindPlaceFragment();
        fragmentTransaction.add(R.id.fragment_container, findPlaceFragment);
        fragmentTransaction.commit();
    }
}
