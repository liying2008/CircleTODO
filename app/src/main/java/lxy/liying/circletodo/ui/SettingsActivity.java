package lxy.liying.circletodo.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import lxy.liying.circletodo.R;
import lxy.liying.circletodo.app.App;
import lxy.liying.circletodo.utils.Constants;

public class SettingsActivity extends BaseActivity {

    private Spinner spStatistics, spShareConflict, spDisplayMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        actionBarNavigator();

        spStatistics = (Spinner) findViewById(R.id.spStatistics);
        spShareConflict = (Spinner) findViewById(R.id.spShareConflict);
        spDisplayMode = (Spinner) findViewById(R.id.spDisplayMode);

        loadSettings();

        spStatistics.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                App.getInstance().putSetting(Constants.STATISTICS_SCOPE, String.valueOf(position));
                App.getInstance().showToast("设置已保存。");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spShareConflict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                App.getInstance().putSetting(Constants.SHARE_CONFLICT, String.valueOf(position));
                App.getInstance().showToast("设置已保存。");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spDisplayMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                App.getInstance().putSetting(Constants.DISPLAY_MODE, String.valueOf(position));
                App.getInstance().showToast("设置已保存。");
                // 刷新“每日标记”显示方式
                setResult(Constants.DATE_MARK_MODE);
                App.DISPLAY_MODE = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * 加载用户的应用设置
     */
    private void loadSettings() {
        int scope = Integer.parseInt(App.getInstance().getSetting(Constants.STATISTICS_SCOPE, "0"));
        int conflict = Integer.parseInt(App.getInstance().getSetting(Constants.SHARE_CONFLICT, "0"));
        int displayMode = Integer.parseInt(App.getInstance().getSetting(Constants.DISPLAY_MODE, "0"));

        spStatistics.setSelection(scope, true);
        spShareConflict.setSelection(conflict, true);
        spDisplayMode.setSelection(displayMode, true);
    }
}
