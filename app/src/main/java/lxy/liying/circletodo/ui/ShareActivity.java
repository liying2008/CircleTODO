package lxy.liying.circletodo.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;

import lxy.liying.circletodo.R;
import lxy.liying.circletodo.app.App;
import lxy.liying.circletodo.utils.Constants;
import lxy.liying.circletodo.utils.WXUtil;

/**
 * 分享应用
 */
public class ShareActivity extends BaseActivity implements  IWeiboHandler.Response {

    private IWXAPI api;
    private Tencent mTencent;
    /** 微博微博分享接口实例 */
    private IWeiboShareAPI mWeiboShareAPI = null;
    private int mShareType = SHARE_CLIENT;
    public static final int SHARE_CLIENT = 1;
    public static final int SHARE_ALL_IN_ONE = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        MobclickAgent.onEvent(this, Constants.UmengStatistics.SHARE_APP);
        api = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID);
        mTencent = Tencent.createInstance(Constants.QQ_APP_ID, this);

        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Constants.WB_APP_KEY);
        mWeiboShareAPI.registerApp();  //  将应用注册到微博客户端
        // 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
        // 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
        // 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
        // 失败返回 false，不调用上述回调
        if (savedInstanceState != null) {
            mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
        }
    }

    /**
     * 初始化界面
     */
    private void initView() {
        setContentView(R.layout.activity_share);
    }

    /**
     * 分享到微博
     * @param view
     */
    public void shareToWeibo(View view) {
        if (mShareType == SHARE_CLIENT) {
            if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
                int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
                if (supportApi >= 10351 /*ApiUtils.BUILD_INT_VER_2_2*/) {
                    sendMultiMessage(true, true, true);
                } else {
                    sendSingleMessage(true, true, true);
                }
            } else {
                App.getInstance().showToast(R.string.weibosdk_demo_not_support_api_hint);
            }
        }
        else if (mShareType == SHARE_ALL_IN_ONE) {
            // 暂不支持网页登录分享
            App.getInstance().showToast(R.string.weibosdk_demo_not_support);
        }
        finish();
    }

    private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        textObject.text = getString(R.string.share_description);
        return textObject;
    }
    private ImageObject getImageObj() {
        ImageObject imageObject = new ImageObject();
        // 设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        imageObject.setImageObject(bitmap);
        return imageObject;
    }
    /**
     * 创建多媒体（网页）消息对象。
     *
     * @return 多媒体（网页）消息对象。
     */
    private WebpageObject getWebpageObj() {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = getString(R.string.share_title);
        mediaObject.description = getString(R.string.share_description);

        Bitmap  bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        // 设置 Bitmap 类型的图片到视频对象里         设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        mediaObject.setThumbImage(bitmap);
        mediaObject.actionUrl = getString(R.string.share_link);
        mediaObject.defaultText = getString(R.string.share_app_name);
        return mediaObject;
    }

    /**
     /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 注意：当 {@link IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时，支持同时分享多条消息，
     * 同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
     *
     * @param hasText    分享的内容是否有文本
     * @param hasImage   分享的内容是否有图片
     * @param hasWebpage 分享的内容是否有网页
     */
    private void sendMultiMessage(boolean hasText, boolean hasImage, boolean hasWebpage) {
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();//初始化微博的分享消息
        if (hasText) {
            weiboMessage.textObject = getTextObj();
        }
        if (hasImage) {
            weiboMessage.imageObject = getImageObj();
        }
        if (hasWebpage) {
            weiboMessage.mediaObject = getWebpageObj();
        }
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;

        // 3. 发送请求消息到微博，唤起微博分享界面
        if (mShareType == SHARE_CLIENT) {
            mWeiboShareAPI.sendRequest(this, request);
        }
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 当{@link IWeiboShareAPI#getWeiboAppSupportAPI()} < 10351 时，只支持分享单条消息，即
     * 文本、图片、网页、音乐、视频中的一种，不支持Voice消息。
     * @param hasText       分享的内容是否有文本
     * @param hasImage      分享的内容是否有图片
     * @param hasWebpage    分享的内容是否有网页
     */
    private void sendSingleMessage(boolean hasText, boolean hasImage, boolean hasWebpage) {

        // 1. 初始化微博的分享消息
        // 用户可以分享文本、图片、网页、音乐、视频中的一种
        WeiboMessage weiboMessage = new WeiboMessage();
        if (hasText) {
            weiboMessage.mediaObject = getTextObj();
        }
        if (hasImage) {
            weiboMessage.mediaObject = getImageObj();
        }
        if (hasWebpage) {
            weiboMessage.mediaObject = getWebpageObj();
        }
        // 2. 初始化从第三方到微博的消息请求
        SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.message = weiboMessage;

        // 3. 发送请求消息到微博，唤起微博分享界面
        mWeiboShareAPI.sendRequest(this, request);
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mWeiboShareAPI.handleWeiboResponse(intent,  this);  //当前应用唤起微博分享后，返回当前应用
    }

    @Override
    public void onResponse(BaseResponse baseResponse) {
        if(baseResponse != null) {
            switch (baseResponse.errCode) {
                case WBConstants.ErrorCode.ERR_OK:
                    App.getInstance().showToast(R.string.toast_share_success);
                    break;
                case WBConstants.ErrorCode.ERR_CANCEL:
                    App.getInstance().showToast(R.string.toast_share_canceled);
                    break;
                case WBConstants.ErrorCode.ERR_FAIL:
                    App.getInstance().showToast(getString(R.string.toast_share_failed) + "Error Message: " + baseResponse.errMsg);
                    break;
            }
        }
    }

    /**
     * 分享到QQ
     * @param view
     */
    public void shareToQQ(View view) {
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, getString(R.string.share_title));
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, getString(R.string.share_description));
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, getString(R.string.share_link));
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, getString(R.string.share_to_qq_image_url));
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, getString(R.string.app_name));
//        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, 0);
        mTencent.shareToQQ(this, params, new BaseUiListener());
        finish();
    }


    private class BaseUiListener implements IUiListener {
        @Override
        public void onComplete(Object o) {
            App.getInstance().showToast(R.string.toast_share_success);
        }

        @Override
        public void onError(UiError uiError) {
            App.getInstance().showToast(R.string.toast_share_failed);
        }

        @Override
        public void onCancel() {
            App.getInstance().showToast(R.string.toast_share_canceled);
        }

    }
    /**
     * 分享到微信
     * @param view
     */
    public void shareToWeixin(View view) {
        shareToWeiXinOrPYQ(SendMessageToWX.Req.WXSceneSession);
    }

    /**
     * 分享到朋友圈
     * @param view
     */
    public void shareToPYQ(View view) {
        shareToWeiXinOrPYQ(SendMessageToWX.Req.WXSceneTimeline);
    }

    /**
     * 分享到微信或朋友圈
     * 注意：图片大小小于32k，文字（分享时传递的简介内容）字要少。
     * @param scene
     */
    private void shareToWeiXinOrPYQ(int scene) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = getString(R.string.share_link);
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = getString(R.string.share_title);
        msg.description = getString(R.string.share_description);
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        msg.thumbData = WXUtil.bmpToByteArray(thumb, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = scene;
        api.sendReq(req);
        finish();
    }
    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
