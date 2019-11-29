package com.example.emailsample.email.edit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.widget.Toast;

import com.example.emailsample.bean.Attachment;
import com.example.emailsample.bean.EmailBean;
import com.example.emailsample.bean.MessageBean;
import com.orhanobut.logger.Logger;

import org.litepal.LitePal;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EditModel implements IEditModel {

    private IEditPresenter iEditPresenter;
    private Activity activity;
    private List<Attachment> list;

    EditModel(IEditPresenter iEditPresenter, Activity activity, List<Attachment> list) {
        this.iEditPresenter = iEditPresenter;
        this.activity = activity;
        this.list = list;
    }

    @Override
    public boolean checkAddress(String address) {
        String check = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(address);
        return matcher.matches();
    }

    @Override
    public Attachment getInformationFromIntent(Intent intent, int editMessageId) {

        Attachment attachment = new Attachment();
        attachment.setLocal(true);
        attachment.setEmailId(getEmailId());
        attachment.setMessageId(editMessageId);

        Uri uri = intent.getData();
        String path = null;
        if (uri != null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                path = getPath(activity, uri);
                Toast.makeText(activity, path, Toast.LENGTH_SHORT).show();
            } else {//4.4以下下系统调用方法
                path = getRealPathFromURI(uri);
                Toast.makeText(activity, path + "222222", Toast.LENGTH_SHORT).show();
            }
            if (path != null) {
                attachment.setFilePath(path);
                File file = new File(path);
                String fileName = file.getName();

                attachment.setFileName(file.getName());
                attachment.setSize(file.length());
                attachment.setFileSize(Formatter.formatFileSize(activity, file.length()));

                String mimeType = activity.getContentResolver().getType(uri);
                if (mimeType != null) {
                    String type = mimeType.substring(0, mimeType.indexOf("/"));
                    String format = mimeType.substring(mimeType.indexOf("/") + 1, mimeType.length());
                    Toast.makeText(activity, type, Toast.LENGTH_SHORT).show();
                    attachment.setFileType(type);
                    attachment.setFileFormat(format);
                } else {
                    String fileFormat = fileName.substring(fileName.indexOf(".") + 1);
                    if (fileFormat.equalsIgnoreCase("jpg")) {
                        attachment.setFileFormat("jpeg");
                    } else {
                        attachment.setFileFormat(fileFormat);
                    }

                    switch (fileFormat) {
                        case "gif":
                        case "png":
                        case "jpeg":
                            attachment.setFileType("image");
                            break;
                        case "mp3":
                            attachment.setFileType("audio");
                            break;
                        case "mp4":
                            attachment.setFileType("video");
                            break;
                        case "pdf":
                            attachment.setFileType("pdf");
                            break;
                        case "xls":
                        case "xlsx":
                            attachment.setFileType("xls");
                            break;
                        case "doc":
                        case "docx":
                            attachment.setFileType("doc");
                            break;
                        case "txt":
                            attachment.setFileType("txt");
                            break;
                        case "zip":
                            attachment.setFileType("zip");
                            break;
                        case "rar":
                            attachment.setFileType("rar");
                            break;
                        case "apk":
                            attachment.setFileType("application");
                            break;
                    }
                }
            }
        }
        //list.add(attachment);
        return attachment;
    }

    @Override
    public void removeAttachment(int position) {
        list.remove(position);
    }

    @Override
    public void sendMessage(String recipient, String cc, String bcc, String subject, String content, final long messageId) {
        final MessageBean messageBean = new MessageBean();
        messageBean.setLocal(true);
        messageBean.setSend(false);
        messageBean.setDate(getDate());
        messageBean.setRead(true);
        messageBean.setTo(recipient);
        messageBean.setCc(cc);
        messageBean.setBcc(bcc);
        messageBean.setSubject(subject);
        messageBean.setPlain(content);
        messageBean.setText(content);

        if (list.size() > 0) {
            long size = 0;
            for (Attachment attachment : list) {
                attachment.save();
                size += attachment.getSize();
            }
            messageBean.setAttachment(true);
            messageBean.setAttachmentNum(list.size());
            messageBean.setAttachmentSize(Formatter.formatFileSize(activity, size));
        } else {
            messageBean.setAttachment(false);
        }

        EmailBean email = getEmail();

        // com.sun.mail.smtp.SMTPAddressFailedException: 501 Bad address syntax
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");     //协议
        props.put("mail.smtp.host", email.getSmtpHost()); // smtp 服务器地址
        Logger.d("EmailModel测试 ------>smtp服务器地址: " + email.getSmtpHost());
        //props.put("mail.smtp.port", 465);             // smtp 服务器端口
        props.put("mail.smtp.auth", true);            // 服务器需要认证
        props.put("mail.smtp.ssl.enable", true);      // 使用 SSL 套接层

        Session session = Session.getInstance(props);
        session.setDebug(true);

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email.getAddress()));
            if (!recipient.equals("")) {                //设置收件人
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            }
            if (!cc.equals("")) {             //设置抄送人
                message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));
            }
            if (!bcc.equals("")) {            //设置密送人
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(bcc));
            }
            message.setSentDate(new Date());
            message.setSubject(subject);
            if (list.size() == 0) {          // 说明没有附件，直接设置文本
                message.setText(content);
            } else {                        // 有附件，处理附件
                MimeMultipart multipart = new MimeMultipart("mixed");
                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                mimeBodyPart.setText(content);
                multipart.addBodyPart(mimeBodyPart);
                for (Attachment attachment : list) {
                    MimeBodyPart bodyPart = new MimeBodyPart();
                    bodyPart.setFileName(attachment.getFileName());
                    FileDataSource fileDataSource = new FileDataSource(attachment.getFilePath());
                    bodyPart.setDataHandler(new DataHandler(fileDataSource));
                    multipart.addBodyPart(bodyPart);
                }
                message.setContent(multipart);
            }
            message.saveChanges();

            Transport transport = session.getTransport();
            transport.connect(email.getAddress(), email.getPassword());
            Logger.d("EmailModel测试 :" + email.getAddress() + "<---->" + email.getPassword());
            transport.addTransportListener(new TransportListener() {
                @Override
                public void messageDelivered(TransportEvent e) {
                    messageBean.setSend(true);
                    messageBean.update(messageId);
                    activity.setResult(Activity.RESULT_OK);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iEditPresenter.sendSuccess();
                        }
                    });
                }

                @Override
                public void messageNotDelivered(TransportEvent e) {
                    messageBean.setSend(false);
                    messageBean.update(messageId);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iEditPresenter.sendFailed();
                        }
                    });
                }

                @Override
                public void messagePartiallyDelivered(TransportEvent e) {
                }
            });
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (MessagingException e) {
            messageBean.setSend(false);
            messageBean.update(messageId);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    iEditPresenter.sendFailed();
                }
            });
            e.printStackTrace();
        }
    }

    private int getEmailId() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return sharedPreferences.getInt("email_id", 0);
    }

    private EmailBean getEmail() {
        return LitePal.find(EmailBean.class, getEmailId());
    }

    /**
     * 获得邮件发送日期
     */
    @SuppressLint("SimpleDateFormat")
    private String getDate() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm");
        return format.format(date);
    }

    private String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.getContentResolver().query(contentUri, proj, null, null, null);
        if (null != cursor && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }

    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    @SuppressLint("NewApi")
    private String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private String getDataColumn(Context context, Uri uri, String selection,
                                 String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}
