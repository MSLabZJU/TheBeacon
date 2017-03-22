package com.example.thebeacon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootRestartReceiver extends BroadcastReceiver {
	
	private final String ACTION = "android.intent.action.BOOT_COMPLETED";
	 @Override
	 public void onReceive(Context context, Intent intent)
	 {
	  // TODO Auto-generated method stub
	   
	  if (intent.getAction().equals(ACTION));
	  {
	   Intent intent2 = new Intent(context, MainActivity.class);
	   intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	   context.startActivity(intent2);
	   Log.d("DEBUG", "开机自动服务自动启动...");
	    
	   //Intent intentService = new Intent();
	   //intentService.setClass(context, MyService.class);
	   //context.startService(intentService);
	    
	  }
	 
	 }

}
