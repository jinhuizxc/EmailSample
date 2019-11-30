package com.example.emailsample.bean;

import org.litepal.crud.LitePalSupport;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 保存的用户信息: EmailBean(id=1, address=1004260403@qq.com,
 * password=jgvlwtwlnovfbfhb, protocol=imap,
 * logo=qq, host=imap.qq.com, smtpHost=smtp.qq.com)
 */
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
