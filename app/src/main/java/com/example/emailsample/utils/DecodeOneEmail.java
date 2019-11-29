package com.example.emailsample.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.format.Formatter;

import com.example.emailsample.bean.Attachment;
import com.example.emailsample.bean.MessageBean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

public class DecodeOneEmail {

    private Activity activity;
    private MimeMessage mimeMessage;
    private int emailId;
    private int otherId;
    private MessageBean bean;
    private String saveAttachPath = ""; // 附件下载后的存放目录
    private String dateFormat = "yy-MM-dd HH:mm"; // 默认的日前显示格式
    private boolean isAttachment = false;
    private int attachmentNum = 0;
    private long allAttachmentSize = 0;
    private String plain;
    private String regFormat = "\\s*|\t|\r|\n";
    private String regTag = "<[^>]*>";

    public DecodeOneEmail(MimeMessage mimeMessage,Activity activity,int otherId,int emailId) {
        this.mimeMessage = mimeMessage;
        this.activity = activity;
        this.emailId = emailId;
        this.otherId = otherId;
        bean = new MessageBean();
        bean.setOtherId(otherId);
        //bean.save();   //保存到数据到数据库，为了下面得到message_id
    }

    /**
     * 判断邮件是否已读
     */
    private boolean isRead() throws MessagingException {
        Flags flags = mimeMessage.getFlags();
        return flags.contains(Flags.Flag.SEEN);
    }

    //判断是否是新邮件
    public static boolean isNew(Message message) throws MessagingException {
        Flags flags = message.getFlags();
        return flags.contains(Flags.Flag.RECENT);
    }

    /**
     * 获得发件人
     */
    public String getFrom() throws Exception {
        InternetAddress address = (InternetAddress) mimeMessage.getFrom()[0];
        String personal = address.getPersonal();
        if (personal.startsWith("=?")){
            personal = MimeUtility.decodeText(personal);
        }
        return personal;
    }

    /**
     * 获得邮件主题
     */
    private String getSubject() throws MessagingException {
        return mimeMessage.getSubject();
    }

    /**
     * 获得邮件发送日期
     */
    @SuppressLint("SimpleDateFormat")
    private String getSentDate() throws Exception {
        Date sentDate = mimeMessage.getSentDate();
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        return format.format(sentDate);
    }

    //获得收件人
    private String getReplyTo() throws MessagingException {
        InternetAddress[] address = (InternetAddress[]) mimeMessage.getReplyTo();
        StringBuilder stringBuilder = new StringBuilder();
        if (address != null){
            for (InternetAddress addr:address){
                stringBuilder.append(addr.getAddress()).append(",");
            }
            stringBuilder.deleteCharAt(stringBuilder.length()-1);
        }
        return stringBuilder.toString();
    }

