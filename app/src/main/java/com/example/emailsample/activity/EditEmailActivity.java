package com.example.emailsample.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.example.emailsample.R;
import com.example.emailsample.adapter.EditAttachmentAdapter;
import com.example.emailsample.bean.Attachment;
import com.example.emailsample.bean.MessageBean;
import com.example.emailsample.email.edit.EditPresenter;
import com.example.emailsample.email.edit.IEditView;
import com.orhanobut.logger.Logger;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 编辑邮件
 */
public class EditEmailActivity extends BaseActivity implements IEditView {

    @BindView(R.id.back_edit)
    ImageView backEdit;
    @BindView(R.id.attachment_edit)
    ImageView attachmentEdit;
    @BindView(R.id.send_edit)
    ImageView sendEdit;
    @BindView(R.id.recipient_edit)
    EditText recipientEdit;
    @BindView(R.id.clear_recipient_edit)
    ImageView clearRecipientEdit;
    @BindView(R.id.CC_BCC)
    TextView CCBCC;
    @BindView(R.id.CC_edit)
    EditText ccEdit;
    @BindView(R.id.clear_CC_edit)
    ImageView clearCCEdit;
    @BindView(R.id.BCC_edit)
    EditText bccEdit;
    @BindView(R.id.clear_BCC_edit)
    ImageView clearBCCEdit;
    @BindView(R.id.subject_edit)
    EditText subjectEdit;
    @BindView(R.id.recycler_edit)
    RecyclerView recyclerEdit;
    @BindView(R.id.content_edit)
    EditText contentEdit;

