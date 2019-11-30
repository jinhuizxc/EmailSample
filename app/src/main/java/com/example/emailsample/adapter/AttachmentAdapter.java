package com.example.emailsample.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emailsample.R;
import com.example.emailsample.bean.Attachment;
import com.example.emailsample.utils.DownloadListener;
import com.example.emailsample.utils.DownloadTask;
import com.example.emailsample.widget.CustomCircleProgressBar;

import java.io.File;
import java.util.List;

public class AttachmentAdapter extends RecyclerView.Adapter<AttachmentAdapter.ViewHolder> {

    private Context context;
    private List<Attachment> list;

    public AttachmentAdapter(List<Attachment> attachments) {
        list = attachments;
    }

    @NonNull
    @Override
    public AttachmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attachment_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.download_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = holder.getAdapterPosition();
                final Attachment attachment = list.get(position);
                if (attachment.getIsDownload()) {          //下载就打开文件
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction(Intent.ACTION_VIEW);

                    File file = new File(context.getFilesDir(), attachment.getFileName());
                    Uri fileUri = FileProvider.getUriForFile(context, "com.example.email.fileprovider", file);
                    String mimeType = context.getContentResolver().getType(fileUri);
                    intent.setDataAndType(fileUri, mimeType);
                    context.startActivity(Intent.createChooser(intent, "选择打开方式"));
                } else {                                //没下载，就开始下载
                    holder.download_open.setVisibility(View.GONE);
                    holder.progressBar.setVisibility(View.VISIBLE);

                    DownloadListener downloadListener = new DownloadListener() {
                        @Override
                        public void onProgress(int progress) {
                            holder.progressBar.setProgress(progress);
                        }

                        @Override
                        public void onSuccess() {
                            attachment.setIsDownload(true);
                            attachment.update(attachment.getId());       //更新数据库
                            notifyItemChanged(position);
                            holder.progressBar.setVisibility(View.GONE);
                            holder.download_open.setVisibility(View.VISIBLE);
                            Toast.makeText(context, "下载成功", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailed() {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.download_open.setVisibility(View.VISIBLE);
                            Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
                        }
                    };

                    DownloadTask downloadTask = new DownloadTask(downloadListener);
                    downloadTask.execute(String.valueOf(attachment.getEmailId()), String.valueOf(attachment.getMessageId()),
                            context.getFilesDir().getPath(), attachment.getFileName(), String.valueOf(attachment.getSize()));
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Attachment attachment = list.get(position);
        if (attachment != null) {
            if (attachment.getFileType() != null){
                switch (attachment.getFileType()) {
                    case "image":
                        holder.type.setImageResource(R.drawable.mz_ic_list_photo_big);
                        break;
                    case "audio":
                        holder.type.setImageResource(R.drawable.mz_ic_list_music_big);
                        break;
                    case "video":
                        holder.type.setImageResource(R.drawable.mz_ic_list_movie_big);
                        break;
                    case "pdf":
                        holder.type.setImageResource(R.drawable.mz_ic_list_pdf_big);
                        break;
                    case "xls":
                        holder.type.setImageResource(R.drawable.mz_ic_list_xls_big);
                        break;
                    case "doc":
                        holder.type.setImageResource(R.drawable.mz_ic_list_doc_big);
                        break;
                    case "txt":
                        holder.type.setImageResource(R.drawable.mz_ic_list_txt_big);
                        break;
                    case "zip":
                        holder.type.setImageResource(R.drawable.mz_ic_list_zip_big);
                        break;
                    case "rar":
                        holder.type.setImageResource(R.drawable.mz_ic_list_zip_big);
                        break;
                    case "application":
                        holder.type.setImageResource(R.drawable.mz_ic_list_app_big);
                        break;
                    default:
                        holder.type.setImageResource(R.drawable.mz_ic_list_unknow_big);
                        break;
                }
            }

        }
        holder.name.setText(attachment.getFileName());
        String s = attachment.getFileFormat() + " " + attachment.getFileSize();
        holder.format_size.setText(s);
        if (attachment.getIsDownload()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.download_open.setBackground(context.getResources().getDrawable(R.drawable.open));
                holder.download_open.setText(R.string.open);
                holder.download_open.setTextColor(context.getResources().getColor(R.color.colorAccent));
            } else {
                holder.download_open.setText(R.string.open);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.download_open.setBackground(context.getResources().getDrawable(R.drawable.download));
                holder.download_open.setText(R.string.download);
                holder.download_open.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            } else {
                holder.download_open.setText(R.string.download);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView type;
        TextView name;
        TextView format_size;
        TextView download_open;
        CustomCircleProgressBar progressBar;

        ViewHolder(View itemView) {
            super(itemView);

            type = itemView.findViewById(R.id.type_attachment);
            name = itemView.findViewById(R.id.name_attachment);
            format_size = itemView.findViewById(R.id.format_size_attachment);
            download_open = itemView.findViewById(R.id.download_open_attachment);
            progressBar = itemView.findViewById(R.id.progressBar_download);
        }
    }

    private int getEmailId() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt("email_id", 0);
    }

}

