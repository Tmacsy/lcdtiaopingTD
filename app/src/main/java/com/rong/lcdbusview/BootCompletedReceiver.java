package com.rong.lcdbusview;

import com.rong.lcdbusview.service.MainService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver  
{  
  
    @Override  
    public void onReceive(Context context, Intent intent)  
    {  
    	String action = intent.getAction();
    	switch (action) {
		case Intent.ACTION_BOOT_COMPLETED://开机广播
//			Intent service = new Intent(context, MainService.class);
//			context.startService(service);
			if(MainApplication.openView){
				Intent view = new Intent(context, MainActivity.class);
				view.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(view);
			}
			break;
			case Intent.ACTION_SHUTDOWN://关机广播
//				MainApplication.getInstance().setRouteStation(-1);
				Intent service1 = new Intent(context, MainService.class);
				context.stopService(service1);
				break;
		default:
			break;
		}
    }  
} 
