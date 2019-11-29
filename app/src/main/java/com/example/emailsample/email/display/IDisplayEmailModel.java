package com.example.emailsample.email.display;

import android.app.Activity;
import android.webkit.WebResourceResponse;

import com.example.emailsample.bean.Attachment;

import java.io.File;
import java.util.List;

public interface IDisplayEmailModel {

    void getMessageInformation(int emailId, int messageId, Activity activity);
    File downloadFile(String url, String fileName);
    WebResourceResponse getWebResourceResponse(String url);
    List<Attachment> getAttachmentInformation();

}
