package com.example.emailsample.utils;

public interface DownloadListener {

    void onProgress(int progress);
    void onSuccess();
    void onFailed();

}
