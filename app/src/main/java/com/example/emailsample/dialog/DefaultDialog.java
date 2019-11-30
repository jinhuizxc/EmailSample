package com.example.emailsample.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.emailsample.MyDecoration;
import com.example.emailsample.R;
import com.example.emailsample.adapter.DefaultAdapter;
import com.example.emailsample.bean.EmailBean;

import org.litepal.LitePal;

import java.util.List;

public class DefaultDialog extends DialogFragment {

    private Activity activity;
    private OnDialogListener listener;

    public interface OnDialogListener {
        void setDefault(EmailBean email);
    }

    public void setDialogListener(OnDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_account, container);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_dialog);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new MyDecoration(activity, RecyclerView.VERTICAL));

        List<EmailBean> emailBeanList = LitePal.findAll(EmailBean.class);
        DefaultAdapter adapter = new DefaultAdapter(emailBeanList);
        recyclerView.setAdapter(adapter);
        adapter.setDefaultListener(new DefaultAdapter.OnDefaultListener() {
            @Override
            public void setDefault(EmailBean email) {
                if (listener != null) {
                    listener.setDefault(email);
                }
            }
        });

        return view;
    }

}
