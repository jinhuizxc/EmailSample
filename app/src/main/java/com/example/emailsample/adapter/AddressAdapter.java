package com.example.emailsample.adapter;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.emailsample.R;
import com.example.emailsample.bean.EmailBean;

import java.util.List;

public class AddressAdapter extends BaseQuickAdapter<EmailBean, BaseViewHolder> {

    private SharedPreferences sharedPreferences;
    private List<EmailBean> data;
    private OnAddressListener listener;

    public interface OnAddressListener{
        void setEmailId(EmailBean email);
    }

    public void setAddressListener(OnAddressListener listener){
        this.listener = listener;
    }


    public AddressAdapter(int layoutResId, @Nullable List<EmailBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, EmailBean email) {
        // 显示logo
        ImageView ivLogoItem = helper.getView(R.id.logo_item);
        switch (email.getLogo()){
            case "qq":
                ivLogoItem.setImageResource(R.drawable.ic_logo_qq);
                break;
            case "163":
                ivLogoItem.setImageResource(R.drawable.ic_logo_163);
                break;
        }

        helper.setText(R.id.address_item, email.getAddress());
        if (getEmailId() == email.getId()){
            helper.setVisible(R.id.check_item, true);
        }else {
            helper.setVisible(R.id.check_item, false);
        }
    }

    private int getEmailId(){
        if (sharedPreferences == null){
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        }
        return sharedPreferences.getInt("email_id",0);
    }

}
