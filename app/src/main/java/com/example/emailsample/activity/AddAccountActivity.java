package com.example.emailsample.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import com.example.emailsample.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddAccountActivity extends BaseActivity {

    @BindView(R.id.QQ_text)
    TextView QQText;
    @BindView(R.id.text_163)
    TextView text163;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        int from = i.getIntExtra("from", 0);
        if (from == 0) {
            if (isLogin()) {
                Intent intent = new Intent(AddAccountActivity.this, MainActivity.class);
                intent.putExtra("email_id", getDefaultId());
                setEmailId(getDefaultId());
                startActivity(intent);
                finish();
            }
        }
        setContentView(R.layout.activity_add_account);
        ButterKnife.bind(this);

    }

    private boolean isLogin() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getBoolean("isLogin", false);
    }

    private int getDefaultId() {
        return sharedPreferences.getInt("default_id", 0);
    }

    private void setEmailId(int emailId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("email_id", emailId);
        editor.apply();
    }


    @OnClick({R.id.QQ_text, R.id.text_163})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.QQ_text:
                Intent intent = new Intent(this, EmailLoginActivity.class);
                intent.putExtra("host","qq");
                startActivityForResult(intent,1);
                break;
            case R.id.text_163:
                Intent i = new Intent(this, EmailLoginActivity.class);
                i.putExtra("host","163");
                startActivityForResult(i,1);
                break;
        }
    }
}
