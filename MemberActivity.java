package com.igenesys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.igenesys.database.dabaseModel.newDbSModels.HohInfoDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.MemberInfoDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.UnitInfoDataModel;
import com.igenesys.databinding.ActivityMemberBinding;
import com.igenesys.utils.Constants;
import com.igenesys.utils.Utils;
import com.igenesys.view.MemberDetailsViewModel;
import com.stfalcon.androidmvvmhelper.mvvm.activities.BindingActivity;

public class MemberActivity extends BindingActivity<ActivityMemberBinding, MemberDetailsViewModel> {

    SharedPreferences sPref;
    @Override
    public MemberDetailsViewModel onCreate() {

        sPref = getSharedPreferences("csp", Context.MODE_PRIVATE);
        return new MemberDetailsViewModel(this);
    }

    @Override
    public int getVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        return R.layout.activity_member;
    }

}




