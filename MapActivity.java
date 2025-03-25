package com.igenesys;


import android.view.WindowManager;

import com.igenesys.databinding.ActivityMapBinding;
import com.igenesys.view.MapViewModel;
import com.stfalcon.androidmvvmhelper.mvvm.activities.BindingActivity;

public class MapActivity extends BindingActivity<ActivityMapBinding, MapViewModel> {

    @Override
    public MapViewModel onCreate() {
        return new MapViewModel(this);
    }

    @Override
    public int getVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        return R.layout.activity_map;
    }
}