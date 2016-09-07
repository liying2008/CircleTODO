package lxy.liying.circletodo.ui;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.libs.zxing.CaptureActivity;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import de.hdodenhof.circleimageview.CircleImageView;
import lxy.liying.circletodo.R;
import lxy.liying.circletodo.adapter.CalendarAdapter;
import lxy.liying.circletodo.app.App;
import lxy.liying.circletodo.domain.CircleUser;
import lxy.liying.circletodo.domain.SelectedColor;
import lxy.liying.circletodo.operation.FileDownloader;
import lxy.liying.circletodo.operation.YearSelector;
import lxy.liying.circletodo.permission.PermissionManager;
import lxy.liying.circletodo.task.RefreshUserInfoTask;
import lxy.liying.circletodo.utils.Constants;
import lxy.liying.circletodo.utils.DES;
import lxy.liying.circletodo.utils.DateMarkUtils;
import lxy.liying.circletodo.utils.ErrorCodes;
import lxy.liying.circletodo.utils.FileUtils;
import lxy.liying.circletodo.utils.QRCodeUtils;

public class CalendarActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    private GestureDetector gestureDetector = null;
    private CalendarAdapter calendarAdapter = null;
    private ViewFlipper flipper = null;
    private GridView gridView = null;
    public static int jumpMonth = 0; // 每次滑动，增加或减去一个月,默认为0（即显示当前月）
    public int year_c = 0;
    public int month_c = 0;
    private int day_c = 0;
    public static String currentDate = "";
    private LinearLayout llDateMark;
    private ListView lvDateMarkSelected;
    private TextView tvSelectMsg;
    private FloatingActionMenu menuFab;
    private View lastView;
    private static String lastDate = "";
    private static int flipperStartY;
    private static int flipperEndY;

    /**
     * 当前的年月，现在日历顶端
     */
    private TextView tvCurrentMonth;
    /**
     * 上个月
     */
    private ImageView prevMonth;
    /**
     * 下个月
     */
    private ImageView nextMonth;
    // 当前个人信息
    private CircleImageView ivHeadIcon;
    private TextView tvCurrUserName;
    private TextView tvCurrEmail;
    private LinearLayout llAddMarks;
    private DateMarkUtils dateMarkUtils;

    /**
     * 构造方法
     */
    public CalendarActivity() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d", Locale.getDefault());
        currentDate = sdf.format(date); // 当期日期
        lastDate = currentDate;
        year_c = Integer.parseInt(currentDate.split("-")[0]);
        month_c = Integer.parseInt(currentDate.split("-")[1]);
        day_c = Integer.parseInt(currentDate.split("-")[2]);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findView();
        setListener();
        // 获取权限
        PermissionManager.grantPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        PermissionManager.grantPermission(this, Manifest.permission.READ_PHONE_STATE);
        PermissionManager.grantPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        PermissionManager.grantPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW);

        gestureDetector = new GestureDetector(this, new MyGestureListener());
        flipper.removeAllViews();
        calendarAdapter = new CalendarAdapter(this, App.getInstance().getToast(), jumpMonth, year_c, month_c);
        addGridView();
        gridView.setAdapter(calendarAdapter);
        flipper.addView(gridView, 0);

        addTextToTopTextView(tvCurrentMonth);

        if (Constants.UNLOGIN_UID.equals(App.CURRENT_UID)) {
            // 用户未登录，打开用户登录界面
            jumpToLogin();
        } else {
            refreshUserInfo();  // 刷新用户信息
        }
        // 点击头部年月可以选择年份
        tvCurrentMonth.setOnClickListener(new YearSelector(this, calendarAdapter, flipper, gridView, tvCurrentMonth));
        if (App.DISPLAY_MODE == 0) {
            // 每日标记显示在界面底部
            llDateMark.setVisibility(View.VISIBLE);
            dateMarkUtils = new DateMarkUtils(lvDateMarkSelected, null, currentDate, CalendarActivity.this, tvSelectMsg, null);
            dateMarkUtils.initSelectedListView();
        }
        llAddMarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalendarActivity.this, RemainListActivity.class);
                intent.putExtra("date", lastDate);
                startActivityForResult(intent, Constants.REQUEST_CODE);
                overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
            }
        });
        // 调用自动更新接口，自动检查更新
        BmobUpdateAgent.update(this);
    }

    /**
     * Find view
     */
    private void findView() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        menuFab = (FloatingActionMenu) findViewById(R.id.menuFab);
        menuFab.setClosedOnTouchOutside(true);
        FloatingActionButton fabSend = (FloatingActionButton) findViewById(R.id.fabSend);
        FloatingActionButton fabStatistics = (FloatingActionButton) findViewById(R.id.fabStatistics);
        FloatingActionButton fabRemove = (FloatingActionButton) findViewById(R.id.fabRemove);
        fabSend.setOnClickListener(fabClickListener);
        fabStatistics.setOnClickListener(fabClickListener);
        fabRemove.setOnClickListener(fabClickListener);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Inflate HeaderView
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        ivHeadIcon = (CircleImageView) headerView.findViewById(R.id.ivHeadIcon);
        tvCurrUserName = (TextView) headerView.findViewById(R.id.tvCurrUserName);
        tvCurrEmail = (TextView) headerView.findViewById(R.id.tvCurrEmail);
        llDateMark = (LinearLayout) findViewById(R.id.llDateMark);
        flipper = (ViewFlipper) findViewById(R.id.flipper);

        tvCurrentMonth = (TextView) findViewById(R.id.currentMonth);
        prevMonth = (ImageView) findViewById(R.id.prevMonth);
        nextMonth = (ImageView) findViewById(R.id.nextMonth);

        lvDateMarkSelected = (ListView) findViewById(R.id.lvDateMarkSelected);
        tvSelectMsg = (TextView) findViewById(R.id.tvSelectMsg);
        llAddMarks = (LinearLayout) findViewById(R.id.llAddMarks);
    }

    /**
     * FloatingActionButton点击监听
     */
    private View.OnClickListener fabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fabSend:
                    // 发送该日标记
                    String event = lastDate + "日标记：\n";
                    List<Integer> colorIds = App.deService.getColorId(lastDate);
                    if (colorIds != null && colorIds.size() > 0) {
                        List<SelectedColor> selectedColors = App.scService.getSelectedColorByIds(colorIds);
                        for (SelectedColor sc : selectedColors) {
                            event = event + sc.getColor_event() + "\n";
                        }
                    } else {
                        event = event + "该日没有标记。\n";
                    }
                    event += "\n——来自CircleTODO";
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.setType("text/*");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, event);
                    startActivity(sendIntent);
                    break;
                case R.id.fabStatistics:
                    // 统计标记
                    statisticsMarks();
                    break;
                case R.id.fabRemove:
                    // 删除该日标记
                    AlertDialog.Builder builder = App.getAlertDialogBuilder(CalendarActivity.this);
                    builder.setTitle("删除提醒");
                    builder.setMessage("您确定要删除 " + lastDate + " 日的标记吗？");
                    builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            App.deService.removeMarksOfDate(lastDate);
                            App.getInstance().showToast(lastDate + "日的标记已删除。");
                            refreshGridView();
                            refreshDateMark();
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    builder.create().show();
                    break;
                default:
                    break;
            }
            menuFab.close(false);
        }
    };

    /**
     * 统计今日及以后的所有标记
     */
    private void statisticsMarks() {
        Intent intent = new Intent(this, StatisticsActivity.class);
        intent.putExtra("date", currentDate);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
    }

    private long lastBackKeyTime;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (menuFab.isOpened()) {
            menuFab.close(true);
        } else {
            long delay = Math.abs(System.currentTimeMillis() - lastBackKeyTime);
            if (delay > 3000) {
                // 双击退出程序
                App.getInstance().showToast("再按一次，退出程序。");
                lastBackKeyTime = System.currentTimeMillis();
            } else {
                finish();
            }
        }
    }

    private void setListener() {
        prevMonth.setOnClickListener(this);
        nextMonth.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // 每次添加gridView到viewFlipper中时给的标记
        int gvFlag = 0;
        switch (v.getId()) {
            case R.id.nextMonth: // 下一个月
                enterNextMonth(gvFlag);
                break;
            case R.id.prevMonth: // 上一个月
                enterPrevMonth(gvFlag);
                break;
            default:
                break;
        }
    }

    /**
     * 长按每个日期项（暂时没有功能）
     * todo
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     * @return
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        // 点击任何一个item，得到这个item的日期(排除点击的是周日到周六(点击不响应))
        int startPosition = calendarAdapter.getStartPositon();
        int endPosition = calendarAdapter.getEndPosition();
        if (startPosition <= position + 7 && position <= endPosition - 7) {
            String scheduleDay = calendarAdapter.getDateByClickItem(position).split("\\.")[0]; // 这一天的阳历
            // 这一天的阴历
            String scheduleYear = calendarAdapter.getShowYear();
            String scheduleMonth = calendarAdapter.getShowMonth();

            final String date = scheduleYear + "-" + scheduleMonth + "-" + scheduleDay;
        }
        return true;
    }

    /**
     * 点击每个日期项
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int startPosition = calendarAdapter.getStartPositon();
        int endPosition = calendarAdapter.getEndPosition();
        if (startPosition <= position + 7 && position <= endPosition - 7) {
            String scheduleDay = calendarAdapter.getDateByClickItem(position).split("\\.")[0]; // 这一天的阳历

            // 这一天的阴历
            String scheduleYear = calendarAdapter.getShowYear();
            String scheduleMonth = calendarAdapter.getShowMonth();

            // 还原之前控件的默认背景
            if (lastView != null) {
                lastView.setBackgroundColor(getResources().getColor(R.color.grid_bg));
            }
            // 给当前控件设置背景
            view.setBackgroundColor(getResources().getColor(R.color.holo_green_dark));
            lastView = view;
            String date = scheduleYear + "-" + scheduleMonth + "-" + scheduleDay;
            lastDate = date;

            if (App.mColorLists == null) {
                // 备选颜色列表为空
                AlertDialog.Builder builder = App.getAlertDialogBuilder(CalendarActivity.this);
                builder.setTitle("备选颜色列表为空");
                builder.setMessage("请先去选择你喜欢的颜色并添加标记。");
                builder.setPositiveButton("去添加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到添加标记界面
                        Intent intent = new Intent(CalendarActivity.this, MarkActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
                    }
                });
                builder.create().show();
            } else {
                if (App.DISPLAY_MODE == 1) {
                    // 以弹窗方式显示
                    Intent intent = new Intent(CalendarActivity.this, DateMarkActivity.class);
                    intent.putExtra("date", date);
                    startActivityForResult(intent, Constants.REQUEST_CODE);
                    // 加载动画效果
                    overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
                } else if (App.DISPLAY_MODE == 0) {
                    // 显示在界面底部
                    refreshDateMark();
                }
            }
        }
    }

    /**
     * 点击头像 <br/>
     * 如果已经登录则进入个人信息页面 <br/>
     * 如果未登录，则跳转到登录页面 <br/>
     *
     * @param view
     */
    public void openPersonalPage(View view) {
        if (Constants.UNLOGIN_UID.equals(App.CURRENT_UID)) {
            // 用户未登录
            Intent intent = new Intent(CalendarActivity.this, LoginActivity.class);
            startActivityForResult(intent, Constants.REQUEST_CODE);
            overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
        } else {
            // 用户已登录
            Intent intent = new Intent(CalendarActivity.this, PersonalActivity.class);
            startActivityForResult(intent, Constants.REQUEST_CODE);
            overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
        }
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            int gvFlag = 0; // 每次添加gridview到viewflipper中时给的标记
            // 取得Flipper的大小
            if (flipperStartY == 0 || flipperEndY == 0) {
                flipperStartY = (int) flipper.getY();
                flipperEndY = (int) flipper.getY() + flipper.getHeight();
            }
            if (e1 != null && e2 != null) {
//                System.out.println("-->" + flipperStartY  + ", " + flipperEndY);
                if (e1.getY() >= flipperStartY && e1.getY() <= flipperEndY) {
                    if (e2.getY() >= flipperStartY && e2.getY() <= flipperEndY) {
                        // 在ViewFlipper内滑动
                        if (e1.getX() - e2.getX() > 40) {
                            // 向左滑动
                            enterNextMonth(gvFlag);
                        } else if (e1.getX() - e2.getX() < -40) {
                            // 向右滑动
                            enterPrevMonth(gvFlag);
                        }
                    }
                    return true;
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    /**
     * 移动到下一个月
     *
     * @param gvFlag
     */
    private void enterNextMonth(int gvFlag) {
        addGridView(); // 添加一个gridView
        jumpMonth++; // 下一个月

        calendarAdapter.setData(jumpMonth, year_c, month_c);
        gridView.setAdapter(calendarAdapter);
        addTextToTopTextView(tvCurrentMonth); // 移动到下一月后，将当月显示在头标题中
        gvFlag++;
        flipper.addView(gridView, gvFlag);
        flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
        flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
        flipper.showNext();
        flipper.removeViewAt(0);
    }

    /**
     * 移动到上一个月
     *
     * @param gvFlag
     */
    private void enterPrevMonth(int gvFlag) {
        addGridView(); // 添加一个gridView
        jumpMonth--; // 上一个月

        calendarAdapter.setData(jumpMonth, year_c, month_c);
        gridView.setAdapter(calendarAdapter);
        gvFlag++;
        addTextToTopTextView(tvCurrentMonth); // 移动到上一月后，将当月显示在头标题中
        flipper.addView(gridView, gvFlag);

        flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
        flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
        flipper.showPrevious();
        flipper.removeViewAt(0);
    }

    /**
     * 添加头部的年份 闰哪月等信息
     *
     * @param view
     */
    public void addTextToTopTextView(TextView view) {
        StringBuffer textDate = new StringBuffer();
        textDate.append(calendarAdapter.getShowYear()).append("年").append(calendarAdapter.getShowMonth()).append("月").append("\t");
        view.setText(textDate);
    }

    public void addGridView() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.MATCH_PARENT);
        gridView = new GridView(this);
        gridView.setNumColumns(7);
        gridView.setGravity(Gravity.CENTER_VERTICAL);

//		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView.setVerticalSpacing(1);
        gridView.setHorizontalSpacing(1);
        gridView.setLayoutParams(params);

        gridView.setOnTouchListener(new View.OnTouchListener() {
            // 将gridview中的触摸事件回传给gestureDetector
            public boolean onTouch(View v, MotionEvent event) {
                return CalendarActivity.this.gestureDetector.onTouchEvent(event);
            }
        });

        gridView.setOnItemClickListener(this);      // 点击事件
        gridView.setOnItemLongClickListener(this);  // 长按事件
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constants.REFRESH_VIEW_CODE) {
            // 需要刷新GridView
            refreshGridView();
        } else if (resultCode == Constants.REFRESH_USER_INFO_CODE) {
            // 需要刷新用户信息
            refreshUserInfo();
        } else if (resultCode == Constants.EXIT_APP_CODE) {
            // 需要退出应用
            // 已废弃
        } else if (resultCode == CaptureActivity.QR_OK) {
            // 获取到了扫描二维码的结果
            String text = data.getStringExtra("data");
            // 开始下载并复制标记
            copyHisMarks(text);
        } else if (resultCode == Constants.DATE_MARK_MODE) {
            // 刷新每日标记显示
            refreshDateMark();
        } else if (resultCode == Constants.REFRESH_GRID_AND_MARK_MODE) {
            // 刷新GridView和每日标记显示
            refreshGridView();
            refreshDateMark();
        }
    }

    /**
     * 刷新每日标记显示
     */
    private void refreshDateMark() {
        if (App.DISPLAY_MODE == 1) {
            llDateMark.setVisibility(View.GONE);
        } else if (App.DISPLAY_MODE == 0) {
            llDateMark.setVisibility(View.VISIBLE);

            if (dateMarkUtils != null) {
                dateMarkUtils = null;
            }
            dateMarkUtils = new DateMarkUtils(lvDateMarkSelected, null, lastDate, CalendarActivity.this, tvSelectMsg, null);
            dateMarkUtils.initSelectedListView();
        }
    }

    /**
     * 刷新GridView
     */
    public void refreshGridView() {
        App.getSelectedColors();
        calendarAdapter.notifyDataSetChanged();
    }

    /**
     * 刷新用户信息
     */
    private void refreshUserInfo() {
        if (Constants.UNLOGIN_UID.equals(App.CURRENT_UID) || App.currentUser.getObjectId() == null) {
            ivHeadIcon.setImageResource(R.drawable.unlogin);
            tvCurrUserName.setText("未登录");
            tvCurrEmail.setText("点击头像登录");
            // 更新已选择的颜色
            App.getSelectedColors();
            // 需要刷新GridView
            calendarAdapter.notifyDataSetChanged();
            gridView.invalidate();
            refreshDateMark();
            // 跳转到登录页面
            jumpToLogin();
            return;
        }
        tvCurrEmail.setText("正在加载用户信息……");
        // 2、更新用户名
        tvCurrUserName.setText(App.currentUser.getUsername());
        // 在子线程中更新用户头像和邮箱
        RefreshUserInfoTask task = new RefreshUserInfoTask(ivHeadIcon, tvCurrEmail, this);
        task.execute();
        // 4、更新已选择的颜色
        App.getSelectedColors();
        // 5、需要刷新GridView
        calendarAdapter.notifyDataSetChanged();
        // 6、需要刷新每日标记显示
       refreshDateMark();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_mark:
                Intent intent = new Intent(this, MarkActivity.class);
                startActivityForResult(intent, Constants.REQUEST_CODE);
                // 新Activity加载动画
                overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.action_mark) {
            // 打开标记页面
            Intent intent = new Intent(this, MarkActivity.class);
            startActivityForResult(intent, Constants.REQUEST_CODE);
            // 新Activity加载动画
            overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
        } else if (id == R.id.action_cleanup) {
            // 打开标记清理页面
            Intent intent2 = new Intent(this, CleanupActivity.class);
            startActivityForResult(intent2, Constants.REQUEST_CODE);
            // 新Activity加载动画
            overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
        } else if (id == R.id.action_backup) {
            // 备份到云端
            if (Constants.UNLOGIN_UID.equals(App.CURRENT_UID)) {
                // 用户没有登录，不能使用备份功能
                App.getInstance().showToast("请先登录。", Toast.LENGTH_LONG);
                return true;
            }
            Intent intent = new Intent(this, BackupActivity.class);
            startActivityForResult(intent, Constants.REQUEST_CODE);
            // 新Activity加载动画
            overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
        } else if (id == R.id.action_restore) {
            // 从云端恢复
            if (Constants.UNLOGIN_UID.equals(App.CURRENT_UID)) {
                // 用户没有登录，不能使用恢复功能
                App.getInstance().showToast("请先登录。", Toast.LENGTH_LONG);
                return true;
            }
            Intent intent = new Intent(this, RestoreActivity.class);
            startActivityForResult(intent, Constants.REQUEST_CODE);
            // 新Activity加载动画
            overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
        } else if (id == R.id.action_share_marks) {
            // 分享标记
            MobclickAgent.onEvent(this, Constants.UmengStatistics.SHAER_MARK);
            PermissionManager.grantPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (Constants.UNLOGIN_UID.equals(App.CURRENT_UID)) {
                // 用户没有登录，不能使用分享标记功能
                App.getInstance().showToast("请先登录。", Toast.LENGTH_LONG);
                return true;
            }
            PermissionManager.grantPermission(CalendarActivity.this, Manifest.permission.CAMERA);
            final ActionSheetDialog dialog = new ActionSheetDialog(this, getResources().getStringArray(R.array.share_mark_option), null);
            dialog.title("选择操作")//
                    .titleTextSize_SP(14.5f)//
                    .show();
            dialog.setOnOperItemClickL(new OnOperItemClickL() {
                @Override
                public void onOperItemClick(AdapterView<?> parent, View view, int which, long id) {
                    if (which == 0) {
                        // 分享我的标记
                        shareMyMarks();
                    } else if (which == 1) {
                        // 复制他(她)的标记
                        // 显示提示信息
                        AlertDialog.Builder builder1 = App.getAlertDialogBuilder(CalendarActivity.this);
                        builder1.setTitle("提醒");
                        int conflict = Integer.parseInt(App.getInstance().getSetting(Constants.SHARE_CONFLICT, "0"));
                        App.SHARE_CONFLICT = conflict;
                        if (conflict == 0) {
                            // 覆盖已有标记
                            builder1.setMessage("复制过程中已有标记和新标记发生冲突时，应用将自动覆盖已有标记。如果您不想覆盖，请到设置页面进行相关设置。如果当日标记数目已达" + Constants.DAY_MAX_MARK_NUM + "个时，应用将不会再向该日期添加新的标记。");
                        } else if (conflict == 1) {
                            // 忽略新标记
                            builder1.setMessage("复制过程中已有标记和新标记发生冲突时，应用将自动忽略新标记以保护已有标记。如果您不想忽略新标记，请到设置页面进行相关设置。如果当日标记数目已达" + Constants.DAY_MAX_MARK_NUM + "个时，应用将不会再向该日期添加新的标记。");
                        }
                        builder1.setPositiveButton("开始复制", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 打开CaptureActivity，获取二维码扫描内容
                                Intent intent = new Intent(CalendarActivity.this, CaptureActivity.class);
                                startActivityForResult(intent, Constants.REQUEST_CODE);
                                overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
                            }
                        });
                        builder1.setNegativeButton("暂不复制", null);
                        builder1.create().show();
                    }
                    dialog.dismiss();
                }
            });

        } else if (id == R.id.action_about) {
            // 打开关于页面
            Intent intent3 = new Intent(this, AboutActivity.class);
            startActivity(intent3);
            // 新Activity加载动画
            overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
        } else if (id == R.id.action_rating) {
            // 为应用评分
            AlertDialog.Builder builder = App.getAlertDialogBuilder(this);
            builder.setTitle("为应用评分");
            builder.setMessage("如果你喜欢这个应用，那就请给我一个五星好评吧(ฅ>ω<*ฅ)");
            builder.setPositiveButton("去评分", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        Uri uri = Uri.parse("market://details?id=" + getPackageName());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        App.getInstance().showToast("您没有安装应用市场类软件，无法进行评分。");
                    }
                }
            });
            builder.setNegativeButton("再用用看", null);
            builder.create().show();
        } else if (id == R.id.action_share) {
            // 分享应用
            startActivity(new Intent(CalendarActivity.this, ShareActivity.class));
            overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
        } else if (id == R.id.action_regist) {
            // 打开用户注册页面
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivityForResult(intent, Constants.REQUEST_CODE);
            overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
        } else if (id == R.id.action_logout) {
            // 退出登录
            BmobUser.logOut();   //清除缓存用户对象
            App.CURRENT_UID = Constants.UNLOGIN_UID;
            // 现在的currentUser是null了
//            BmobUser currentUser = BmobUser.getCurrentUser();
//            System.out.println(currentUser);
            refreshUserInfo();
        } else if (id == R.id.action_settings) {
            // 设置
            startActivityForResult(new Intent(CalendarActivity.this, SettingsActivity.class), Constants.REQUEST_CODE);
            overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 复制他(她)的标记
     *
     * @param text
     */
    private void copyHisMarks(String text) {
        String uid = "";   // 解密后的UID
        String username = "";   // 解密后的用户名
        String dbBackupUrl = "";   // 解密后的数据库备份地址
        String info;
        // 解密内容
        try {
            info = DES.decrypt(text);
        } catch (Exception e) {
            AlertDialog.Builder builder = App.getAlertDialogBuilder(CalendarActivity.this);
            builder.setTitle("扫描结果");
            builder.setMessage(text);
            builder.create().show();
            return;
        }

        if (TextUtils.isEmpty(info)) {
            AlertDialog.Builder builder = App.getAlertDialogBuilder(CalendarActivity.this);
            builder.setTitle("扫描结果");
            builder.setMessage(text);
            builder.create().show();
            return;
        }
        try {
            JSONObject json = new JSONObject(info);
            uid = json.getString("uid");
            username = json.getString("username");
            dbBackupUrl = json.getString("dbBackupUrl");
        } catch (JSONException e) {
            return;
        }

        BmobFile bmobfile = new BmobFile("temp.db", "", dbBackupUrl);
        FileDownloader downloader = new FileDownloader(this, calendarAdapter);
        downloader.downloadDBFile(uid, username, bmobfile); // 下载数据库文件
    }

    /**
     * 分享我的标记
     */
    private void shareMyMarks() {
        App.getInstance().showToast("正在查询云端数据……");
        // 查看云端的数据库的备份路径
        BmobQuery<CircleUser> query = new BmobQuery<CircleUser>();
        query.getObject(App.currentUser.getObjectId(), new QueryListener<CircleUser>() {

            @Override
            public void done(CircleUser object, BmobException e) {
                if (e == null) {
                    String dbBackupUrl = object.getDbBackupUrl();
                    if ("".equals(dbBackupUrl)) {
                        // 云端无备份
                        App.getInstance().showToast("您没有备份过标记，无法分享。", Toast.LENGTH_LONG);
                    } else {
                        // 生成二维码
                        boolean rlt = QRCodeUtils.generateQRCode(CalendarActivity.this,
                                App.currentUser.getUid(), App.currentUser.getUsername(), dbBackupUrl);
                        if (rlt) {
                            // 生成成功
                            AlertDialog.Builder builder = App.getAlertDialogBuilder(CalendarActivity.this);
                            View view = getLayoutInflater().inflate(R.layout.dialog_qrcode, null);
                            ImageView ivQR = (ImageView) view.findViewById(R.id.ivQR);
                            ivQR.setImageBitmap(FileUtils.convertToBitmap(Constants.QR_FILE_NAME));
                            // 长按将二维码保存到Pictures文件夹下
                            ivQR.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    try {
                                        File sourceFile = new File(Constants.QR_FILE_NAME);
                                        File targetFile = new File(Constants.PICTURES_DIR + File.separator + "Share_Marks.png");
                                        FileUtils.copyFile(sourceFile, targetFile);
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }
                                    App.getInstance().showToast("已将二维码保存至“Pictures”文件夹下。");
                                    return false;
                                }
                            });
                            builder.setView(view);
                            builder.create().show();
                        } else {
                            showErrorDialog(CalendarActivity.this, "很抱歉，生成二维码失败。", false);
                        }
                    }
                } else {
                    showErrorDialog(CalendarActivity.this, "很抱歉，生成二维码失败，\n" +
                            ErrorCodes.errorMsg.get(e.getErrorCode()), false);
                }
            }

        });
    }

    /**
     * 跳转到登录页面
     */
    public void jumpToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, Constants.REQUEST_CODE);
        overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
    }

}
