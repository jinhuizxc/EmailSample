package com.example.emailsample.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.emailsample.R;
import com.example.emailsample.bean.EmailBean;

import java.util.List;

public class DefaultAdapter extends RecyclerView.Adapter<DefaultAdapter.ViewHolder> {

    private Context context;
    private List<EmailBean> list;
    private SharedPreferences sharedPreferences;
    private OnDefaultListener listener;

    public interface OnDefaultListener {
        void setDefault(EmailBean email);
    }

    public DefaultAdapter(List<EmailBean> list) {
        this.list = list;
    }

    public void setDefaultListener(OnDefaultListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public DefaultAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                EmailBean email = list.get(position);
                if (listener != null) {
                    listener.setDefault(email);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DefaultAdapter.ViewHolder holder, int position) {
        EmailBean email = list.get(position);
        holder.account.setText(email.getAddress());
        if (getDefaultId() == email.getId()) {
            holder.check.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout layout;
        private TextView account;
        private ImageView check;

        ViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.item_dialog);
            account = itemView.findViewById(R.id.account_dialog);
            check = itemView.findViewById(R.id.check_dialog);
        }
    }

    private int getDefaultId() {
        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return sharedPreferences.getInt("default_id", 0);
    }
}
