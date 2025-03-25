package com.igenesys.model;

import com.igenesys.utils.Utils;

public class LoginUser {

    private String strUserName;
    private String strPassword;

    public LoginUser(String UserName, String Password) {
        strUserName = UserName;
        strPassword = Password;
    }

    public String getStrUserName() {
        return strUserName;
    }

    public String getStrPassword() {
        return strPassword;
    }

    public boolean isUserNameValid() {
        return Utils.isNullOrEmpty(getStrUserName());
    }


    public boolean isPasswordLengthGreaterThan5() {
        return getStrPassword().length() > 5;
    }

}
