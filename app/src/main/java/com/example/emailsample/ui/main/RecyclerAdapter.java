package com.example.emailsample.ui.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.emailsample.R;
import com.example.emailsample.activity.DisplayEmailActivity;
import com.example.emailsample.activity.EditEmailActivity;
import com.example.emailsample.bean.EmailBean;
import com.example.emailsample.bean.MessageBean;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<MessageBean> list;
    private String page;
    private List<Boolean> listCheck;
    private int size = 0;
    private boolean isShow = false;
    private itemLongClickListener listener;

    public RecyclerAdapter(List<MessageBean> list) {
        this.list = list;
        listCheck = new ArrayList<>();
        page = "";          //默认空，也就是收件箱页面
    }

    public List<MessageBean> getList() {
        return list;
    }

    public void setList(List<MessageBean> list) {
        this.list.clear();         //先清除
        this.list = list;
        notifyDataSetChanged();
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setShow(boolean isShow) {
        this.isShow = isShow;
        notifyDataSetChanged();
    }

    private void initListCheck() {
        size = 0;
        listCheck.clear();
        for (int i = 0; i < list.size(); i++) {
            listCheck.add(false);
        }
        //notifyDataSetChanged();
    }

    public List<Boolean> getListCheck() {
        return listCheck;
    }

    public void setListCheck(List<Boolean> listCheck) {
        this.listCheck = listCheck;
        notifyDataSetChanged();
    }

    public void setItemLongClickListener(itemLongClickListener itemLongClickListener) {
        listener = itemLongClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.recycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShow) {
                    int position = holder.getAdapterPosition();
                    if (listCheck.get(position)) {
                        listCheck.set(position, false);
                        size--;
                    } else {
                        listCheck.set(position, true);
                        size++;
                    }
                    notifyItemChanged(position);
                    if (listener != null) {
                        listener.onClick(page, size);
                    }
                } else {
                    switch (page) {
                        case "收件箱":
                        case "未读箱":
                        case "已删除邮件": {
                            final int position = holder.getAdapterPosition();
                            final MessageBean bean = list.get(position);
                            if (!bean.isRead()) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int i = bean.getOtherId();
                                        setSeen(i);        //服务器上设为已读
                                    }
                                }).start();
                                bean.setRead(true);
                                //notifyItemChanged(position);
                                bean.update(bean.getId());      //本地数据库上设为已读
                                holder.isRead.setVisibility(View.GONE);    //界面上设为已读
                            }
                            // 跳转邮箱页面
                            Intent intent = new Intent(context, DisplayEmailActivity.class);
                            intent.putExtra("messageId", bean.getOtherId());
                            context.startActivity(intent);
                            break;
                        }
                        case "草稿箱": {
                            int position = holder.getAdapterPosition();
                            MessageBean bean = list.get(position);
                            Intent intent = new Intent(context, EditEmailActivity.class);
                            intent.putExtra("messageId", bean.getId());
                            Activity activity = (Activity) context;
                            activity.startActivityForResult(intent, 0);
                            activity.overridePendingTransition(R.anim.bottom_in, R.anim.scale_out);
                            break;
                        }
                        case "已发送": {
                            int position = holder.getAdapterPosition();
                            MessageBean bean = list.get(position);
                            Intent intent = new Intent(context, DisplayEmailActivity.class);
                            intent.putExtra("messageId", bean.getId());
                            context.startActivity(intent);
                            break;
                        }
                    }
                }
            }
        });
        holder.recycler.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                initListCheck();
                int position = holder.getAdapterPosition();
                //holder.checkBox.setChecked(true);
                listCheck.set(position, true);
                size++;
                isShow = true;
                notifyDataSetChanged();

                if (listener != null) {
                    listener.onClick(page, size);
                    listener.onLongClick(page);
                }
                return true;
            }
        });
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (listCheck.get(position)) {
                    listCheck.set(position, false);
                    size--;
                } else {
                    listCheck.set(position, true);
                    size++;
                }
                notifyItemChanged(position);
                if (listener != null) {
                    listener.onClick(page, size);
                }
            }
        });

        //holder.setIsRecyclable(false);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageBean messageBean = list.get(position);
        if (holder instanceof ViewHolder) {
            if (!messageBean.isRead()) {
                ((ViewHolder) holder).isRead.setVisibility(View.VISIBLE);
            } else {
                ((ViewHolder) holder).isRead.setVisibility(View.GONE);
            }
            if (messageBean.isAttachment()) {
                ((ViewHolder) holder).attachment.setVisibility(View.VISIBLE);
            } else {
                ((ViewHolder) holder).attachment.setVisibility(View.GONE);
            }
            if (messageBean.getText() == null || messageBean.getText().equals("")) {
                ((ViewHolder) holder).subject.setText("无主题");
            } else {
                ((ViewHolder) holder).subject.setText(messageBean.getSubject());
            }
            ((ViewHolder) holder).date.setText(messageBean.getDate());
            switch (page) {
                case "收件箱":
                case "未读箱":
                case "已删除邮件":
                    ((ViewHolder) holder).from.setText(messageBean.getFrom());
                    if (messageBean.getPlainInstead() == null || messageBean.getPlainInstead().equals("")) {
                        ((ViewHolder) holder).text.setText("无内容");
                    } else {
                        ((ViewHolder) holder).text.setText(messageBean.getPlainInstead());
                    }
                    break;
                case "草稿箱":
                case "已发送":
                    ((ViewHolder) holder).from.setText(messageBean.getTo());
                    if (messageBean.getPlain() == null || messageBean.getPlain().equals("")) {
                        ((ViewHolder) holder).text.setText("无内容");
                    } else {
                        ((ViewHolder) holder).text.setText(messageBean.getPlain());
                    }
                    break;
            }
            if (isShow) {
                ((ViewHolder) holder).checkBox.setVisibility(View.VISIBLE);
                if (listCheck.get(position)) {
                    ((ViewHolder) holder).checkBox.setChecked(true);
                } else {
                    ((ViewHolder) holder).checkBox.setChecked(false);
                }
                ((ViewHolder) holder).recycler.setLongClickable(false);        //长点击后禁止再次点击
            } else {
                ((ViewHolder) holder).checkBox.setVisibility(View.GONE);
                ((ViewHolder) holder).recycler.setLongClickable(true);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (list.size() > 40) {
            return 40;
        } else {
            return list.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout recycler;
        ImageView isRead;
        TextView subject;
        ImageView attachment;
        TextView date;
        TextView from;
        CheckBox checkBox;
        TextView text;

        ViewHolder(View itemView) {
            super(itemView);
            recycler = itemView.findViewById(R.id.recycler_item);
            isRead = itemView.findViewById(R.id.isRead_item);
            subject = itemView.findViewById(R.id.subject_item);
            attachment = itemView.findViewById(R.id.attachment_item);
            date = itemView.findViewById(R.id.date_item);
            from = itemView.findViewById(R.id.from_item);
            checkBox = itemView.findViewById(R.id.checkbox_item);
            text = itemView.findViewById(R.id.text_item);
        }
    }

    private int getEmailId() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt("email_id", 0);
    }

    private void setSeen(int position) {
        EmailBean email = LitePal.find(EmailBean.class, getEmailId());
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
                Message message = messages[position];
                if (!message.getFlags().contains(Flags.Flag.SEEN)) {
                    message.setFlag(Flags.Flag.SEEN, true);
                }
            }

        } catch (javax.mail.NoSuchProviderException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (folder != null) {
                    folder.close(true);
                }
                if (store != null) {
                    store.close();
                }
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    public interface itemLongClickListener {
        void onClick(String page, int size);

        void onLongClick(String page);
    }
}
