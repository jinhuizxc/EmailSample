package com.example.emailsample.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.emailsample.MyDecoration;
import com.example.emailsample.R;
import com.example.emailsample.adapter.AccountAdapter;
import com.example.emailsample.bean.EmailBean;
import com.example.emailsample.dialog.DefaultDialog;
import com.orhanobut.logger.Logger;

import org.litepal.LitePal;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {

    @BindView(R.id.back_setting)
    ImageView backSetting;
    @BindView(R.id.default_account_setting)
    TextView defaultAccountSetting;
    @BindView(R.id.default_setting)
    RelativeLayout defaultSetting;
    @BindView(R.id.recycler_setting)
    RecyclerView recyclerSetting;
    @BindView(R.id.add_setting)
    TextView addSetting;

    private SharedPreferences sharedPreferences;
    private AccountAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerSetting.setLayoutManager(layoutManager);
        recyclerSetting.addItemDecoration(new MyDecoration(this, RecyclerView.VERTICAL));

        List<EmailBean> list = LitePal.findAll(EmailBean.class);
        Logger.d("设置页面: " + list.toString());
        if (list.size() == 1) {
            defaultAccountSetting.setVisibility(View.GONE);
        } else {
            EmailBean email = LitePal.find(EmailBean.class, getDefaultId());
            defaultAccountSetting.setText(email.getAddress());
        }

        adapter = new AccountAdapter(R.layout.account_item, list);
        recyclerSetting.setAdapter(adapter);
    }

    private int getDefaultId() {
        return sharedPreferences.getInt("default_id", 0);
    }

    private void setDefaultId(int defaultId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("default_id", defaultId);
        editor.apply();
    }

    private int getEmailId() {
        return sharedPreferences.getInt("email_id", 0);
    }

    private void setEmailId(int emailId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("email_id", emailId);
        editor.apply();
    }

    @OnClick({R.id.back_setting, R.id.add_setting, R.id.default_setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_setting:
                this.finish();
                break;
            case R.id.default_setting:
                showDefaultDialog();
                break;
            case R.id.add_setting:
                Intent intent = new Intent(this, AddAccountActivity.class);
                intent.putExtra("from", 1);
                startActivityForResult(intent, 1);
                break;
        }
    }

    private void showDefaultDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        final DefaultDialog dialog = new DefaultDialog();
        dialog.show(fragmentManager, "DefaultDialog");
        dialog.setDialogListener(new DefaultDialog.OnDialogListener() {
            @Override
            public void setDefault(EmailBean email) {
                defaultAccountSetting.setText(email.getAddress());
                setDefaultId(email.getId());
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK || resultCode == 4 || resultCode == 3) {
                    //ok默认账号，4登录账号，3既不是登录账号，也不是默认账号
                    //这一步说明删了一个账号，保险起见，刷新
                    List<EmailBean> list = LitePal.findAll(EmailBean.class);
                    if (list.size() == 1) {
                        defaultAccountSetting.setVisibility(View.GONE);
                    } else {
                        EmailBean email = LitePal.find(EmailBean.class, getDefaultId());
                        defaultAccountSetting.setText(email.getAddress());
                    }

                    adapter = new AccountAdapter(R.layout.account_item, list);
                    recyclerSetting.setAdapter(adapter);

                    if (resultCode == 4) {
                        //登录账号被删，
                        setResult(RESULT_OK);        //返回ok,让主页面刷新
                    } else {
                        setResult(4);       //让address列表更新
                    }

                } else if (resultCode == 5) {
                    //说明凉了，去死吧
                    setResult(5);         //让上一个页面也凉吧
                    finish();
                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {      //成功添加了一个账号
                    setResult(5);    //关闭主页面
                    finish();
                }
                break;
        }
    }
}
