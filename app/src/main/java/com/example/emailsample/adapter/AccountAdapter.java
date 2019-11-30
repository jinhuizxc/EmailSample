package com.example.emailsample.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.emailsample.R;
import com.example.emailsample.activity.EditAccountActivity;
import com.example.emailsample.bean.EmailBean;

import java.util.List;

public class AccountAdapter extends BaseQuickAdapter<EmailBean, BaseViewHolder> {

    public AccountAdapter(int layoutResId, @Nullable List<EmailBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final EmailBean emailBean) {
        helper.setText(R.id.account_item, emailBean.getAddress());

        helper.setOnClickListener(R.id.item_layout, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                int position = helper.getAdapterPosition();
                Intent intent = new Intent(mContext, EditAccountActivity.class);
                intent.putExtra("account",emailBean.getAddress());
                intent.putExtra("emailId",emailBean.getId());
                Activity activity = (Activity) mContext;
                activity.startActivityForResult(intent,0);
            }
        });

    }
}
