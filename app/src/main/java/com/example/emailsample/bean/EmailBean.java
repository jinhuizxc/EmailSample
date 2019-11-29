package com.example.emailsample.bean;

import org.litepal.crud.LitePalSupport;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class EmailBean extends LitePalSupport {

    private int id;
    private String address;
    private String password;
    private String protocol;
    private String logo;
    private String host;
    private String smtpHost;

}
