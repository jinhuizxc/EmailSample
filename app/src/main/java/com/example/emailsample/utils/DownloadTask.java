package com.example.emailsample.utils;

import android.os.AsyncTask;

import com.example.emailsample.bean.EmailBean;

import org.litepal.LitePal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeUtility;

public class DownloadTask extends AsyncTask<String,Integer,Boolean> {

    private DownloadListener listener;

    public DownloadTask(DownloadListener listener){
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(String... params) {       //传五个参数，第一个emailId,第二个messageId,
        // 第三个文件路径，第四个fileName，第五个文件大小
        int emailId = Integer.parseInt(params[0]);
        int messageId = Integer.parseInt(params[1]);
        String filepath = params[2];
        String fileName = params[3];
        long fileLength = Long.parseLong(params[4]);

        EmailBean email = LitePal.find(EmailBean.class, emailId);

        Properties prop = new Properties();
        if (email.getProtocol().equals("imap")){
            prop.put("mail.store.protocol", email.getProtocol());
            prop.put("mail.imap.host", email.getHost());
            prop.put("mail.imap.ssl.enable", true);
        }else if (email.getProtocol().equals("pop3")){
            prop.put("mail.store.protocol", email.getProtocol());
            prop.put("mail.pop3.host", email.getHost());
            prop.put("mail.pop3.ssl.enable", true);
        }

        Session session = Session.getInstance(prop);
        session.setDebug(true);
        Store store = null;
        Folder folder = null;

        try {

            store = session.getStore(email.getProtocol());
            store.connect(email.getAddress(), email.getPassword());

            folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            Message[] messages = folder.getMessages();
            if (messages.length > 0){
                Message message = messages[messageId];
                BodyPart bodyPart = getAttachmentBodyPart(message,fileName);
                if (bodyPart != null){

                    BufferedInputStream bufferedInputStream = null;
                    BufferedOutputStream bufferedOutputStream = null;
                    try {
                        long downloadLength = 0;             //文件下载长度
                        File file = new File(filepath,fileName);
                        InputStream inputStream = bodyPart.getInputStream();
                        bufferedInputStream = new BufferedInputStream(inputStream);
                        bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
                        byte[] buf = new byte[8192];
                        int len;
                        while ((len = bufferedInputStream.read(buf)) > 0){
                            bufferedOutputStream.write(buf, 0, len);
                            bufferedOutputStream.flush();
                            downloadLength += len;
                            int progress = (int) ((int) downloadLength*100/fileLength);
                            publishProgress(progress);
                        }
                        return true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (bufferedInputStream != null) {
                                bufferedInputStream.close();
                            }
                            if (bufferedOutputStream != null) {
                                bufferedOutputStream.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }

        } catch (javax.mail.NoSuchProviderException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (folder != null) {
                    folder.close();
                }
                if (store != null) {
                    store.close();
                }
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        listener.onProgress(progress);
    }

    @Override
    protected void onPostExecute(Boolean status) {
        if (status){
            listener.onSuccess();
        }else{
            listener.onFailed();
        }
    }

    private BodyPart getAttachmentBodyPart(Part part, String fileName) throws MessagingException, IOException {
        if (part.isMimeType("multipart/mixed")){
            Multipart multipart = (Multipart) part.getContent();
            int counts = multipart.getCount();
            for (int i=0;i<counts;i++){
                BodyPart bodyPart = multipart.getBodyPart(i);
                String disposition = bodyPart.getDisposition();
                if (disposition != null && disposition.equals(Part.ATTACHMENT)){
                    String name = bodyPart.getFileName();
                    if (name.startsWith("=?")){
                        name = MimeUtility.decodeText(name);
                    }
                    if (name != null && name.equals(fileName)){
                        return bodyPart;
                    }
                }
            }
        }
        return null;
    }

}