    //获得收件人
    public String getTo() throws MessagingException {
        InternetAddress[] address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.TO);
        StringBuilder stringBuilder = new StringBuilder();
        if (address != null){
            for (InternetAddress addr:address){
                stringBuilder.append(addr.getAddress()).append(",");
            }
            stringBuilder.deleteCharAt(stringBuilder.length()-1);
        }
        return stringBuilder.toString();
    }

    //获得抄送人
    public String getCC() throws MessagingException {
        InternetAddress[] address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.CC);
        StringBuilder stringBuilder = new StringBuilder();
        if (address != null){
            for (InternetAddress addr:address){
                stringBuilder.append(addr.getAddress()).append(",");
            }
            stringBuilder.deleteCharAt(stringBuilder.length()-1);
        }
        return stringBuilder.toString();
    }

    //获得密送人
    public String getBCC() throws MessagingException {
        InternetAddress[] address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.BCC);
        StringBuilder stringBuilder = new StringBuilder();
        if (address != null){
            for (InternetAddress addr:address){
                stringBuilder.append(addr.getAddress()).append(",");
            }
            stringBuilder.deleteCharAt(stringBuilder.length()-1);
        }
        return stringBuilder.toString();
    }

    //text,image,audio,video,application,multipart,message
    //text/plain,text/html,text/css,text/xml
    //multipart/mixed,multipart/alternative,multipart/related
    private String getEmailContent(Part part) throws MessagingException, IOException {
        String content = null;
        if (part.isMimeType("text/plain")){
            content = (String) part.getContent();
        }else if (part.isMimeType("text/html")){
            content = (String) part.getContent();
        }else if (part.isMimeType("multipart/mixed")){
            Multipart multipart = (Multipart) part.getContent();
            int counts = multipart.getCount();
            for (int i=0;i<counts;i++){
                BodyPart bodyPart = multipart.getBodyPart(i);
                String disposition = bodyPart.getDisposition();
                if (disposition != null && disposition.equals(Part.ATTACHMENT)){
                    attachmentNum++;
                    if (!isAttachment){
                        isAttachment = true;
                    }
                    getAttachmentInformation((MimeBodyPart) bodyPart);
                }else{
                    content = getEmailContent(bodyPart);
                }
            }
        }else if (part.isMimeType("multipart/alternative")){
            Multipart multipart = (Multipart) part.getContent();
            int counts = multipart.getCount();
            for (int i=0;i<counts;i++){
                BodyPart bodyPart = multipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/html")){
                    content = getEmailContent(bodyPart);
                }
                if (bodyPart.isMimeType("text/plain")){
                    plain = getEmailContent(bodyPart);
                }
            }
        }else if (part.isMimeType("multipart/related")){
            content = (String) part.getContent();
        }
        return content;
    }

    //判断是否有附件
    private boolean isAttachment(){
        return isAttachment;
    }

    //获取附件信息
    private void getAttachmentInformation(MimeBodyPart bodyPart) throws MessagingException, IOException {
        Attachment attachment = new Attachment();
        attachment.setLocal(false);
        String fileName = bodyPart.getFileName();
        if (fileName.startsWith("=?")){
            fileName = MimeUtility.decodeText(fileName);
        }
        attachment.setFileName(fileName);
        //String contentType = bodyPart.getContentType();
        //contentType = contentType.substring(0,contentType.indexOf(";"));
        /*if (contentType.equalsIgnoreCase("application/octet-stream")){

            String fileFormat = fileName.substring(fileName.indexOf(".")+1);
            if (fileFormat.equalsIgnoreCase("jpg")){
                attachment.setFileFormat("jpeg");
            }else {
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
                case "zip":
                    attachment.setFileType("zip");
                    break;
                case "apk":
                    attachment.setFileType("application");
                    break;
            }
        }else{
            attachment.setFileType(contentType.substring(0,contentType.lastIndexOf("/")).toLowerCase());
            attachment.setFileFormat(contentType.substring(contentType.lastIndexOf("/")+1).toLowerCase());
        }*/    //mime方法真是不靠谱，还是后缀名比较靠谱点

        String fileFormat = fileName.substring(fileName.indexOf(".")+1);
        if (fileFormat.equalsIgnoreCase("jpg")){
            attachment.setFileFormat("jpeg");
        }else {
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

        long size = getAttachmentSize(bodyPart);
        attachment.setSize(size);
        attachment.setFileSize(Formatter.formatFileSize(activity,size));

        attachment.setContentId(bodyPart.getContentID());

        attachment.setIsDownload(isDownload(fileName));

        attachment.setEmailId(emailId);
        attachment.setMessageId(otherId);
        attachment.save();
    }


    private boolean isDownload(String fileName){
        File file = new File(activity.getFilesDir(),fileName);
        return file.exists();
    }

    //下载附件，更简单的方法
    private void downloadAttachment(MimeBodyPart mimeBodyPart){
        try {
            String fileName = mimeBodyPart.getFileName();
            File file = new File(activity.getFilesDir(),fileName);
            mimeBodyPart.saveFile(file);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //保存附件
    private void saveAttachment(BodyPart bodyPart) {
        BufferedInputStream bufferedInputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        try {
            String fileName = bodyPart.getFileName();
            File file = new File(activity.getFilesDir(),fileName);
            bufferedInputStream = new BufferedInputStream(bodyPart.getInputStream());
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
            int c ;
            while ((c=bufferedInputStream.read())!=1){
                bufferedOutputStream.write(c);
            }
            bufferedOutputStream.close();
            bufferedInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
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

    //获取附件数量
    private int getAttachmentNum(){
        return  attachmentNum;
    }

    //获取单个附件的大小
    private long getAttachmentSize(BodyPart bodyPart) throws MessagingException {
        long size = bodyPart.getSize();
        /*int c;
        InputStream inputStream = null;
        try {
            inputStream = bodyPart.getInputStream();
            byte[] buffer = new byte[1024];
            while ((c=inputStream.read(buffer)) != -1){
                size += c;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        allAttachmentSize += size;
        return size;
    }

    //获取附件总大小
    private String getAllAttachmentSize(){
        return Formatter.formatFileSize(activity,allAttachmentSize);
    }

    public MessageBean getMessageBean(){
        try {
            bean.setRead(isRead());
            bean.setLocal(false);
            bean.setSubject(getSubject());
            bean.setDate(getSentDate());
            bean.setFrom(getFrom());
            bean.setReplyTo(getReplyTo());
            bean.setTo(getTo());
            bean.setCc(getCC());
            bean.setBcc(getBCC());
            bean.setText(getEmailContent(mimeMessage));
            bean.setPlain(plain);
            String replaceAll = bean.getText().replaceAll(regFormat,"").replaceAll(regTag,"");
            bean.setPlainInstead(replaceAll);
            bean.setAttachment(isAttachment());
            if (isAttachment()){
                bean.setAttachmentNum(getAttachmentNum());
                bean.setAttachmentSize(getAllAttachmentSize());
            }
            bean.setEmailId(emailId);
            bean.save();   //再次保存，更新数据
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * 【设置附件存放路径】
     */
    public void setAttachPath(String attachpath) {
        this.saveAttachPath = attachpath;
    }

    /**
     * 【设置日期显示格式】
     */
    public void setDateFormat(String format) throws Exception {
        this.dateFormat = format;
    }

    /**
     * 【获得附件存放路径】
     */
    public String getAttachPath() {
        return saveAttachPath;
    }


}
