package com.example.emailsample.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.emailsample.bean.EmailBean;

import java.util.List;

public class AccountAdapter extends BaseQuickAdapter<EmailBean, BaseViewHolder> {

    public AccountAdapter(int layoutResId, @Nullable List<EmailBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, EmailBean emailBean) {
//        helper.account.setText(emailBean.getAddress());
    }
}
