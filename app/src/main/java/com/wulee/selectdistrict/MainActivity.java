package com.wulee.selectdistrict;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wulee.seldistictlibrary.entity.SearchComPlaceResult;
import com.wulee.seldistictlibrary.ui.FindPlaceFragment;

public class MainActivity extends AppCompatActivity {

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    private Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        btnConfirm = (Button) findViewById(R.id.btn_confirm);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        final FindPlaceFragment findPlaceFragment = new FindPlaceFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(FindPlaceFragment.INTENT_REQ_LEVEL, 3);
        findPlaceFragment.setArguments(bundle);

        fragmentTransaction.add(R.id.fragment_container, findPlaceFragment);
        fragmentTransaction.commit();


        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchComPlaceResult place = findPlaceFragment.getSelectDistrictInfo();
                StringBuilder sb = new StringBuilder();
                sb.append(place.getProvinceName()).append(place.getCityName()).append(place.getAreaName());
                Toast.makeText(MainActivity.this, sb.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
