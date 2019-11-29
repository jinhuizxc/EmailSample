package com.example.emailsample.bean;

import org.litepal.crud.LitePalSupport;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MessageBean extends LitePalSupport {

    private int id;
    private boolean isLocal;
    private boolean isSend;
    private boolean isRead;
    private boolean isDelete;
    private String subject;
    private String from;
    private String replyTo;
    private String to;
    private String cc;
    private String bcc;
    private String date;
    private String plain;
    private String plainInstead;
    private String text;
    private boolean isAttachment;
    private int attachmentNum;
    private String attachmentSize;
    private int emailId;
    private int otherId;



}
