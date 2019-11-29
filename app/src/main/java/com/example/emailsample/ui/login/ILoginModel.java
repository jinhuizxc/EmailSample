package com.example.emailsample.ui.login;

import android.app.Activity;

public interface ILoginModel {

    boolean checkAddress(String address);
    void login(String address,String password,String protocol,String logo,
               String host,String smtpHost, Activity activity);
}
