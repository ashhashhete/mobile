package com.igenesys;


import android.view.WindowManager;

import com.igenesys.databinding.ActivityUnitBinding;
import com.igenesys.view.FormPageViewModel;
import com.stfalcon.androidmvvmhelper.mvvm.activities.BindingActivity;


public class UnitActivity extends BindingActivity<ActivityUnitBinding, FormPageViewModel> {

    @Override
    public FormPageViewModel onCreate() {
        return new FormPageViewModel(this);
    }

    @Override
    public int getVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        return R.layout.activity_unit;
    }

}