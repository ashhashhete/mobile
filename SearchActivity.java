package com.igenesys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.WindowManager;

import com.igenesys.databinding.ActivitySearchBinding;
import com.igenesys.utils.Constants;
import com.igenesys.view.SearchViewModel;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        ActivitySearchBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
//        setContentView(R.layout.activity_search);

        SearchViewModel searchViewModel = new SearchViewModel(this, binding);
    }
}