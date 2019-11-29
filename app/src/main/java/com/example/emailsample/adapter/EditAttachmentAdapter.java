package com.example.emailsample.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.emailsample.R;
import com.example.emailsample.bean.Attachment;

import java.util.List;

public class EditAttachmentAdapter extends RecyclerView.Adapter<EditAttachmentAdapter.ViewHolder> {

    private List<Attachment> list;
    private EditAttachmentListener listener;

    public EditAttachmentAdapter(List<Attachment> attachments){
        list = attachments;
    }

    public void setEditAttachmentListener(EditAttachmentListener editAttachmentListener){
        listener = editAttachmentListener;
    }

    @NonNull
    @Override
    public EditAttachmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_attachment_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (listener != null){
                    listener.removeItem(position);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull EditAttachmentAdapter.ViewHolder holder, int position) {
        Attachment attachment = list.get(position);
        switch (attachment.getFileType()) {
            case "image":
                if (attachment.getFilePath() != null){
                    Bitmap bitmap = BitmapFactory.decodeFile(attachment.getFilePath());
                    holder.type.setImageBitmap(bitmap);
                }else {
                    holder.type.setImageResource(R.drawable.mz_ic_list_photo_big);
                }
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
            case "zip":
                holder.type.setImageResource(R.drawable.mz_ic_list_zip_big);
                break;
            case "application":
                holder.type.setImageResource(R.drawable.mz_ic_list_app_big);
                break;
            default:
                holder.type.setImageResource(R.drawable.mz_ic_list_unknow_big);
                break;
        }
        holder.filename.setText(attachment.getFileName());
        holder.fileSize.setText(attachment.getFileSize());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView type;
        TextView filename;
        TextView fileSize;
        ImageView delete;

        ViewHolder(View itemView) {
            super(itemView);

            type = itemView.findViewById(R.id.type_edit);
            filename = itemView.findViewById(R.id.filename_edit);
            fileSize = itemView.findViewById(R.id.fileSize_edit);
            delete = itemView.findViewById(R.id.delete_item_edit);
        }
    }

    public interface EditAttachmentListener{
        void removeItem(int position);
    }
}
