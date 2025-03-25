package com.igenesys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.igenesys.R;
import com.igenesys.databinding.ActivityHohBinding;
import com.igenesys.databinding.ActivityUnitBinding;
import com.igenesys.view.FormPageViewModel;
import com.igenesys.view.HOHViewModel;
import com.stfalcon.androidmvvmhelper.mvvm.activities.BindingActivity;

public class HohActivity extends BindingActivity<ActivityHohBinding, HOHViewModel> {

    @Override
    public HOHViewModel onCreate() {
        return new HOHViewModel(this);
    }

    @Override
    public int getVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        return R.layout.activity_hoh;
    }

}