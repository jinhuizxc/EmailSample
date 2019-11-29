package com.example.emailsample.email.display;

import com.example.emailsample.bean.Attachment;
import com.example.emailsample.bean.MessageBean;

import java.util.List;

public interface IDisplayEmailView {

    int getEmailId();
    int getMessageId();
    void initializeInterface(MessageBean messageBean);
    void showAttachmentLayout();
    void setRecyclerView(List<Attachment> list);
    void showErrorMessage();

}
