package com.example.testtools.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.testtools.Fragment.Fragment_CommSetting;
import com.example.testtools.Fragment.Fragment_Stress_Test;
import com.example.testtools.R;
import com.example.testtools.ui.main.SectionsPagerAdapter;
import com.pax.poslink.CommSetting;


public class MainActivity extends AppCompatActivity
    implements Fragment_CommSetting.OnSavedCommSettingListener, Fragment_Stress_Test.GetCommSetting {

    CommSetting commSetting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof Fragment_CommSetting) {
            Fragment_CommSetting fragment_commSetting = (Fragment_CommSetting) fragment;
            fragment_commSetting.setOnSavedCommSettingListener(this);
        }
    }

    // Get commSetting from Fragment_CommSetting
    @Override
    public void onSavedCommSetting(CommSetting commSetting){
        this.commSetting = commSetting;
    }

    // Pass commSetting to Fragment_StressTest
    @Override
    public CommSetting getCommSetting(){
        return commSetting;
    }

}