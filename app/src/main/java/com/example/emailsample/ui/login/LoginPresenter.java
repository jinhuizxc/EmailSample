package com.example.emailsample.ui.login;

import android.app.Activity;

public class LoginPresenter implements ILoginPresenter {

    private ILoginView iLoginView;
    private ILoginModel iLoginModel;

    public LoginPresenter(ILoginView iLoginView) {
        this.iLoginView = iLoginView;
        iLoginModel = new LoginModel(this);
    }

    @Override
    public void checkEmail(String address, String password) {
        if (iLoginModel.checkAddress(address) && !password.equals("")) {
            iLoginView.showCarryOut();
        } else {
            iLoginView.hideCarryout();
        }
    }

    @Override
    public void login(String address, String password, String protocol, String logo,
                      String host, String smtpHost, Activity activity) {
        iLoginView.showProgressDialog();
        iLoginModel.login(address, password, protocol, logo, host, smtpHost, activity);
    }

    @Override
    public void loginSuccess(int email_id) {
        iLoginView.hideProgressDialog();
        iLoginView.jumpInterface(email_id);
        iLoginView.finishActivity();
    }

    @Override
    public void loginFail() {
        iLoginView.hideProgressDialog();
        iLoginView.showErrorDialog();
    }
}
