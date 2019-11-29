package com.example.emailsample.email.display;

import android.app.Activity;
import android.webkit.WebResourceResponse;

import com.example.emailsample.bean.Attachment;
import com.example.emailsample.bean.EmailBean;
import com.example.emailsample.bean.MessageBean;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;

public class DisplayEmailModel implements IDisplayEmailModel {

    private IDisplayEmailPresenter iDisplayEmailPresenter;

    private int messageId;
    private Activity activity;
    private EmailBean email;

    DisplayEmailModel(IDisplayEmailPresenter iDisplayEmailPresenter) {
        this.iDisplayEmailPresenter = iDisplayEmailPresenter;
    }

    @Override
    public void getMessageInformation(int emailId, int messageId, Activity activity) {

        this.messageId = messageId;
        this.activity = activity;

        email = LitePal.find(EmailBean.class, emailId);

        List<MessageBean> messages = LitePal.where("otherId = ?",
                String.valueOf(messageId)).find(MessageBean.class);
        if (messages.size() > 0) {
            iDisplayEmailPresenter.getInformationSuccess(messages.get(0));
        } else {
            iDisplayEmailPresenter.getInformationFail();
        }
    }

    @Override
    public File downloadFile(String url, String fileName) {


        Properties prop = new Properties();
        if (email.getProtocol().equals("imap")) {
            prop.put("mail.store.protocol", email.getProtocol());
            prop.put("mail.imap.host", email.getHost());
            prop.put("mail.imap.ssl.enable", true);
        } else if (email.getProtocol().equals("pop3")) {
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
            folder.open(Folder.READ_WRITE);

            Message[] messages = folder.getMessages();
            if (messages.length > 0) {
                Message message = messages[messageId];
                if (url != null) {
                    List<Attachment> attachments = LitePal.where("contentId = ?", url).find(Attachment.class);
                    if (attachments.size() > 0) {
                        Attachment attachment = attachments.get(0);
                        BodyPart bodyPart = getAttachmentBodyPart(message, url);
                        if (bodyPart != null) {
                            MimeBodyPart mimeBodyPart = (MimeBodyPart) bodyPart;
                            File file = new File(activity.getFilesDir(), attachment.getFileName());
                            mimeBodyPart.saveFile(file);            //保存文件

                            attachment.setIsDownload(true);
                            attachment.update(attachment.getId());      //更新数据库

                            return file;  //返回文件
                        }
                    }
                } else if (fileName != null) {

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
        return null;
    }

    @Override
    public WebResourceResponse getWebResourceResponse(String url) {

        List<Attachment> attachments = LitePal.where("contentId = ?", url).find(Attachment.class);

        if (attachments.size() > 0) {
            Attachment attachment = attachments.get(0);
            String mimeType = attachment.getFileType() + "/" + attachment.getFileFormat();
            File file = new File(activity.getFilesDir(), attachment.getFileName());
            if (file.exists()) {
                //烦人啊，看来下拉刷新还是需要优化啊，优化！下拉刷新太耗时了！
                //attachment.setDownload(true);   //下拉刷新时，重新更新了本地数据库，附件默认都是未下载的，但已下载附件并不会删除，所以此时重新设为已下载
                //attachment.update(attachment.getId());    //更新数据库

                try {
                    return new WebResourceResponse(mimeType, "utf-8", new FileInputStream(file));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {

                Properties prop = new Properties();
                if (email.getProtocol().equals("imap")) {
                    prop.put("mail.store.protocol", email.getProtocol());
                    prop.put("mail.imap.host", email.getHost());
                    prop.put("mail.imap.ssl.enable", true);
                } else if (email.getProtocol().equals("pop3")) {
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
                    if (messages.length > 0) {
                        Message message = messages[messageId];
                        if (url != null) {
                            MimeBodyPart mimeBodyPart = (MimeBodyPart) getAttachmentBodyPart(message, url);
                            if (mimeBodyPart != null) {
                                mimeBodyPart.saveFile(file);   //保存文件

                                attachment.setIsDownload(true);
                                attachment.update(attachment.getId());    //更新数据库

                                return new WebResourceResponse(mimeType, "utf-8", new FileInputStream(file));
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
            }
        }

        return null;
    }

    @Override
    public List<Attachment> getAttachmentInformation() {
        return LitePal.where("messageId = ?", String.valueOf(messageId)).find(Attachment.class);
    }

    private BodyPart getAttachmentBodyPart(Part part, String url) throws MessagingException, IOException {
        if (part.isMimeType("multipart/mixed")) {
            Multipart multipart = (Multipart) part.getContent();
            int counts = multipart.getCount();
            for (int i = 0; i < counts; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                String disposition = bodyPart.getDisposition();
                if (disposition != null && disposition.equals(Part.ATTACHMENT)) {
                    MimeBodyPart mimeBodyPart = (MimeBodyPart) bodyPart;
                    String contentId = mimeBodyPart.getContentID();
                    if (contentId.equals(url)) {
                        return bodyPart;
                    }
                }
            }
        }
        return null;
    }
}
