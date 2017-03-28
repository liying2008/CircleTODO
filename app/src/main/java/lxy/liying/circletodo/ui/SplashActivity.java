package lxy.liying.circletodo.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import java.security.MessageDigest;

import lxy.liying.circletodo.R;
import lxy.liying.circletodo.app.App;
import lxy.liying.circletodo.utils.Constants;

public class SplashActivity extends BaseActivity {

    // CircleTODO的签名的MD5值
    private static final String MD5_SIGNATURE = "e2f32944095606a12389652db76b44e9";
    private static PackageInfo packageInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        /** 设置是否对日志信息进行加密, 默认false(不加密). */
        MobclickAgent.enableEncrypt(true);//6.0.0版本及以后
        setContentView(R.layout.activity_splash);
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 检查数字签名，防止二次打包
                if (checkSignature()) {
                    // 签名正确
                    saveVersion();  // 保存版本信息
                    loadSettings(); // 加载设置
                    jump();
                } else {
                    // 签名错误
                    App.getInstance().showToast("该应用程序已被篡改，为了保证安全性，请到应用商店重新下载。", Toast.LENGTH_LONG);
                    finish();
                    // 调试使用
//                    saveVersion();  // 保存版本信息
//                    loadSettings(); // 加载设置
//                    jump();
                }
            }
        }, 600);   // 停留时间600ms
    }
    /**
     * 检查数字签名
     * @return true:数字签名正确，数字签名错误
     */
    private boolean checkSignature() {
        Signature[] signatures = packageInfo.signatures;
        Signature sign = signatures[0];
        String md5Signature = getMD5String(sign.toByteArray());
//        System.out.println("MD5 : " + md5Signature);
        if (md5Signature != null && MD5_SIGNATURE.equals(md5Signature)) {
            return true;
        }
        return false;
    }

    private static String getMD5String(byte[] paramArrayOfByte) {
        // ascii表对应的数字和字符的编码
        char[] asciiTable = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };
        try {
            MessageDigest md5MessageDigest = MessageDigest.getInstance("MD5");
            md5MessageDigest.update(paramArrayOfByte);//
            byte[] tempByte = md5MessageDigest.digest();
            int i = tempByte.length;
            char[] tempChar = new char[i * 2];
            int j = 0;
            int k = 0;
            while (true) { // 将二进制数组转换成字符串
                if (j >= i) {
                    return new String(tempChar);
                }
                int m  = tempByte[j];
                int n  = k + 1;
                tempChar[k] = asciiTable[(0xF & m >>> 4)];
                k = n + 1;
                tempChar[n] = asciiTable[(m & 0xF)];
                j++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加载应用程序的一些设置
     */
    private void loadSettings() {
        App.DISPLAY_MODE  = Integer.parseInt(App.getInstance().getSetting(Constants.DISPLAY_MODE, "0"));
    }

    /**
     * 跳转到CalendarActivity
     */
    private void jump() {
        startActivity(new Intent(this, CalendarActivity.class));
        overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
        finish();
    }

    /** 保存当前版本号 */
    private void saveVersion() {
        int currentVersion = App.getInstance().getVersionCode();
        String lastVersionStr = App.getInstance().getSetting(Constants.VERSION, "0");
        int lastVersion = Integer.parseInt(lastVersionStr);
        if (currentVersion > lastVersion) {
            //如果当前版本大于上次版本，保存当前版本号
            App.getInstance().putSetting(Constants.VERSION, String.valueOf(currentVersion));
        }
    }
}
