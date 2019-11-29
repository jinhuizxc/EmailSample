package com.example.emailsample.dialog;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.example.emailsample.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ProtocolDialog extends DialogFragment {

    @BindView(R.id.POP3_protocol)
    TextView pop3;
    @BindView(R.id.IMAP_protocol)
    TextView imap;
    @BindView(R.id.Exchange_protocol)
    TextView exchange;
    Unbinder unbinder;

    private protocolDialogListener listener;

    public interface protocolDialogListener {
        void protocolListener(String protocol);
    }

    public void setProtocolDialogListener(protocolDialogListener listener) {
        this.listener = listener;
    }

    public static ProtocolDialog newInstance(String protocol) {
        ProtocolDialog dialog = new ProtocolDialog();
        Bundle args = new Bundle();
        args.putString("protocol", protocol);
        dialog.setArguments(args);
        return dialog;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_protocol, container);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            String protocol = getArguments().getString("protocol", "");
            ToastUtils.showShort("protocol = " + protocol);
            selectProtocol(protocol);
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    String protocol = null;

    @OnClick({R.id.POP3_protocol, R.id.IMAP_protocol, R.id.Exchange_protocol})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.POP3_protocol:
                protocol = pop3.getText().toString();
                selectProtocol(protocol);
                break;
            case R.id.IMAP_protocol:
                protocol = imap.getText().toString();
                selectProtocol(protocol);
                break;
            case R.id.Exchange_protocol:
                protocol = exchange.getText().toString();
                selectProtocol(protocol);
                break;
        }

        if (listener != null) {
            listener.protocolListener(protocol);
        }

    }

    private void selectProtocol(String protocol) {
        cancelSelect();
        Drawable drawable = getResources().getDrawable(R.drawable.ic_check_black_24dp);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        switch (protocol) {
            case "POP3":
                pop3.setTextColor(getResources().getColor(R.color.colorAccent));
                pop3.setCompoundDrawables(null, null, drawable, null);
                break;
            case "IMAP":
                imap.setTextColor(getResources().getColor(R.color.colorAccent));
                imap.setCompoundDrawables(null, null, drawable, null);
                break;
            case "Exchange":
                exchange.setTextColor(getResources().getColor(R.color.colorAccent));
                exchange.setCompoundDrawables(null, null, drawable, null);
                break;
        }
    }

    private void cancelSelect() {
        pop3.setTextColor(getResources().getColor(R.color.black));
        imap.setTextColor(getResources().getColor(R.color.black));
        exchange.setTextColor(getResources().getColor(R.color.black));

        pop3.setCompoundDrawables(null, null, null, null);
        imap.setCompoundDrawables(null, null, null, null);
        exchange.setCompoundDrawables(null, null, null, null);
    }

}
