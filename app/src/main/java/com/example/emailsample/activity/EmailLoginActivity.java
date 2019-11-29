package com.example.emailsample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.emailsample.R;
import com.example.emailsample.dialog.LoginFailDialog;
import com.example.emailsample.dialog.ProgressDialogFragment;
import com.example.emailsample.dialog.ProtocolDialog;
import com.example.emailsample.ui.login.ILoginView;
import com.example.emailsample.ui.login.LoginPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *
 * 邮箱保存账户
 * host: qq、163
 */
public class EmailLoginActivity extends BaseActivity implements ILoginView {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_complete)
    TextView tvComplete;
    @BindView(R.id.et_email_address)
    EditText etEmailAddress;
    @BindView(R.id.iv_clear_address)
    ImageView ivClearAddress;
    @BindView(R.id.et_email_password)
    EditText etEmailPassword;
    @BindView(R.id.iv_show_or_hide_password)
    ImageView ivShowOrHidePassword;
    @BindView(R.id.tv_protocol_text)
    TextView tvProtocolText;
    @BindView(R.id.protocol_login)
    LinearLayout protocolLogin;

    private boolean passwordIsVisible = false;
    private boolean clearIsVisible = true;
    private String host;
    private String smtpHost;
    private String logo;
    private ProgressDialogFragment progressDialog;


    private LoginPresenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        loginPresenter = new LoginPresenter(this);

        // 测试！
//        etEmailAddress.setText("1004260403@qq.com");

        init();
        initAddress();

    }

    private void init() {
        etEmailAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (!getAddress().equals("")) {
                        showClearImage();
                    }
                } else {
                    hideClearImage();
                }
            }
        });

        etEmailAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (getAddress().equals("")) {
                    hideClearImage();
                } else {
                    if (!clearIsVisible) {
                        showClearImage();
                    }
                }
                loginPresenter.checkEmail(getAddress(), getPassword());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etEmailPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loginPresenter.checkEmail(getAddress(), getPassword());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tvComplete.setClickable(false);

    }

    private void showClearImage() {
        clearIsVisible = true;
        ivClearAddress.setVisibility(View.VISIBLE);
    }

    private void hideClearImage() {
        clearIsVisible = false;
        ivClearAddress.setVisibility(View.GONE);
    }


    private void initAddress() {
        Intent intent = getIntent();
        logo = intent.getStringExtra("host");
        switch (logo) {
            case "qq":
                etEmailAddress.setText(R.string.email_qq);
                host = getProtocol().toLowerCase() + ".qq.com";
                smtpHost = "smtp" + ".qq.com";
                break;
            case "163":
                etEmailAddress.setText(R.string.email_163);
                host = getProtocol().toLowerCase() + ".163.com";
                smtpHost = "smtp" + ".163.com";
                break;
            case "126":
                etEmailAddress.setText(R.string.email_126);
                host = getProtocol().toLowerCase() + ".126.com";
                smtpHost = "smtp" + ".126.com";
                break;
        }
        etEmailAddress.setSelection(0, 5);
    }

    @OnClick({R.id.iv_back, R.id.tv_complete, R.id.iv_show_or_hide_password,
            R.id.tv_protocol_text})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                this.finish();
                break;
            case R.id.iv_clear_address:
                clearAddress();
                hideClearImage();
                break;
            case R.id.tv_complete:
                // 登录
                loginEmail();
                break;
            case R.id.iv_show_or_hide_password:
                if (passwordIsVisible) {
                    hidePassword();
                } else {
                    showPassword();
                }
                break;
            case R.id.tv_protocol_text:
                // 显示协议
                showProtocolDialog(getProtocol());
                break;
        }
    }

    private void clearAddress() {
        etEmailAddress.setText("");
    }

    private void hidePassword() {
        passwordIsVisible = false;
        etEmailPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        ivShowOrHidePassword.setImageResource(R.drawable.ic_visibility_off_black_24dp);
    }

    private void showPassword() {
        passwordIsVisible = true;
        etEmailPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        ivShowOrHidePassword.setImageResource(R.drawable.ic_visibility_black_24dp);
    }

    private void showProtocolDialog(String protocol) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        final ProtocolDialog dialog = ProtocolDialog.newInstance(protocol);
        dialog.show(fragmentManager, "ProtocolDialog");
        dialog.setProtocolDialogListener(new ProtocolDialog.protocolDialogListener() {
            @Override
            public void protocolListener(String protocol) {
                tvProtocolText.setText(protocol);
                setHost(protocol);
                dialog.dismiss();
            }
        });
    }

    private void setHost(String protocol) {
        if (protocol.equalsIgnoreCase("POP3")) {
            switch (logo) {
                case "qq":
                    host = "pop.qq.com";
                    break;
                case "163":
                    host = "pop.163.com";
                    break;
                case "126":
                    host = "pop.126.com";
                    break;
            }
        } else if (protocol.equalsIgnoreCase("IMAP")) {
            switch (logo) {
                case "qq":
                    host = "imap.qq.com";
                    break;
                case "163":
                    host = "imap.163.com";
                    break;
                case "126":
                    host = "imap.126.com";
                    break;
            }
        }
    }

    private void loginEmail() {
        loginPresenter.login(getAddress(), getPassword(), getProtocol().toLowerCase(),
                logo, getHost(), smtpHost, this);
    }

    private String getHost() {
        return host;
    }

    private String getAddress() {
        return etEmailAddress.getText().toString();
    }

    private String getPassword() {
        return etEmailPassword.getText().toString();
    }

    private String getProtocol() {
        return tvProtocolText.getText().toString();
    }


    @Override
    public void showCarryOut() {
        tvComplete.setTextColor(getResources().getColor(R.color.colorAccent));
        tvComplete.setClickable(true);
    }

    @Override
    public void hideCarryout() {
        tvComplete.setTextColor(-1979711488);
        tvComplete.setClickable(false);
    }

    @Override
    public void showProgressDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        progressDialog = new ProgressDialogFragment();
        progressDialog.show(fragmentManager,"progressDialog");
        progressDialog.setCancelable(false);
    }

    @Override
    public void hideProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    public void showErrorDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        LoginFailDialog dialog = new LoginFailDialog();
        dialog.show(fragmentManager,"loginFail");
        dialog.setCancelable(false);
    }

    @Override
    public void finishActivity() {
        this.finish();
    }

    @Override
    public void jumpInterface(int email_id) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("email_id",email_id);
        setResult(RESULT_OK);
        startActivity(intent);
    }
}
