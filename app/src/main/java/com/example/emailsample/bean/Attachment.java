package com.example.emailsample.bean;

import org.litepal.crud.LitePalSupport;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Attachment extends LitePalSupport {

    private int id;
    private boolean isLocal;
    private String fileType;
    private String fileName;
    private String fileFormat;
    private String filePath;
    private long size;
    private String fileSize;
    private Boolean isDownload;
    private String contentId;
    private int emailId;
    private int messageId;
}
