package lxy.liying.circletodo.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.umeng.analytics.MobclickAgent;

import lxy.liying.circletodo.R;
import lxy.liying.circletodo.app.App;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/7/28 11:51
 * 版本：1.0
 * 描述：
 * 备注：
 * =======================================================
 */
public class BaseActivity extends AppCompatActivity {
    private CompositeSubscription mCompositeSubscription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 关闭Activity时动画效果
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.push_right_in_ac, R.anim.push_right_out_ac);
    }

    // 获取点击事件
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (isHideInput(view, ev)) {
                HideSoftInput(view.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    // 判定是否需要隐藏
    private boolean isHideInput(View v, MotionEvent ev) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            return !(ev.getX() > left && ev.getX() < right && ev.getY() > top
                    && ev.getY() < bottom);
        }
        return false;
    }

    // 隐藏软键盘
    private void HideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // 点击返回图标事件
                this.finish();
                overridePendingTransition(R.anim.push_right_in_ac, R.anim.push_right_out_ac);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * ActionBar导航
     */
    protected void actionBarNavigator() {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        // 设置是否显示app的图标
        actionBar.setDisplayShowHomeEnabled(true);
        // 设置是否将app图标转变成成可点击的按钮
        actionBar.setHomeButtonEnabled(true);
    }

    /**
     * 错误提示对话框
     *
     * @param activity
     * @param errorMsg
     * @param isFinish 是否关闭Activity
     */
    protected void showErrorDialog(final BaseActivity activity, String errorMsg, final boolean isFinish) {
        AlertDialog.Builder builder = App.getAlertDialogBuilder(activity);
        builder.setTitle("出现了错误");
        builder.setMessage(errorMsg);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isFinish) {
                    activity.finish();  // 关闭Activity
                }
            }
        });
        AlertDialog dialog = builder.create();
        // 设置Dialog动画效果
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.myDialogAnim);
        dialog.show();
    }

    /**
     * 解决Subscription内存泄露问题
     *
     * @param s
     */
    protected void addSubscription(Subscription s) {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }
        this.mCompositeSubscription.add(s);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
