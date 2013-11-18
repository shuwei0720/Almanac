package com.twm.gpsmapv2;

import android.R.string;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.Toast;

public class GpsMapV2Class {
	
	static public boolean DEF_LOG_FILE = true;
	
	// Service
	static public boolean mbIsBind = false;
//	static public ITaskBinder mITaskBinder = null;
//	static public ServiceConnection mConnection = null;
	
	static public String TAG = "GGGGG";
	
}
