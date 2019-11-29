package com.example.emailsample.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.emailsample.R;
import com.example.emailsample.activity.BaseActivity;
import com.example.emailsample.adapter.AccountAdapter;

public class SettingActivity extends BaseActivity {

    private SharedPreferences sharedPreferences;
    private RelativeLayout defaultLayout;
    private TextView defaultAccount;
    private RecyclerView recyclerView;
    private AccountAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }
}
