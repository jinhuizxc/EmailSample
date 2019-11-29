package com.example.emailsample.email.edit;

import android.content.Intent;

import com.example.emailsample.bean.Attachment;

public interface IEditPresenter {

    void checkEmail(String recipient,String cc,String bcc);
    Attachment getEditAttachment(Intent intent);
    void removeAttachment(int position);
    void sendMessage(String recipient,String cc,String bcc,String subject,String content);
    void sending();
    void sendSuccess();
    void sendFailed();

}
