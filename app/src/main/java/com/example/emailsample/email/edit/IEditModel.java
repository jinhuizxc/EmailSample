package com.example.emailsample.email.edit;

import android.content.Intent;

import com.example.emailsample.bean.Attachment;

public interface IEditModel {

    boolean checkAddress(String address);
    Attachment getInformationFromIntent(Intent intent, int editMessageId);
    void removeAttachment(int position);
    void sendMessage(String recipient,String cc,String bcc,String subject,String content,long messageId);

}
