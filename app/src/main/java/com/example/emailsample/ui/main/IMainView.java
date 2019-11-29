package com.example.emailsample.ui.main;

import com.example.emailsample.bean.MessageBean;

import java.util.List;

public interface IMainView {

    int getEmailId();
    void showProgressBar();
    void hideProgressBar();
    void showRecyclerView(List<MessageBean> list);
    void hideRefresh();
    void showSuccessMessage();
    void showErrorMessage();
}
