package com.example.emailsample.activity;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.example.emailsample.MyDecoration;
import com.example.emailsample.R;
import com.example.emailsample.adapter.AddressAdapter;
import com.example.emailsample.adapter.AttachmentAdapter;
import com.example.emailsample.bean.Attachment;
import com.example.emailsample.bean.EmailBean;
import com.example.emailsample.bean.MessageBean;
import com.example.emailsample.ui.main.IMainView;
import com.example.emailsample.ui.main.MainPresenter;
import com.example.emailsample.adapter.RecyclerAdapter;
import com.example.emailsample.utils.AppUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements IMainView {

    @BindView(R.id.open_drawerLayout)
    ImageView openDrawerLayout;
    @BindView(R.id.title_main)
    TextView titleMain;
    @BindView(R.id.action_bar_main)
    LinearLayout actionBarMain;
    @BindView(R.id.cancel_main)
    TextView cancelMain;
    @BindView(R.id.select_email_main)
    TextView selectEmailMain;
    @BindView(R.id.select_all_main)
    TextView selectAllMain;
    @BindView(R.id.long_click_bar)
    RelativeLayout longClickBar;
    @BindView(R.id.view_main)
    View viewMain;
    @BindView(R.id.recycler_main)
    RecyclerView recyclerViewMain;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.float_button)
    FloatingActionButton floatButton;
    @BindView(R.id.mark_main)
    TextView markMain;
    @BindView(R.id.move_main)
    TextView moveMain;
    @BindView(R.id.delete_main)
    TextView deleteMain;
    @BindView(R.id.long_click_bottom)
    LinearLayout longClickBottom;
    @BindView(R.id.restore_deleted)
    TextView restoreDeleted;
    @BindView(R.id.remove_completely_deleted)
    TextView removeCompletelyDeleted;
    @BindView(R.id.deleted_bottom)
    LinearLayout deletedBottom;
    @BindView(R.id.progressBar_main)
    ProgressBar progressBarMain;
    @BindView(R.id.logo_sidebar)
    ImageView logoSidebar;
    @BindView(R.id.address_sidebar)
    TextView addressSidebar;
    @BindView(R.id.rotate_expand)
    ImageView rotateExpand;
    @BindView(R.id.head_sidebar)
    RelativeLayout headSidebar;
    @BindView(R.id.inbox)
    LinearLayout inbox;
    @BindView(R.id.unread_box)
    LinearLayout unreadBox;
    @BindView(R.id.important_contacts)
    LinearLayout importantContacts;
    @BindView(R.id.attachment_sidebar)
    LinearLayout attachmentSidebar;
    @BindView(R.id.email_todo)
    LinearLayout emailTodo;
    @BindView(R.id.draft)
    LinearLayout draft;
    @BindView(R.id.ll_send)
    LinearLayout llSend;
    @BindView(R.id.recycler)
    LinearLayout recycler;
    @BindView(R.id.star)
    LinearLayout star;
    @BindView(R.id.folder)
    LinearLayout folder;
    @BindView(R.id.scrollView_layout)
    ScrollView scrollViewLayout;
    @BindView(R.id.recycler_address)
    RecyclerView recyclerAddress;
    @BindView(R.id.add_sidebar)
    TextView addSidebar;
    @BindView(R.id.sidebar_expand)
    LinearLayout sidebarExpand;
    @BindView(R.id.setting_sidebar)
    TextView settingSidebar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    private MainPresenter mainPresenter;
    private SharedPreferences sharedPreferences;
    private Intent intent;


    private boolean isSelectAll = false;
    private int bottomMeasureHeight;
    private int deletedBottomMeasureHeight;
    private boolean isLongClick = false;

    private int sidebarMeasureHeight;
    private boolean isExpand_sidebar = false;
    private boolean isAnimating_sidebar = false;//是否正在执行动画

    private int scrollMeasureHeight;

    private int folderMeasureHeight;
    private boolean isExpand_folder = false;
    private boolean isAnimating_folder = false;//是否正在执行动画

    private RecyclerAdapter adapter;
    private AddressAdapter addressAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        init();
        moveView();
        initData();
        inbox.setSelected(true);
        showRecyclerAddress();

    }

    private void showRecyclerAddress() {
        List<EmailBean> list = LitePal.findAll(EmailBean.class);
        // 地址适配器
        addressAdapter = new AddressAdapter(R.layout.address_item, list);
        recyclerAddress.setAdapter(addressAdapter);
        addressAdapter.setAddressListener(new AddressAdapter.OnAddressListener() {
            @Override
            public void setEmailId(EmailBean email) {
                refreshPage();
                //关闭动画
                RotateAnimation animation = new RotateAnimation(180f, 360f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(200);
                animation.setFillAfter(true);
                animation.setInterpolator(new LinearInterpolator());
                rotateExpand.startAnimation(animation);
                animateClose(sidebarExpand, 1);
                isExpand_sidebar = !isExpand_sidebar;
            }
        });
    }

    private void animateOpen(View view, int measureHeight, final int i) {
        view.setVisibility(View.VISIBLE);
        ValueAnimator animator = createDropAnimator(view, measureHeight, 0, 1);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (i == 1) {          //i=1,代表是sidebarExpand
                    isAnimating_sidebar = false;
                }
                if (i == 2) {           //i=2,代表是folderExpand
                    isAnimating_folder = false;
                }
            }
        });
        animator.start();
    }

    private void animateClose(final View view, final int i) {
        int origHeight = view.getHeight();
        ValueAnimator animator = createDropAnimator(view, 0, -origHeight, i);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                if (i == 1) {          //i=1,代表是sidebarExpand
                    isAnimating_sidebar = false;
                }
                if (i == 2) {           //i=2,代表是folderExpand
                    isAnimating_folder = false;
                }
            }
        });
        animator.start();
    }

    private ValueAnimator createDropAnimator(final View view, int start, int end, final int i) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                if (i == 1) {
                    view.setTranslationY(value);
                }
            }
        });
        return animator;
    }


    private void cancelSelect() {
        inbox.setSelected(false);
        unreadBox.setSelected(false);
        importantContacts.setSelected(false);
        attachmentSidebar.setSelected(false);
        emailTodo.setSelected(false);
        draft.setSelected(false);
        llSend.setSelected(false);
        recycler.setSelected(false);
        star.setSelected(false);
    }

    private void refreshPage() {
        cancelSelect();
        inbox.setSelected(true);
        titleMain.setText(R.string.inbox);
        swipeRefresh.setEnabled(true);      //启用下来刷新
        List<MessageBean> notDeleteList = LitePal.where("emailId = ? and isLocal = ? and isDelete = ?",
                String.valueOf(getEmailId()), "0", "0").order("id desc").find(MessageBean.class);   //查询本地未被删除的邮件
        if (notDeleteList.size() == 0) {       //如果邮件都被删除，则弹出消息提示，没有邮件
            Toast.makeText(this, "没有邮件", Toast.LENGTH_SHORT).show();
        }
        adapter.setList(notDeleteList);
        adapter.setPage("收件箱");
        recyclerViewMain.setAdapter(adapter);

        initData();

        List<EmailBean> list = LitePal.findAll(EmailBean.class);
        addressAdapter.setNewData(list);
    }

    private void initData() {
        EmailBean email = LitePal.find(EmailBean.class, getEmailId());
        String logo = email.getLogo();
        addressSidebar.setText(email.getAddress());
        switch (logo) {
            case "qq":
                logoSidebar.setImageResource(R.drawable.ic_logo_qq);
                break;
            case "163":
                logoSidebar.setImageResource(R.drawable.ic_logo_163);
                break;
        }
    }


    private void moveView() {
        //将sidebarExpand向上移动
        scrollMeasureHeight = measureHeight(scrollViewLayout);
        headSidebar.bringToFront();
        sidebarExpand.animate().translationY(-scrollMeasureHeight);

        //将longClickBottom向下移动
        bottomMeasureHeight = measureHeight(longClickBottom);
        longClickBottom.animate().translationY(bottomMeasureHeight);

        //将已删除邮件页面的底部布局下移，隐藏
        deletedBottomMeasureHeight = measureHeight(deletedBottom);
        deletedBottom.animate().translationY(deletedBottomMeasureHeight);
    }

    private int measureHeight(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return view.getMeasuredHeight();
    }


    public void init() {
        mainPresenter = new MainPresenter(this);
        intent = getIntent();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewMain.setLayoutManager(layoutManager);
        recyclerViewMain.addItemDecoration(new MyDecoration(this, RecyclerView.VERTICAL));
        // 加载主页数据
        mainPresenter.getMessageList(this);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mainPresenter.refreshData(getEmailId());
            }
        });

    }

    @OnClick({R.id.open_drawerLayout, R.id.cancel_main, R.id.select_all_main,
            R.id.float_button, R.id.setting_sidebar, R.id.ll_send,
            R.id.email_todo, R.id.draft, R.id.recycler, R.id.star, R.id.folder,
            R.id.important_contacts, R.id.attachment_sidebar, R.id.head_sidebar,
            R.id.inbox, R.id.mark_main, R.id.move_main, R.id.unread_box,
            R.id.delete_main})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.open_drawerLayout:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.cancel_main:
                hideLongClickInterface();
                break;
            case R.id.select_all_main:
                if (isSelectAll) {
                    List<Boolean> list = new ArrayList<>();
                    for (int i = 0; i < adapter.getItemCount(); i++) {
                        list.add(false);
                    }
                    adapter.setListCheck(list);
                    adapter.setSize(0);
                    selectEmailMain.setText(R.string.select_email);
                    selectAllMain.setText(R.string.select_all);
                } else {
                    List<Boolean> list = new ArrayList<>();
                    for (int i = 0; i < adapter.getItemCount(); i++) {
                        list.add(true);
                    }
                    adapter.setListCheck(list);
                    adapter.setSize(adapter.getItemCount());
                    String select = "已选择 " + adapter.getItemCount() + " 项";
                    selectEmailMain.setText(select);
                    selectAllMain.setText("全不选");
                }
                isSelectAll = !isSelectAll;
                onBottomStatus(adapter.getPage(), adapter.getSize());
                break;
            // 标记移动删除 ->布局
            case R.id.mark_main:
            case R.id.move_main:
                ToastUtils.showShort("todo");
                break;
            case R.id.delete_main:
                showDeleteMenu(drawerLayout);
                break;
            case R.id.float_button:
                // 跳转编辑页面
                Intent intent = new Intent(this, EditEmailActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.bottom_in, R.anim.scale_out);
                break;
            case R.id.setting_sidebar:
                Intent intent1 = new Intent(this, SettingActivity.class);
                //intent1.putExtra("emailId",getEmailId());
                startActivityForResult(intent1, 1);
                break;
            case R.id.head_sidebar:
                if (isAnimating_sidebar) return;
                isAnimating_sidebar = true;
                if (!isExpand_sidebar) {
                    //打开动画
                    RotateAnimation animation = new RotateAnimation(0, 180f,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    animation.setDuration(200);
                    animation.setFillAfter(true);
                    animation.setInterpolator(new LinearInterpolator());
                    rotateExpand.startAnimation(animation);
                    animateOpen(sidebarExpand, -scrollMeasureHeight, 1);
                } else {
                    //关闭动画
                    RotateAnimation animation = new RotateAnimation(180f, 360f,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    animation.setDuration(200);
                    animation.setFillAfter(true);
                    animation.setInterpolator(new LinearInterpolator());
                    rotateExpand.startAnimation(animation);
                    animateClose(sidebarExpand, 1);
                }
                isExpand_sidebar = !isExpand_sidebar;
                break;
            case R.id.inbox:
                cancelSelect();
                inbox.setSelected(true);
                titleMain.setText(R.string.inbox);
                drawerLayout.closeDrawers();
                swipeRefresh.setEnabled(true);      //启用下来刷新
                List<MessageBean> notDeleteList = LitePal.where("emailId = ? and isLocal = ? and isDelete = ?",
                        String.valueOf(getEmailId()), "0", "0").order("id desc").find(MessageBean.class);   //查询本地未被删除的邮件
                if (notDeleteList.size() == 0) {       //如果邮件都被删除，则弹出消息提示，没有邮件
                    Toast.makeText(this, "没有邮件", Toast.LENGTH_SHORT).show();
                }
                adapter.setList(notDeleteList);
                adapter.setPage("收件箱");
                recyclerViewMain.setAdapter(adapter);
                break;
            case R.id.unread_box:
                cancelSelect();
                unreadBox.setSelected(true);
                titleMain.setText(R.string.unread);
                drawerLayout.closeDrawers();
                swipeRefresh.setEnabled(false);
                List<MessageBean> notReadList = LitePal
                        .where("emailId = ? and isLocal = ? and isDelete = ? and isRead = ?",
                                String.valueOf(getEmailId()), "0", "0", "0")
                        .order("id desc").find(MessageBean.class);
                adapter.setList(notReadList);
                adapter.setPage("未读箱");
                recyclerViewMain.setAdapter(adapter);
                break;
            case R.id.important_contacts:
                cancelSelect();
                importantContacts.setSelected(true);
                break;
            case R.id.attachment_sidebar:
                // 附件
                cancelSelect();
                attachmentSidebar.setSelected(true);
                titleMain.setText(R.string.attachment);
                drawerLayout.closeDrawers();
                swipeRefresh.setEnabled(false);      //禁用下来刷新
                List<Attachment> attachmentList = LitePal.where("emailId = ? and isLocal = ?",
                        String.valueOf(getEmailId()), "0").order("id desc").find(Attachment.class);   //查询本地保存的草稿邮件
                AttachmentAdapter attachmentAdapter = new AttachmentAdapter(attachmentList);
                recyclerViewMain.setAdapter(attachmentAdapter);
                break;
            case R.id.email_todo:
                cancelSelect();
                emailTodo.setSelected(true);
                break;
            case R.id.draft:
                cancelSelect();
                draft.setSelected(true);
                titleMain.setText(R.string.draft);
                drawerLayout.closeDrawers();
                swipeRefresh.setEnabled(false);      //禁用下来刷新
                List<MessageBean> draftList = LitePal.where("emailId = ? and isLocal = ? and isSend = ?",
                        String.valueOf(getEmailId()), "1", "0").order("id desc").find(MessageBean.class);   //查询本地保存的草稿邮件
                adapter.setList(draftList);
                adapter.setPage("草稿箱");
                recyclerViewMain.setAdapter(adapter);
                break;
            case R.id.ll_send:
                cancelSelect();
                llSend.setSelected(true);
                titleMain.setText(R.string.sent);
                drawerLayout.closeDrawers();
                swipeRefresh.setEnabled(false);      //禁用下来刷新
                List<MessageBean> sentList = LitePal.where("emailId = ? and isLocal = ? and isSend = ?",
                        String.valueOf(getEmailId()), "1", "1").order("id desc").find(MessageBean.class);   //查询本地保存的草稿邮件
                adapter.setList(sentList);
                adapter.setPage("已发送");
                recyclerViewMain.setAdapter(adapter);
                break;
            case R.id.recycler:
                // 已删除邮件
                cancelSelect();
                recycler.setSelected(true);
                titleMain.setText(R.string.recycler);
                drawerLayout.closeDrawers();
                swipeRefresh.setEnabled(false);         //禁用下拉刷新
                List<MessageBean> deletedList = LitePal.where("emailId = ? and isLocal = ? and isDelete = ?",
                        String.valueOf(getEmailId()), "0", "1").order("id desc").find(MessageBean.class);
                if (deletedList.size() == 0) {
                    Toast.makeText(this, "没有已删除邮件", Toast.LENGTH_SHORT).show();
                }
                adapter.setList(deletedList);
                adapter.setPage("已删除邮件");
                recyclerViewMain.setAdapter(adapter);
                break;
            case R.id.star:
                cancelSelect();
                star.setSelected(true);
                break;
            case R.id.folder:
                cancelSelect();
                folder.setSelected(true);
                drawerLayout.closeDrawers();
                break;
        }
    }

    //显示删除弹窗
    private void showDeleteMenu(View v) {
        // 加载PopupWindow的布局
        View view = View.inflate(this, R.layout.menu_delete, null);
        TextView deleteMenu = view.findViewById(R.id.delete_menu_main);
        TextView cancelMenu = view.findViewById(R.id.cancel_menu_main);

        String delete = "删除 " + adapter.getSize() + " 封邮件";
        deleteMenu.setText(delete);

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

        deleteMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideLongClickInterface();
                String page = adapter.getPage();
                List<Boolean> list = adapter.getListCheck();
                List<MessageBean> beanList = adapter.getList();
                switch (page) {
                    case "收件箱":
                    case "未读箱":
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i)) {
                                MessageBean messageBean = beanList.get(i);
                                messageBean.setDelete(true);
                                messageBean.update(messageBean.getId());    //在本地数据库上将其设置为删除
                                beanList.remove(i);         //数据源上移除
                                adapter.notifyItemRemoved(i);       //recyclerView上移除
                                //adapter.notifyItemRangeChanged(i,adapter.getItemCount());
                            }
                        }
                        break;
                    case "草稿箱":
                    case "已发送":
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i)) {
                                MessageBean messageBean = beanList.get(i);
                                if (messageBean.isAttachment()) {         //判断是否有附件
                                    LitePal.deleteAll(Attachment.class,
                                            "messageId = ?", String.valueOf(messageBean.getId()));
                                }
                                LitePal.delete(MessageBean.class, messageBean.getId());       //在本地数据库上将其设置为删除
                                beanList.remove(i);         //数据源上移除
                                adapter.notifyItemRemoved(i);       //recyclerView上移除
                                //adapter.notifyItemRangeChanged(i,adapter.getItemCount());
                            }
                        }
                        break;
                    case "已删除邮件":
                        List<Integer> integerList = new ArrayList<>();
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i)) {
                                //从本地数据库中得到最新数据，因为adapter中的数据不能实时更新
                                MessageBean messageBean = LitePal.find(MessageBean.class, beanList.get(i).getId());
                                integerList.add(messageBean.getOtherId());               //得到在服务器上message的下标
                                if (messageBean.isAttachment()) {         //判断是否有附件
                                    List<Attachment> attachmentList = LitePal.findAll(Attachment.class, messageBean.getOtherId());
                                    for (Attachment attachment : attachmentList) {
                                        if (attachment.getIsDownload()) {      //判断附件是否下载，下载的话，就删除本地文件
                                            MainActivity.this.deleteFile(attachment.getFileName());
                                        }
                                        LitePal.delete(Attachment.class, attachment.getId());           //在本地数据库上删除附件
                                    }
                                }
                                LitePal.delete(MessageBean.class, messageBean.getId());   //在本地数据库上将其删除
                                beanList.remove(i);         //数据源上移除
                                adapter.notifyItemRemoved(i);       //recyclerView上移除
                            }
                        }
                        mainPresenter.deleteEmail(integerList);        //在服务器上彻底删除

                        break;
                }
                popWindow.dismiss();
            }
        });

        cancelMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.dismiss();
            }
        });
    }

    /**
     * 设置屏幕的背景透明度
     */
    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        getWindow().setAttributes(lp);
    }


    private void onBottomStatus(String page, int size) {
        switch (page) {
            case "收件箱":
            case "未读箱":
                if (size != 0) {
                    String select = "已选择 " + size + " 项";
                    selectEmailMain.setText(select);
                    deleteMain.setClickable(true);
                    longClickBottom.setSelected(true);
                    markMain.setTextColor(getResources().getColor(R.color.black));
                    moveMain.setTextColor(getResources().getColor(R.color.black));
                    deleteMain.setTextColor(getResources().getColor(R.color.black));

                    if (size == adapter.getItemCount()) {
                        isSelectAll = true;
                        selectAllMain.setText("全不选");
                    }
                } else {
                    deleteMain.setClickable(false);
                    selectEmailMain.setText(R.string.select_email);
                    longClickBottom.setSelected(false);
                    markMain.setTextColor(getResources().getColor(R.color.text_color_gray));
                    moveMain.setTextColor(getResources().getColor(R.color.text_color_gray));
                    deleteMain.setTextColor(getResources().getColor(R.color.text_color_gray));
                }
                break;
            case "草稿箱":
            case "已发送":
                if (size != 0) {
                    String select = "已选择 " + size + " 项";
                    selectEmailMain.setText(select);
                    deleteMain.setClickable(true);
                    longClickBottom.setSelected(true);
                    deleteMain.setTextColor(getResources().getColor(R.color.black));

                    if (size == adapter.getItemCount()) {
                        isSelectAll = true;
                        selectAllMain.setText("全不选");
                    }
                } else {
                    deleteMain.setClickable(false);
                    selectEmailMain.setText(R.string.select_email);
                    longClickBottom.setSelected(false);
                    deleteMain.setTextColor(getResources().getColor(R.color.text_color_gray));
                }
                break;
            case "已删除邮件":
                if (size != 0) {
                    String select = "已选择 " + size + " 项";
                    selectEmailMain.setText(select);
                    restoreDeleted.setClickable(true);
                    deleteMain.setSelected(true);
                    deletedBottom.setSelected(true);
                    restoreDeleted.setTextColor(getResources().getColor(R.color.black));
                    removeCompletelyDeleted.setTextColor(getResources().getColor(R.color.black));

                    if (size == adapter.getItemCount()) {
                        isSelectAll = true;
                        selectAllMain.setText("全不选");
                    }
                } else {
                    selectEmailMain.setText(R.string.select_email);
                    restoreDeleted.setClickable(false);
                    deleteMain.setSelected(false);
                    deletedBottom.setSelected(false);
                    restoreDeleted.setTextColor(getResources().getColor(R.color.text_color_gray));
                    removeCompletelyDeleted.setTextColor(getResources().getColor(R.color.text_color_gray));
                }
                break;
        }
    }

    @Override
    public int getEmailId() {
        return sharedPreferences.getInt("email_id", 0);
    }

    @Override
    public void showProgressBar() {
        progressBarMain.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBarMain.setVisibility(View.GONE);
    }

    @Override
    public void showRecyclerView(List<MessageBean> list) {
        adapter = new RecyclerAdapter(list);
        adapter.setPage("收件箱");

        // 长按显示隐藏的底部布局
        adapter.setItemLongClickListener(new RecyclerAdapter.itemLongClickListener() {
            @Override
            public void onClick(String page, int size) {
                onBottomStatus(page, size);
            }

            @Override
            public void onLongClick(String page) {
                if (adapter.getItemCount() == 1) {
                    isSelectAll = true;
                    selectAllMain.setText("全不选");
                }
                showLongClickInterface(page);
            }
        });
        recyclerViewMain.setAdapter(adapter);
    }

    private void showLongClickInterface(String page) {
        ((DefaultItemAnimator) recyclerViewMain.getItemAnimator()).setSupportsChangeAnimations(false);
        isLongClick = true;
        longClickBar.setVisibility(View.VISIBLE);
        floatButton.animate().translationY(floatButton.getHeight() + floatButton.getMeasuredHeight());
        switch (page) {
            case "收件箱":
            case "未读箱":
                longClickBottom.setSelected(true);
                longClickBottom.setVisibility(View.VISIBLE);
                longClickBottom.animate().translationY(0);
                break;
            case "草稿箱":
            case "已发送":
                longClickBottom.setSelected(true);
                markMain.setVisibility(View.GONE);
                moveMain.setVisibility(View.GONE);
                longClickBottom.setVisibility(View.VISIBLE);
                longClickBottom.animate().translationY(0);
                break;
            case "已删除邮件":
                deletedBottom.setSelected(true);
                deletedBottom.setVisibility(View.VISIBLE);
                deletedBottom.animate().translationY(0);
                break;
        }
    }


    @Override
    public void hideRefresh() {
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void showSuccessMessage() {
        ToastUtils.showShort("刷新成功");
    }

    @Override
    public void showErrorMessage() {
        ToastUtils.showShort("获取信息失败");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    List<MessageBean> draftList = LitePal.where("emailId = ? and isLocal = ? and isSend = ?",
                            String.valueOf(getEmailId()), "1", "0").order("id desc").find(MessageBean.class);   //查询本地保存的草稿邮件
                    adapter.setList(draftList);
                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    //刷新页面
                    refreshPage();
                } else if (resultCode == 4) {        //说明删了一个账号，让address列表更新
                    List<EmailBean> list = LitePal.findAll(EmailBean.class);
                    addressAdapter.setNewData(list);
                } else if (resultCode == 5) {
                    finish();             //凉了，
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    finish();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else if (isLongClick) {
            hideLongClickInterface();
        } else {
            super.onBackPressed();
        }
    }

    private void hideLongClickInterface() {
        ((DefaultItemAnimator) recyclerViewMain.getItemAnimator()).setSupportsChangeAnimations(true);
        isLongClick = false;
        adapter.setShow(false);
        longClickBar.setVisibility(View.GONE);
        String page = adapter.getPage();
        switch (page) {
            case "收件箱":
            case "未读箱":
                longClickBottom.animate().translationY(bottomMeasureHeight);
                longClickBottom.setVisibility(View.GONE);
                break;
            case "草稿箱":
            case "已发送":
                longClickBottom.animate().translationY(bottomMeasureHeight);
                longClickBottom.setVisibility(View.GONE);
                markMain.setVisibility(View.VISIBLE);
                moveMain.setVisibility(View.VISIBLE);
                break;
            case "已删除邮件":
                deletedBottom.animate().translationY(deletedBottomMeasureHeight);
                deletedBottom.setVisibility(View.GONE);
                break;
        }
        floatButton.animate().translationY(0);
    }

}
