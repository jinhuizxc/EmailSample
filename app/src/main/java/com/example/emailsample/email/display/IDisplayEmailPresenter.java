package com.example.emailsample.email.display;

import android.app.Activity;
import android.webkit.WebResourceResponse;

import com.example.emailsample.bean.MessageBean;

import java.io.File;

public interface IDisplayEmailPresenter {

    void getInformation(Activity activity);
    File download(String url, String fileName);
    WebResourceResponse getWebResourceResponse(String url);
    void getInformationSuccess(MessageBean messageBean);
    void getInformationFail();

}
