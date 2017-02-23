package com.wulee.selectdistrict;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wulee.seldistictlibrary.db.PlaceDao;
import com.wulee.seldistictlibrary.entity.SearchComPlaceResult;
import com.wulee.seldistictlibrary.ui.FindPlaceActivity;
import com.wulee.seldistictlibrary.ui.FindPlaceFragment;
import com.wulee.seldistictlibrary.utils.Util;

public class MainActivity extends AppCompatActivity {


    private Button btnActivity;
    private Button btnFragment;

    private Button btnConfirm;

    public static final int PLACE_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        btnConfirm = (Button) findViewById(R.id.btn_confirm);
        btnActivity = (Button) findViewById(R.id.btn_activity);
        btnFragment = (Button) findViewById(R.id.btn_fragment);

        btnFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

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
        });
        btnActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, FindPlaceActivity.class),PLACE_REQUEST);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            SearchComPlaceResult pr = (SearchComPlaceResult) data.getSerializableExtra("place");
            String areaId = "";
            if (Util.isSpeRegion(pr.getProvinceID())) {
                areaId = String.format("%06d", pr.getProvinceID());
            } else {
                areaId = String.format("%06d", pr.getAreaID());
            }
            if (!TextUtils.isEmpty(pr.getProvinceName())) {
                showPlaceByAreaId(areaId);
            }
        }
    }

    /**
     * 通过区Id显示所在地区
     * @param areaId
     */
    private void showPlaceByAreaId(String areaId) {
        SearchComPlaceResult placeResult = new PlaceDao(this).getDistrictInfoByAreaId(areaId);
        int selProvinceId = placeResult.getProvinceID();
        String placeInfo = "";
        if (Util.isSpeRegion(selProvinceId)) {
            placeInfo = placeResult.getProvinceName();
        } else if (Util.isDireGovernment(selProvinceId)) {
            placeInfo= placeResult.getProvinceName() + "-" + placeResult.getAreaName();
        } else {
            placeInfo = placeResult.getProvinceName() + "-" + placeResult.getCityName() + "-" + placeResult.getAreaName();
        }
        Toast.makeText(this, placeInfo, Toast.LENGTH_SHORT).show();
    }
}
