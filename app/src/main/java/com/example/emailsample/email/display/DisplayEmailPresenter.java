package com.example.emailsample.email.display;

import android.app.Activity;
import android.webkit.WebResourceResponse;

import com.example.emailsample.bean.MessageBean;

import java.io.File;

public class DisplayEmailPresenter implements IDisplayEmailPresenter {

    private IDisplayEmailView iDisplayEmailView;
    private IDisplayEmailModel iDisplayEmailModel;

    public DisplayEmailPresenter(IDisplayEmailView iDisplayEmailView){
        this.iDisplayEmailView = iDisplayEmailView;
        iDisplayEmailModel = new DisplayEmailModel(this);
    }

    @Override
    public void getInformation(Activity activity) {
        int emailId = iDisplayEmailView.getEmailId();
        int messageId = iDisplayEmailView.getMessageId();
        iDisplayEmailModel.getMessageInformation(emailId, messageId,activity);
    }

    @Override
    public File download(String url, String fileName) {
        return iDisplayEmailModel.downloadFile(url, fileName);
    }

    @Override
    public WebResourceResponse getWebResourceResponse(String url) {
        return iDisplayEmailModel.getWebResourceResponse(url);
    }


    @Override
    public void getInformationSuccess(MessageBean messageBean) {
        iDisplayEmailView.initializeInterface(messageBean);
        if (messageBean.isAttachment()){
            iDisplayEmailView.setRecyclerView(iDisplayEmailModel.getAttachmentInformation());
            iDisplayEmailView.showAttachmentLayout();
        }
    }

    @Override
    public void getInformationFail() {
        iDisplayEmailView.showErrorMessage();
    }


}
