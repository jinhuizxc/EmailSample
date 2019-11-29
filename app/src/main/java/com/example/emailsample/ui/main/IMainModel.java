package com.example.emailsample.ui.main;

import android.app.Activity;

import java.util.List;

public interface IMainModel {

    void getMessageList(int emailId, Activity activity);
    void getListFromServer();
    void refreshData(int emailId);
    void deleteEmail(List<Integer> list);
    void updateSubscript();
}
