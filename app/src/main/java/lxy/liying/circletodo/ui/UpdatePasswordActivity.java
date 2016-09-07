package lxy.liying.circletodo.ui;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import lxy.liying.circletodo.R;
import lxy.liying.circletodo.app.App;
import lxy.liying.circletodo.utils.ErrorCodes;
import lxy.liying.circletodo.utils.StringUtils;

public class UpdatePasswordActivity extends BaseActivity {
    private EditText etCurrPass, etNewPass1, etNewPass2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        actionBarNavigator();

        etCurrPass = (EditText) findViewById(R.id.etCurrPass);
        etNewPass1 = (EditText) findViewById(R.id.etNewPass1);
        etNewPass2 = (EditText) findViewById(R.id.etNewPass2);
    }

    /**
     * 更改密码
     *
     * @param view
     */
    public void changePassword(View view) {
        String currPass = etCurrPass.getText().toString();
        String newPass1 = etNewPass1.getText().toString();
        String newPass2 = etNewPass2.getText().toString();

        if ("".equals(currPass)) {
            App.getInstance().showToast("现密码不能为空。");
            return;
        } else if ("".equals(newPass1) || "".equals(newPass2)) {
            App.getInstance().showToast("新密码不能为空。");
            return;
        } else if (!newPass1.equals(newPass2)) {
            App.getInstance().showToast("两次输入的新密码不一致。");
            return;
        }
        // 加密密码
        currPass = StringUtils.encryptToSHA1(currPass);
        newPass1 = StringUtils.encryptToSHA1(newPass1);

        System.out.println("currPass = " + currPass + ", newPass = " + newPass1);
        BmobUser.updateCurrentUserPassword(currPass, newPass1, new UpdateListener() {

            @Override
            public void done(BmobException e) {
                if (e == null) {
                    AlertDialog.Builder builder = App.getAlertDialogBuilder(UpdatePasswordActivity.this);
                    builder.setTitle("修改成功");
                    builder.setMessage("密码修改成功，可以用新密码进行登录啦。");
                    builder.setPositiveButton("知道了", null);
                    builder.create().show();
                } else {
                    showErrorDialog(UpdatePasswordActivity.this, "修改失败。\n" +
                            ErrorCodes.errorMsg.get(e.getErrorCode()), false);
                }
            }
        });
    }
}
