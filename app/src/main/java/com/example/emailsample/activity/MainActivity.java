package com.example.emailsample.activity;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.example.emailsample.MyDecoration;
import com.example.emailsample.R;
import com.example.emailsample.adapter.AddressAdapter;
import com.example.emailsample.bean.EmailBean;
import com.example.emailsample.bean.MessageBean;
import com.example.emailsample.ui.main.IMainView;
import com.example.emailsample.ui.main.MainPresenter;
import com.example.emailsample.ui.main.RecyclerAdapter;

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
    RecyclerView recyclerView;
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
    @BindView(R.id.sent)
    LinearLayout sent;
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
        sent.setSelected(false);
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
        recyclerView.setAdapter(adapter);

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
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new MyDecoration(this,RecyclerView.VERTICAL));
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
            R.id.long_click_bar, R.id.view_main, R.id.float_button,
            R.id.mark_main, R.id.move_main, R.id.delete_main})
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
            case R.id.long_click_bar:
                break;
            case R.id.float_button:
                // 跳转编辑页面
                Intent intent = new Intent(this, EditEmailActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.bottom_in, R.anim.scale_out);
                break;
            case R.id.mark_main:
                break;
            case R.id.move_main:
                break;
            case R.id.delete_main:
                break;
        }
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
        recyclerView.setAdapter(adapter);
    }

    private void showLongClickInterface(String page) {
        ((DefaultItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
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
        ((DefaultItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(true);
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
