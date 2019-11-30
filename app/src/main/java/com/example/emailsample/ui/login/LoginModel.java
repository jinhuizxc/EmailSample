package com.example.emailsample.ui.login;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.example.emailsample.bean.EmailBean;
import com.orhanobut.logger.Logger;

import org.litepal.LitePal;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

public class LoginModel implements ILoginModel {

    private ILoginPresenter iLoginPresenter;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    /*@SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what){
                case 1:
                    iLoginPresenter.loginSuccess();
                    break;
                case 0:
                    iLoginPresenter.loginFail();
                    break;
            }
        }
    };*/

    LoginModel(ILoginPresenter iLoginPresenter) {
        this.iLoginPresenter = iLoginPresenter;
    }

    @Override
    public boolean checkAddress(String address) {
        String check = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(address);
        return matcher.matches();
    }

    @Override
    public void login(final String address, final String password, final String protocol, final String logo, final String host,
                      final String smtpHost, final Activity activity) {

        Properties properties = new Properties();
        if (protocol.equals("imap")) {
            properties.put("mail.store.protocol", protocol);
            properties.put("mail.imap.host", host);
            properties.put("mail.imap.ssl.enable", true);
        } else if (protocol.equals("pop3")) {
            properties.put("mail.store.protocol", protocol);
            properties.put("mail.pop3.host", host);
            properties.put("mail.pop3.ssl.enable", true);
        }

        //properties.put("mail.imap.connectiontimeout", 1000);
        Session session = Session.getInstance(properties);
        session.setDebug(true);
        try {
            ToastUtils.showShort("host: " + host + " protocol: " + protocol);
            final Store store = session.getStore(protocol);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        // 连接账号密码:
                        store.connect(address, password);

                        EmailBean emailBean = new EmailBean();
                        emailBean.setAddress(address);
                        emailBean.setPassword(password);
                        emailBean.setProtocol(protocol);
                        emailBean.setLogo(logo);
                        emailBean.setHost(host);
                        emailBean.setSmtpHost(smtpHost);
                        emailBean.save();
                        Logger.d("保存的用户信息: " + emailBean.toString());
                        iLoginPresenter.loginSuccess(emailBean.getId());
                        store.close();

                        if (!getLoginStatus(activity)) {
                            setLoginStatus(true);
                        }
                        isFirstEmail(emailBean.getId());
                        /*android.os.Message message = new android.os.Message();
                        if (store.isConnected()){
                            message.what = 1;
                        }else {
                            message.what = 0;
                        }
                        handler.sendMessage(message);*/
                    } catch (MessagingException e) {
                        iLoginPresenter.loginFail();
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (javax.mail.NoSuchProviderException e) {
            iLoginPresenter.loginFail();
            e.printStackTrace();
        }

    }

    private boolean getLoginStatus(Activity activity) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return sharedPreferences.getBoolean("isLogin", false);
    }

    private void setLoginStatus(boolean bool) {
        editor = sharedPreferences.edit();
        editor.putBoolean("isLogin", bool);
        editor.apply();
    }

    private void isFirstEmail(int emailId) {
        EmailBean emailBean = LitePal.findFirst(EmailBean.class);
        if (emailBean.getId() == emailId) {
            setDefaultEmailId(emailId);
            setEmailId(emailId);
        } else {
            setEmailId(emailId);
        }
    }

    private void setDefaultEmailId(int emailId) {
        editor = sharedPreferences.edit();
        editor.putInt("default_id", emailId);
        editor.apply();
    }

    private void setEmailId(int emailId) {
        editor = sharedPreferences.edit();
        editor.putInt("email_id", emailId);
        editor.apply();
    }

}
