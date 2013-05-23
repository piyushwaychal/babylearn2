package com.wingedstone.babylearn.sharekit;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.wingedstone.babylearn.Configures;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CallBackReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		final IWXAPI api = WXAPIFactory.createWXAPI(context, null);
		api.registerApp(Configures.wechat_appkey);
	}

}
