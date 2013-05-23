package com.wingedstone.babylearn.sharekit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.sso.SsoHandler;
import com.wingedstone.babylearn.Configures;
import com.wingedstone.babylearn.R;
import com.wingedstone.babylearn.Utils;
import com.wingedstone.babylearn.R.string;

public class ShareKit implements IWXAPIEventHandler{
	
	private Weibo weibo_api = null;	
	private SsoHandler weibo_sso_api = null;
	private IWXAPI wechat_api = null;
	private Activity context;
	
	public ShareKit(Activity c) {
		context = c;
		init();
	}
	
	public void init() {
		// share initialize
		weibo_api = Weibo.getInstance(Configures.weibo_appkey, Configures.weibo_redirect_url);
		weibo_sso_api = new SsoHandler( context, weibo_api);
		wechat_api = WXAPIFactory.createWXAPI(context, Configures.wechat_appkey, true);
		wechat_api.registerApp(Configures.wechat_appkey);
		
	}
	
	public void handleCallbackIntent(Intent intent) {
		wechat_api.handleIntent(intent, this);
	}
	
	public void handleCallbackIntent(int requestCode, int resultCode, Intent data) {
		weibo_sso_api.authorizeCallBack(requestCode, resultCode, data);
	}
	
	public void share(Bitmap bmp, int to) {
		WXImageObject imgObj = new WXImageObject(bmp);
		
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = imgObj;
		msg.description = "宝宝来画画";
		msg.title = "宝宝来画画";
		
		Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 100, 100, true);
		msg.thumbData = Utils.bmpToByteArray(thumbBmp, true); 

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("img");
		req.message = msg;
		req.scene =  SendMessageToWX.Req.WXSceneSession;
		boolean result = wechat_api.sendReq(req);
		Toast.makeText(context, String.valueOf(result) , Toast.LENGTH_SHORT).show();
	}
	
	public void authorize() {
		weibo_sso_api.authorize(new WeiboListener());
	}
	
	public void shareToWeibo(Bitmap bmp) {
		Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(context);
		if (token.isSessionValid()) {
		}
	}

	@Override
	public void onReq(BaseReq arg0) {
		// we don't handle any direct requests from wechat
		// so be it.
		
	}

	@Override
	public void onResp(BaseResp resp) {
		int result = 0;
		
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			result = R.string.errcode_success;
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = R.string.errcode_cancel;
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = R.string.errcode_deny;
			break;
		default:
			result = R.string.errcode_unknown;
			break;
		}
		
		Toast.makeText(context, result, Toast.LENGTH_LONG).show();
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
	 class WeiboListener implements WeiboAuthListener {

	        @Override
	        public void onComplete(Bundle values) {
	        	String token = values.getString("access_token");
	            String expires_in = values.getString("expires_in");
	            Oauth2AccessToken accessToken = new Oauth2AccessToken(token, expires_in);
	            if (accessToken.isSessionValid()) {
	            	AccessTokenKeeper.keepAccessToken(ShareKit.this.context,
	                        accessToken);
	                Toast.makeText(ShareKit.this.context, "认证成功", Toast.LENGTH_SHORT)
	                        .show();
	            }
	        }

	        @Override
	        public void onError(WeiboDialogError e) {
	        }

	        @Override
	        public void onCancel() {
	        }

	        @Override
	        public void onWeiboException(WeiboException e) {
	        }

	    }

}
