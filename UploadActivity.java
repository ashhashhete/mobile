package com.igenesys;

import android.view.WindowManager;

import com.igenesys.databinding.ActivitySurveyLocalListBinding;
import com.igenesys.view.SurveyListPageViewModel;
import com.stfalcon.androidmvvmhelper.mvvm.activities.BindingActivity;

public class UploadActivity extends BindingActivity<ActivitySurveyLocalListBinding, SurveyListPageViewModel> {

    @Override
    public SurveyListPageViewModel onCreate() {
        return new SurveyListPageViewModel(this);
    }

    @Override
    public int getVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        return R.layout.activity_survey_local_list;
    }
}