    private MessageBean messageBean;
    private int messageId;
    private List<Attachment> attachmentList = new ArrayList<>();
    ;
    private EditPresenter editPresenter;
    private EditAttachmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_email);
        ButterKnife.bind(this);

        init();
        initData();
    }

    private void initData() {
        messageId = getMessageId();
        Logger.d("编辑邮件 ->messageId = " + messageId);
        if (messageId == 0) {
            messageBean = new MessageBean();
            messageBean.setEmailId(getEmailId());
            messageBean.save();             //保存到数据库，为了得到id

            Intent intent = getIntent();
            String mark = intent.getStringExtra("mark");
            Logger.d("mark值为: " + mark);  // mark值为: null
            if (!TextUtils.isEmpty(mark)) {
                switch (mark) {
                    case "reply": {
                        String to = intent.getStringExtra("to");
                        if (to.contains(",")) {
                            to = to.substring(0, to.indexOf(",") - 1);
                        }
                        recipientEdit.setText(to);
                        break;
                    }
                    case "replyAll": {
                        String to = intent.getStringExtra("to");
                        recipientEdit.setText(to);
                        break;
                    }
                    case "forward":
                        int otherId = intent.getIntExtra("otherId", 0);
                        messageBean = getMessage(otherId);
                        subjectEdit.setText(messageBean.getSubject());
                        contentEdit.setText(messageBean.getText());
                        break;
                }
            }
        } else {
            messageBean = LitePal.find(MessageBean.class, messageId);
            recipientEdit.setText(messageBean.getTo());
            ccEdit.setText(messageBean.getCc());
            bccEdit.setText(messageBean.getBcc());
            subjectEdit.setText(messageBean.getSubject());
            contentEdit.setText(messageBean.getPlain());

            if (messageBean.isAttachment()) {
                attachmentList = LitePal.where("messageId = ?", String.valueOf(messageId)).find(Attachment.class);
                showRecyclerView(attachmentList);
            }
        }

        editPresenter = new EditPresenter(this, attachmentList);

        recipientEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    showClear(clearRecipientEdit);
                } else {
                    hideClear(clearRecipientEdit);
                }
                editPresenter.checkEmail(getText(recipientEdit), getText(ccEdit), getText(bccEdit));
            }
        });

        ccEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    showClear(clearCCEdit);
                } else {
                    hideClear(clearCCEdit);
                }
                editPresenter.checkEmail(getText(recipientEdit), getText(ccEdit), getText(bccEdit));
            }
        });

        bccEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    showClear(clearBCCEdit);
                } else {
                    hideClear(clearBCCEdit);
                }
                editPresenter.checkEmail(getText(recipientEdit), getText(ccEdit), getText(bccEdit));
            }
        });

    }

    private void showClear(View view) {
        view.setVisibility(View.VISIBLE);
    }

    private void hideClear(View view) {
        view.setVisibility(View.GONE);
    }


    private MessageBean getMessage(int otherId) {
        List<MessageBean> list = LitePal.where("otherId = ?", String.valueOf(otherId)).find(MessageBean.class);
        return list.get(0);
    }


    private int getEmailId() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getInt("email_id", 0);
    }

    private int getMessageId() {
        Intent intent = getIntent();
        return intent.getIntExtra("messageId", 0);
    }


    public void init() {
        sendEdit.setClickable(false);     //禁止点击
    }

    @Override
    public int getEditMessageId() {
        return messageBean.getId();
    }

    @Override
    public void canSend() {
        sendEdit.setImageResource(R.drawable.mz_tab_ic_send_dark);
        sendEdit.setClickable(true);
    }

    @Override
    public void cannotSend() {
        sendEdit.setImageResource(R.drawable.ic_tb_send_disable);
        sendEdit.setClickable(false);       //禁止点击
    }

    @Override
    public void toastSending() {
        ToastUtils.showShort("正在发送中...");
    }

    @Override
    public void toastSuccess() {
        ToastUtils.showShort("发送成功");
    }

    @Override
    public void toastFailed() {
        ToastUtils.showShort("发送失败");
    }

    @Override
    public void killActivity() {
        finish();
    }

    @OnClick({R.id.back_edit, R.id.attachment_edit, R.id.clear_recipient_edit,
            R.id.clear_BCC_edit, R.id.clear_CC_edit, R.id.send_edit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_edit:
                this.finish();
                break;
            case R.id.attachment_edit:
                hideSoftKeyboard(view);
                showBottomMenu(attachmentEdit);
                break;
            case R.id.clear_recipient_edit:
                clearText(recipientEdit);
                break;
            case R.id.clear_CC_edit:
                clearText(ccEdit);
                break;
            case R.id.clear_BCC_edit:
                clearText(bccEdit);
                break;
            case R.id.send_edit:
                editPresenter.sendMessage(getText(recipientEdit), getText(ccEdit),
                        getText(bccEdit), getText(subjectEdit), getText(contentEdit));
                break;
        }
    }

    private String getText(EditText editText) {
        return editText.getText().toString();
    }


    private void clearText(EditText editText) {
        editText.setText("");
    }


    // 显示底部按钮
    private void showBottomMenu(View v) {
        // 加载PopupWindow的布局
        View view = View.inflate(this, R.layout.menu_attachment, null);
        TextView gallery = view.findViewById(R.id.gallery);
        TextView fileManagement = view.findViewById(R.id.file_management);

        final PopupWindow popWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popWindow.setBackgroundDrawable(new ColorDrawable(0));
        popWindow.setAnimationStyle(R.style.bottom_menu_anim_style);
        popWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        //设置背景半透明
        backgroundAlpha(0.6f);

        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(EditEmailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditEmailActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                } else {
                    // 打开相册
                    openGallery();
                }
                popWindow.dismiss();
            }
        });

        fileManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(EditEmailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditEmailActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    // 打开文件
                    openFileManagement();
                }
                popWindow.dismiss();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);      无用
        startActivityForResult(intent, 0);
    }

    private void openFileManagement() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }

    /**
     * 设置屏幕的背景透明度
     */
    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        getWindow().setAttributes(lp);
    }

    private void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showRecyclerView(final List<Attachment> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerEdit.setLayoutManager(layoutManager);
        adapter = new EditAttachmentAdapter(list);
        //监听回调
        adapter.setEditAttachmentListener(new EditAttachmentAdapter.EditAttachmentListener() {
            @Override
            public void removeItem(int position) {
                //editPresenter.removeAttachment(position);
                Attachment attachment = attachmentList.get(position);
                attachment = LitePal.find(Attachment.class, attachment.getId());
                if (attachment != null) {
                    LitePal.delete(Attachment.class, attachment.getId());
                }
                attachmentList.remove(position);
                adapter.notifyItemRemoved(position);
            }
        });
        recyclerEdit.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
            case 1:
                if (resultCode == RESULT_OK) {
                    Attachment attachment = editPresenter.getEditAttachment(data);
                    if (attachmentList.size() == 0) {
                        attachmentList.add(attachment);
                        showRecyclerView(attachmentList);
                    } else {
                        attachmentList.add(attachment);
                        adapter.notifyItemInserted(adapter.getItemCount());
                    }
                }
                break;
            default:
        }
    }

}
