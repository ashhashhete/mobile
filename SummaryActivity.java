package com.igenesys;

import android.view.WindowManager;

import com.igenesys.databinding.ActivitySummaryBinding;
import com.igenesys.view.SummaryViewModel;
import com.stfalcon.androidmvvmhelper.mvvm.activities.BindingActivity;

public class SummaryActivity  extends BindingActivity<ActivitySummaryBinding, SummaryViewModel> {

    @Override
    public SummaryViewModel onCreate() {
        return new SummaryViewModel(this);
    }

    @Override
    public int getVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        App.getSharedPreferencesHandler().putString("unit_unique_id","");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        return R.layout.activity_summary;
    }
}