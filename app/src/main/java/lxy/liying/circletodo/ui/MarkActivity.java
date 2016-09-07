package lxy.liying.circletodo.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import lxy.liying.circletodo.R;
import lxy.liying.circletodo.adapter.SelectedColorsListAdapter;
import lxy.liying.circletodo.app.App;
import lxy.liying.circletodo.domain.SelectedColor;
import lxy.liying.circletodo.utils.Constants;

public class MarkActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private CircleImageView ivColor1, ivColor2, ivColor3, ivColor4, ivColor5, ivColor6, ivColor7;
    private CircleImageView ivColor8, ivColor9, ivColor10, ivColor11, ivColor12, ivColor13, ivColor14;
    private ListView lvSelected;
    private SelectedColorsListAdapter adapter;
    private TextView tvMsgNone;
    private static MarkActivity markActivity;
    private Map<Integer, CircleImageView> color2ivMap = new HashMap<>(14);
    private Map<Integer, CircleImageView> color2ivMapDia = new HashMap<>(14);
    private List<Integer> selectedIds = new ArrayList<>(14);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView(); // 初始化View
        MobclickAgent.onEvent(this, Constants.UmengStatistics.SET_MARK);
        markActivity = this;
        adapter = new SelectedColorsListAdapter(this, App.mColorLists);
    }

    /**
     * 得到本Activity的实例
     *
     * @return
     */
    public static MarkActivity getInstance() {
        return markActivity;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (App.mColorLists != null) {
            // 有已选颜色
            tvMsgNone.setVisibility(View.GONE); // 设置“选择颜色为空”信息不可见
        }
        lvSelected.setAdapter(adapter);
        lvSelected.setOnItemClickListener(this);
    }

    /***
     * 初始化View
     */
    private void initView() {
        setContentView(R.layout.activity_mark);

        actionBarNavigator();

        ivColor1 = (CircleImageView) findViewById(R.id.ivColor1);
        ivColor2 = (CircleImageView) findViewById(R.id.ivColor2);
        ivColor3 = (CircleImageView) findViewById(R.id.ivColor3);
        ivColor4 = (CircleImageView) findViewById(R.id.ivColor4);
        ivColor5 = (CircleImageView) findViewById(R.id.ivColor5);
        ivColor6 = (CircleImageView) findViewById(R.id.ivColor6);
        ivColor7 = (CircleImageView) findViewById(R.id.ivColor7);
        ivColor8 = (CircleImageView) findViewById(R.id.ivColor8);
        ivColor9 = (CircleImageView) findViewById(R.id.ivColor9);
        ivColor10 = (CircleImageView) findViewById(R.id.ivColor10);
        ivColor11 = (CircleImageView) findViewById(R.id.ivColor11);
        ivColor12 = (CircleImageView) findViewById(R.id.ivColor12);
        ivColor13 = (CircleImageView) findViewById(R.id.ivColor13);
        ivColor14 = (CircleImageView) findViewById(R.id.ivColor14);
        lvSelected = (ListView) findViewById(R.id.lvSelected);

        ivColor1.setOnClickListener(this);
        ivColor2.setOnClickListener(this);
        ivColor3.setOnClickListener(this);
        ivColor4.setOnClickListener(this);
        ivColor5.setOnClickListener(this);
        ivColor6.setOnClickListener(this);
        ivColor7.setOnClickListener(this);
        ivColor8.setOnClickListener(this);
        ivColor9.setOnClickListener(this);
        ivColor10.setOnClickListener(this);
        ivColor11.setOnClickListener(this);
        ivColor12.setOnClickListener(this);
        ivColor13.setOnClickListener(this);
        ivColor14.setOnClickListener(this);

        color2ivMap.put(R.color.color_1, ivColor1);
        color2ivMap.put(R.color.color_2, ivColor2);
        color2ivMap.put(R.color.color_3, ivColor3);
        color2ivMap.put(R.color.color_4, ivColor4);
        color2ivMap.put(R.color.color_5, ivColor5);
        color2ivMap.put(R.color.color_6, ivColor6);
        color2ivMap.put(R.color.color_7, ivColor7);
        color2ivMap.put(R.color.color_8, ivColor8);
        color2ivMap.put(R.color.color_9, ivColor9);
        color2ivMap.put(R.color.color_10, ivColor10);
        color2ivMap.put(R.color.color_11, ivColor11);
        color2ivMap.put(R.color.color_12, ivColor12);
        color2ivMap.put(R.color.color_13, ivColor13);
        color2ivMap.put(R.color.color_14, ivColor14);

        tvMsgNone = (TextView) findViewById(R.id.tvMsgNone);
        // 已选择的colorId
        if (App.mColorLists != null && App.mColorLists.size() > 0) {
            for (SelectedColor sc : App.mColorLists) {
                selectedIds.add(sc.getColor_id());
            }
        }
        setColorVisibility();   // 设置每一个CircleImageView的可见性
    }

    /**
     * 设置每一个CircleImageView的可见性
     */
    private void setColorVisibility() {
        // 已选择的颜色不再显示在预设颜色里
        // 把id出现在selectedIds中的颜色隐藏
        for (int id : selectedIds) {
            color2ivMap.get(id).setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 显示添加标记对话框
     *
     * @param color colorId
     */
    private void showDialogF(final int color) {
//        if (null != App.scService.getColorEvent(color)) {
//            // 数据库中已有该颜色
//            App.getInstance().showToast("您已经添加了该颜色，无法重复添加");
//            return;
//        }

        View viewRoot = getLayoutInflater().inflate(R.layout.dialog_select, null);
        final EditText etEvent = (EditText) viewRoot.findViewById(R.id.etEvent);
        etEvent.requestFocus();
        CircleImageView ivCurrent = (CircleImageView) viewRoot.findViewById(R.id.ivCurrent);
        Button btnClear = (Button) viewRoot.findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etEvent.setText("");
            }
        });
        ivCurrent.setImageResource(color);
        etEvent.setTextColor(getResources().getColor(color));

        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(viewRoot);
        builder.setTitle("请输入标记名称");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String s = etEvent.getText().toString();
                if (TextUtils.isEmpty(s)) {
                    App.getInstance().showToast("标记名称不可为空。");
                    return;
                }
                App.scService.addColor(new SelectedColor(color, s), false);
                adapter.setData(App.getSelectedColors());
                tvMsgNone.setVisibility(View.GONE);  // 设置“选择颜色为空”信息不可见
                adapter.notifyDataSetChanged();
                // 设置该颜色在预设颜色里不可见
                color2ivMap.get(color).setVisibility(View.INVISIBLE);
                selectedIds.add(color);
                setResult(Constants.REFRESH_REMAIN_LIST);
            }
        });
        AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        // 不允许通过点击Dialog外部关闭Dialog
        dialog.setCanceledOnTouchOutside(false);
        // 解决不自动弹出输入法面板的问题
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivColor1:
                showDialogF(R.color.color_1);
                break;
            case R.id.ivColor2:
                showDialogF(R.color.color_2);
                break;
            case R.id.ivColor3:
                showDialogF(R.color.color_3);
                break;
            case R.id.ivColor4:
                showDialogF(R.color.color_4);
                break;
            case R.id.ivColor5:
                showDialogF(R.color.color_5);
                break;
            case R.id.ivColor6:
                showDialogF(R.color.color_6);
                break;
            case R.id.ivColor7:
                showDialogF(R.color.color_7);
                break;
            case R.id.ivColor8:
                showDialogF(R.color.color_8);
                break;
            case R.id.ivColor9:
                showDialogF(R.color.color_9);
                break;
            case R.id.ivColor10:
                showDialogF(R.color.color_10);
                break;
            case R.id.ivColor11:
                showDialogF(R.color.color_11);
                break;
            case R.id.ivColor12:
                showDialogF(R.color.color_12);
                break;
            case R.id.ivColor13:
                showDialogF(R.color.color_13);
                break;
            case R.id.ivColor14:
                showDialogF(R.color.color_14);
                break;
            default:
                break;
        }
    }

    /**
     * 删除一个颜色
     *
     * @param colorId
     */
    public void delColor(final int colorId) {
        AlertDialog.Builder builder = App.getAlertDialogBuilder(MarkActivity.this);
        builder.setTitle("删除提醒").setMessage("删除该颜色会将其对应的标记一并删除。您确定要删除该颜色吗？");
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                App.scService.removeColor(colorId); // 删除颜色
                App.deService.removeMarks(colorId); // 删除日期中所有选择的该颜色
                App.getInstance().showToast("颜色已删除。");
                // 通知CalendarActivity视图更新或RemainListActivity视图更新
                setResult(Constants.REFRESH_GRID_AND_MARK_MODE);
                List<SelectedColor> newSelectedColors = App.getSelectedColors();
                // 删除最后一个已选择的颜色后，newSelectedColors为空
                if (null == newSelectedColors) {
                    newSelectedColors = new ArrayList<SelectedColor>(1);
                    tvMsgNone.setVisibility(View.VISIBLE);  // 设置“选择颜色为空”信息可见
                }
                adapter.setData(newSelectedColors);
                adapter.notifyDataSetChanged();
                // 设置该颜色在预设颜色里可见
                color2ivMap.get(colorId).setVisibility(View.VISIBLE);
                selectedIds.remove(Integer.valueOf(colorId));
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        final ActionSheetDialog dialog = new ActionSheetDialog(this, getResources().getStringArray(R.array.sel_color_option), null);
        dialog.title("选择操作")//
                .titleTextSize_SP(14.5f)//
                .show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int which, long id) {
                if (which == 0) {
                    // 更改标记名称
                    changeMark(position);
                } else if (which == 1) {
                    // 更换颜色
                    changeColor(position);
                }
                dialog.dismiss();
            }
        });
    }

    /**
     * 更换颜色
     *
     * @param position
     */
    private void changeColor(int position) {
        if (selectedIds.size() == 14) {
            // 已无预设颜色
            App.getInstance().showToast("没有空余颜色可供选择。");
            return;
        }
        final int colorId = App.mColorLists.get(position).getColor_id();
        View viewRoot = getLayoutInflater().inflate(R.layout.layout_preset_color_list, null);
        viewRoot.setPadding(20, 10, 20, 10);
        AlertDialog.Builder builder = App.getAlertDialogBuilder(this);
        builder.setTitle("请选择一种颜色");
        builder.setView(viewRoot);
        CircleImageView ivColor1 = (CircleImageView) viewRoot.findViewById(R.id.ivColor1);
        CircleImageView ivColor2 = (CircleImageView) viewRoot.findViewById(R.id.ivColor2);
        CircleImageView ivColor3 = (CircleImageView) viewRoot.findViewById(R.id.ivColor3);
        CircleImageView ivColor4 = (CircleImageView) viewRoot.findViewById(R.id.ivColor4);
        CircleImageView ivColor5 = (CircleImageView) viewRoot.findViewById(R.id.ivColor5);
        CircleImageView ivColor6 = (CircleImageView) viewRoot.findViewById(R.id.ivColor6);
        CircleImageView ivColor7 = (CircleImageView) viewRoot.findViewById(R.id.ivColor7);
        CircleImageView ivColor8 = (CircleImageView) viewRoot.findViewById(R.id.ivColor8);
        CircleImageView ivColor9 = (CircleImageView) viewRoot.findViewById(R.id.ivColor9);
        CircleImageView ivColor10 = (CircleImageView) viewRoot.findViewById(R.id.ivColor10);
        CircleImageView ivColor11 = (CircleImageView) viewRoot.findViewById(R.id.ivColor11);
        CircleImageView ivColor12 = (CircleImageView) viewRoot.findViewById(R.id.ivColor12);
        CircleImageView ivColor13 = (CircleImageView) viewRoot.findViewById(R.id.ivColor13);
        CircleImageView ivColor14 = (CircleImageView) viewRoot.findViewById(R.id.ivColor14);

        AlertDialog dialog = builder.create();

        ChangeColorListener changeColorListener = new ChangeColorListener(dialog, colorId);
        ivColor1.setOnClickListener(changeColorListener);
        ivColor2.setOnClickListener(changeColorListener);
        ivColor3.setOnClickListener(changeColorListener);
        ivColor4.setOnClickListener(changeColorListener);
        ivColor5.setOnClickListener(changeColorListener);
        ivColor6.setOnClickListener(changeColorListener);
        ivColor7.setOnClickListener(changeColorListener);
        ivColor8.setOnClickListener(changeColorListener);
        ivColor9.setOnClickListener(changeColorListener);
        ivColor10.setOnClickListener(changeColorListener);
        ivColor11.setOnClickListener(changeColorListener);
        ivColor12.setOnClickListener(changeColorListener);
        ivColor13.setOnClickListener(changeColorListener);
        ivColor14.setOnClickListener(changeColorListener);

        color2ivMapDia.put(R.color.color_1, ivColor1);
        color2ivMapDia.put(R.color.color_2, ivColor2);
        color2ivMapDia.put(R.color.color_3, ivColor3);
        color2ivMapDia.put(R.color.color_4, ivColor4);
        color2ivMapDia.put(R.color.color_5, ivColor5);
        color2ivMapDia.put(R.color.color_6, ivColor6);
        color2ivMapDia.put(R.color.color_7, ivColor7);
        color2ivMapDia.put(R.color.color_8, ivColor8);
        color2ivMapDia.put(R.color.color_9, ivColor9);
        color2ivMapDia.put(R.color.color_10, ivColor10);
        color2ivMapDia.put(R.color.color_11, ivColor11);
        color2ivMapDia.put(R.color.color_12, ivColor12);
        color2ivMapDia.put(R.color.color_13, ivColor13);
        color2ivMapDia.put(R.color.color_14, ivColor14);

        // 隐藏已经选过的颜色
        for (int id : selectedIds) {
            color2ivMapDia.get(id).setVisibility(View.INVISIBLE);
        }

        dialog.show();
    }

    /**
     * 更改标记名称
     *
     * @param position
     */
    private void changeMark(int position) {
        final int colorId = App.mColorLists.get(position).getColor_id();
        View viewRoot = getLayoutInflater().inflate(R.layout.dialog_select, null);
        final EditText etEvent = (EditText) viewRoot.findViewById(R.id.etEvent);
        CircleImageView ivCurrent = (CircleImageView) viewRoot.findViewById(R.id.ivCurrent);
        Button btnClear = (Button) viewRoot.findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etEvent.setText("");
            }
        });
        ivCurrent.setImageResource(colorId);

        AlertDialog.Builder builder = App.getAlertDialogBuilder(MarkActivity.this);
        builder.setView(viewRoot);
        builder.setTitle("更改标记名称");
        etEvent.setTextColor(getResources().getColor(colorId));
        String oldEvent = App.scService.getColorEvent(colorId);
        etEvent.setText(oldEvent);  // 显示原标记名称
        etEvent.setSelection(oldEvent.length());    // 设置光标在末尾
        builder.setPositiveButton("更改", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                App.scService.updateEvent(colorId, etEvent.getText().toString());
                App.getInstance().showToast("标记名称已修改。");
                adapter.setData(App.getSelectedColors());
                adapter.notifyDataSetChanged();
                setResult(Constants.REFRESH_REMAIN_LIST);
            }
        });
        AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        // 不允许通过点击Dialog外部关闭Dialog
        dialog.setCanceledOnTouchOutside(false);
        // 解决不自动弹出输入法面板的问题
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    /**
     * 更换颜色的点击事件监听器
     */
    class ChangeColorListener implements View.OnClickListener {

        private int oldColorId; // 要更换的颜色
        private Dialog dialog;

        public ChangeColorListener(Dialog dialog, int oldColorId) {
            this.oldColorId = oldColorId;
            this.dialog = dialog;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ivColor1:
                    changeColorTask(oldColorId, R.color.color_1);
                    break;
                case R.id.ivColor2:
                    changeColorTask(oldColorId, R.color.color_2);
                    break;
                case R.id.ivColor3:
                    changeColorTask(oldColorId, R.color.color_3);
                    break;
                case R.id.ivColor4:
                    changeColorTask(oldColorId, R.color.color_4);
                    break;
                case R.id.ivColor5:
                    changeColorTask(oldColorId, R.color.color_5);
                    break;
                case R.id.ivColor6:
                    changeColorTask(oldColorId, R.color.color_6);
                    break;
                case R.id.ivColor7:
                    changeColorTask(oldColorId, R.color.color_7);
                    break;
                case R.id.ivColor8:
                    changeColorTask(oldColorId, R.color.color_8);
                    break;
                case R.id.ivColor9:
                    changeColorTask(oldColorId, R.color.color_9);
                    break;
                case R.id.ivColor10:
                    changeColorTask(oldColorId, R.color.color_10);
                    break;
                case R.id.ivColor11:
                    changeColorTask(oldColorId, R.color.color_11);
                    break;
                case R.id.ivColor12:
                    changeColorTask(oldColorId, R.color.color_12);
                    break;
                case R.id.ivColor13:
                    changeColorTask(oldColorId, R.color.color_13);
                    break;
                case R.id.ivColor14:
                    changeColorTask(oldColorId, R.color.color_14);
                    break;
                default:
                    break;
            }
            dialog.dismiss();
        }
    }

    /**
     * 更新数据库，执行更换颜色操作
     *
     * @param oldColorId 被更换颜色
     * @param colorId    更换后的颜色
     */
    private void changeColorTask(int oldColorId, int colorId) {
        App.scService.updateColorId(oldColorId, colorId);
        App.deService.updateColorId(oldColorId, colorId);
        App.getInstance().showToast("颜色已更换。");
        setResult(Constants.REFRESH_REMAIN_LIST);
        color2ivMap.get(oldColorId).setVisibility(View.VISIBLE);
        color2ivMap.get(colorId).setVisibility(View.INVISIBLE);
        selectedIds.remove(Integer.valueOf(oldColorId));
        selectedIds.add(colorId);
        // 刷新已选择颜色列表
        App.getSelectedColors();
        adapter.setData(App.mColorLists);
        adapter.notifyDataSetChanged();
    }
}
