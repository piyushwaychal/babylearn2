package com.wingedstone.babylearn;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.sina.weibo.sdk.WeiboSDK;
import com.sina.weibo.sdk.api.BaseResponse;
import com.sina.weibo.sdk.api.IWeiboAPI;
import com.sina.weibo.sdk.api.IWeiboHandler;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;

public class ShareKit implements IWeiboHandler.Response, 
	IWXAPIEventHandler{
	
	private IWeiboAPI weibo_api = null;	
	private IWXAPI wechat_api = null;
	private Context context;
	
	public ShareKit(Context c) {
		context = c;
		init();
	}
	
	public void init() {
		// share initialize
		weibo_api = WeiboSDK.createWeiboAPI(context, Configures.weibo_appkey);	
		wechat_api = WXAPIFactory.createWXAPI(context, Configures.wechat_appkey, true);
		wechat_api.registerApp(Configures.wechat_appkey);
		
	}
	
	public void handleCallbackIntent(Intent intent) {
		wechat_api.handleIntent(intent, this);
		if (intent.getAction() == "com.sina.weibo.sdk.action.ACTION_SDK_REQ_ACTIVITY") {
			weibo_api.responseListener(intent, this);
		} 
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

	@Override
	public void onResponse(BaseResponse baseResp) {
		int result = 0;
	   switch (baseResp.errCode) {
        case com.sina.weibo.sdk.constant.Constants.ErrorCode.ERR_OK:
        	result = R.string.errcode_success;
            break;
        case com.sina.weibo.sdk.constant.Constants.ErrorCode.ERR_FAIL:
        	result = R.string.errcode_unknown;
            break;
        }
		Toast.makeText(context, result, Toast.LENGTH_LONG).show();	   
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

}
