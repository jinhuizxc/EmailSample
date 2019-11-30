package com.example.emailsample.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.emailsample.R;
import com.example.emailsample.bean.Attachment;
import com.example.emailsample.bean.EmailBean;
import com.example.emailsample.bean.MessageBean;

import org.litepal.LitePal;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditAccountActivity extends BaseActivity {

    @BindView(R.id.back_edit_account)
    ImageView backEditAccount;
    @BindView(R.id.title_edit_account)
    TextView titleEditAccount;
    @BindView(R.id.bar_edit_account)
    LinearLayout barEditAccount;
    @BindView(R.id.delete_edit_account)
    TextView deleteEditAccount;
    private int emailId;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_account);
        ButterKnife.bind(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Intent intent = getIntent();
        String account = intent.getStringExtra("account");
        emailId = intent.getIntExtra("emailId", 0);
        titleEditAccount.setText(account);


    }

    @OnClick({R.id.back_edit_account, R.id.delete_edit_account})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_edit_account:
                this.finish();
                break;
            case R.id.delete_edit_account:
                showDeleteMenu(view);
                break;
        }
    }

    //显示删除弹窗
    private void showDeleteMenu(View v) {
        // 加载PopupWindow的布局
        View view = View.inflate(this, R.layout.menu_delete, null);
        TextView deleteMenu = view.findViewById(R.id.delete_menu_main);
        TextView cancelMenu = view.findViewById(R.id.cancel_menu_main);
        deleteMenu.setText("删除账号");

        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        popupWindow.setAnimationStyle(R.style.bottom_menu_anim_style);
        popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        //设置背景半透明
        backgroundAlpha(0.6f);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });

        deleteMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //删除该账号的所有消息
                LitePal.deleteAll(MessageBean.class, "emailId = ?", String.valueOf(emailId));
                List<Attachment> attachmentList = LitePal.where("emailId = ?"
                        , String.valueOf(emailId)).find(Attachment.class);

                // 删除文件
                for (Attachment attachment : attachmentList) {
                    if (attachment.getIsDownload()) {
                        deleteFile(attachment.getFileName());
                    }
                    LitePal.delete(Attachment.class, attachment.getId());
                }
                //删除该账号所有的附件
                LitePal.delete(EmailBean.class, emailId);      //删除该账号
                List<EmailBean> list = LitePal.findAll(EmailBean.class);
                if (list.size() > 0) {
                    if (getDefaultId() == getEmailId()) {       //登录的账号正好是默认账号
                        if (getDefaultId() == emailId) {           //删除的是默认账号
                            setDefaultId(list.get(0).getId());      //将数据库中第一个设置为默认账号
                            setEmailId(list.get(0).getId());        //同时将数据库中第一个设置为登录账号
                            setResult(4);          //返回4，说明登录账号被删了
                        } else {
                            setResult(3);
                        }
                    } else {                               //默认账号和登录账号不是同一个
                        if (getDefaultId() == emailId) {          //删除的是默认账号
                            setDefaultId(list.get(0).getId());              //将数据库中第一个设置为默认账号
                            setResult(RESULT_OK);      //返回ok,便是默认账号被删了
                        } else if (getEmailId() == emailId) {          //说明删除的是此刻正登录的账号，删除后需要更新页面
                            setEmailId(getDefaultId());         //将登录账号设为默认账号
                            setResult(4);
                        } else {
                            setResult(3);        //返回3，说明删除的既不是登录账号，也不是默认账号
                        }
                    }
                } else {
                    setResult(5);     //说明凉了
                    setLoginStatus();
                    Intent intent = new Intent(EditAccountActivity.this, AddAccountActivity.class);
                    startActivity(intent);
                }
                finish();
                popupWindow.dismiss();

            }
        });

        cancelMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });


    }

    /**
     * 设置屏幕的背景透明度
     */
    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        getWindow().setAttributes(lp);
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

    private void setLoginStatus() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLogin", false);
        editor.apply();
    }


}
