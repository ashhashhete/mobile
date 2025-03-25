package com.igenesys.view;

import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.igenesys.model.LoginUser;

public class LoginViewModel extends ViewModel {
    public MutableLiveData<String> UserName = new MutableLiveData<>();
    public MutableLiveData<String> Password = new MutableLiveData<>();

    private MutableLiveData<LoginUser> userMutableLiveData;

    public MutableLiveData<LoginUser> getUser() {

        if (userMutableLiveData == null) {
            userMutableLiveData = new MutableLiveData<>();
        }
        return userMutableLiveData;
    }


    public void onClick(View view) {

        LoginUser loginUser = new LoginUser(UserName.getValue(), Password.getValue());

        userMutableLiveData.setValue(loginUser);

    }



}
