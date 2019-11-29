package com.example.emailsample.ui.main;

import android.app.Activity;

import com.example.emailsample.bean.MessageBean;

import java.util.List;

public class MainPresenter implements IMainPresenter {

    private IMainView iMainView;
    private IMainModel iMainModel;

    public MainPresenter(IMainView iMainView){
        this.iMainView = iMainView;
        iMainModel = new MainModel(this);
    }

    @Override
    public void getMessageList(Activity activity) {
        iMainView.showProgressBar();
        iMainModel.getMessageList(iMainView.getEmailId(),activity);
    }

    @Override
    public void getListSuccess(List<MessageBean> list) {
        iMainView.hideProgressBar();
        iMainView.showRecyclerView(list);
    }

    @Override
    public void getListFail() {
        iMainView.hideProgressBar();
        iMainView.showErrorMessage();
    }

    @Override
    public void refreshData(int emailId) {
        iMainModel.refreshData(emailId);
    }

    @Override
    public void refreshSuccess(List<MessageBean> list) {
        iMainView.showRecyclerView(list);
        iMainView.hideRefresh();
        iMainView.showSuccessMessage();
    }

    @Override
    public void refreshFail() {
        iMainView.hideRefresh();
        iMainView.showErrorMessage();
    }

    @Override
    public void deleteEmail(final List<Integer> list) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                iMainModel.deleteEmail(list);
                iMainModel.updateSubscript();
            }
        }).start();
    }

}

