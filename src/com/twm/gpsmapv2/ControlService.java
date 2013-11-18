package com.twm.gpsmapv2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;


//import com.twm.gpsmapv2.ITaskBinder;

/*
 * 		Service  �u�| Bind �@��, �G�ݨϥΤ��P�ɦW�Ψ禡�Ϲj 
 * 		1. �� Bind �� onBind �I�s Function
 * 		2. �w Bind �� aidl  �I�s Function
 */
public class ControlService extends Service{

	static public int DEF_SERVICE_MODE_GPS = 0;	// �ɮפU��
//	static public int DEF_SERVICE_MODE_PIC_DL = 1;	// ���ɤU��
	int mIntMode = DEF_SERVICE_MODE_GPS;
	
	// �ǤJ FileName
//	String mStrFileName = "";
	// �ǤJ URL
//	String mLinkURL = "";
	// �ǤJ�ɮפj�p
//	int mIntLimitFileSize = 0;	
	// �O�_����
//	boolean IsCompleted = false;
//	// ���ɦs�x�ؿ� (���K�����x)
//	String mSavePath_SenaoPhoto = SenaoSalseClass.DEF_DIR_SENAO_PHOTO;
//	// ���ɦs�x�ؿ� (�����u�f Image & WaWa Bank logo)
//	String mSavePath_SenaoImage = SenaoSalseClass.DEF_DIR_SENAO_IMAGE;
	// �U���Ȧs���|�ɦW
	String currentTempFilePath_Branch = "";
	
	// �^�Ǫ��A	
	String mStrDlReStr = "";
	
	// =============================== 		���K�����x
//	String mStrFileName_Secret = "";
//	String mLinkURL_Secret = "";
//	int mIntLimitFileSize_Secret = 0;
//	// �ǿ��ɮפj�p
//	int mIntFileTransSize_Secret = 0;	// �U���Ȧs���|�ɦW
//	String currentTempFilePath_Secret = "";
	
	// =============================== 		GPS
	private LocationManager mLocationManager = null;
	
	private NotificationManager notificationManager;
	
	@Override
	public void onCreate() {
		
		super.onCreate();
	}



	@Override
	public IBinder onBind(Intent intent) {
		
		Log.d(GpsMapV2Class.TAG, "ControlService-onBind");
		
		//���o�ǻ��Ѽ�
		Bundle getBundle = intent.getExtras(); 
		if (getBundle != null){
			mIntMode = getBundle.getInt("ServiceMode");
			
//			// �U���ɮ� (���K�����x)
//			if (mIntMode == DEF_SERVICE_MODE_GPS){
//				mStrFileName_Secret = getBundle.getString("ServiceFileName");
//				mLinkURL_Secret = getBundle.getString("ServiceLinkURL");
//				mIntLimitFileSize_Secret = getBundle.getInt("ServiceLimitFileSize");				
//			}
		}
//		IsCompleted = false;
		
//		Log.d(SenaoSalseClass.TAG, "mIntMode: " + mIntMode + " mIntSubMode: " + mIntSubMode + " mLinkURL: " + mLinkURL_Secret + " mStrFileName: " + mStrFileName_Secret + " mIntLimitFileSize: " + mIntLimitFileSize_Secret);
		
//		if (mIntMode == DEF_SERVICE_MODE_GPS){
//			Log.d(GpsMapV2Class.TAG, "ControlService-onBind: mIntMode == DEF_SERVICE_MODE_GPS");
//			// �U���ɮ� (���K�����x)
////			dlFileFromURL();
//		}
//		else if (mIntMode == DEF_SERVICE_MODE_PIC_DL){
//			// �����u�f
//			if (mIntSubMode == DEF_SERVICE_SUB_MODE_PROMOTIONS){
//				 //�U������
//				dlPhotoFromURL();
//			}
//		}
		
		return mBinder;
	}
	
	

	@Override
	public boolean onUnbind(Intent intent) {
		
//		if (mLocationManager != null)
//			mLocationManager.removeUpdates(mLocationListener);
		
		stopForeground(true);
		
		return super.onUnbind(intent);
	}

	private void showNotification() {
		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        CharSequence text = "GPS Map V2 ���յ{��";
        //2013.07.04	Ned
        Notification notification = new Notification(
        		R.drawable.ic_launcher, text, System.currentTimeMillis());
        
        PendingIntent contentIntent = PendingIntent.getActivity(
        		this, 0, 
        		new Intent(this, GpsMapV2Activity.class), 
        		0);
        notification.setLatestEventInfo(this, getText(R.string.app_name), text, contentIntent);
       
        //�N�A�ȱ`�n
        startForeground(0x1982, notification);
        
    }
	// ���U CallBack
    private final ITaskBinder.Stub mBinder = new ITaskBinder.Stub() {  
        
        public void stopRunningTask() {  
              
        }  
      
        public boolean isTaskRunning() {   
            return false;   
        }   
          
        public void registerCallback(ITaskCallback cb) {   
            if (cb != null) {   
                mCallbacks.register(cb);  
            }  

    		// Notify
    		showNotification();
    		
    		// LocationManager
			gpsLocationManager();
        }  
          
        public void unregisterCallback(ITaskCallback cb) {  
            if(cb != null) {  
                mCallbacks.unregister(cb);  
            }  
        }  
    }; 
    
    final RemoteCallbackList <ITaskCallback>mCallbacks = new RemoteCallbackList <ITaskCallback>(); 
    	
    // ============================================= GPS
    private void gpsLocationManager(){
    	
    	Log.d(GpsMapV2Class.TAG, "ControlService-gpsLocationManager()");
    	
        // GPS Location        
        if (mLocationManager == null){
        	
        	mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        	
	        final int N = mCallbacks.beginBroadcast();  
	        for (int i=0; i<N; i++) {   
	            try {  
	                mCallbacks.getBroadcastItem(i).isGpsProviderEnabled(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
	            }  
	            catch (RemoteException e) {   
	                // The RemoteCallbackList will take care of removing   
	                // the dead object for us.     
	            }  
	        }  
	        mCallbacks.finishBroadcast(); 
	        
        	
        	Log.d(GpsMapV2Class.TAG, "ControlService-checkGPSNetOnOff()");
        	
        	mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, mLocationListener);
        	
        	mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, mLocationListener);
        	
        }
    }
    
	// ============================================= �U���ɮ�
	// �U���ɮ�
	private void dlFileFromURL(){
		
//		Log.d(SenaoSalseClass.TAG, "===========dlFileFromURL()==========" + mLinkURL_Secret + mStrFileName_Secret);
		
	      // �_�@��Runnable�ӽT�O�ɮצb�x�s������~�}�lstart() 
//	      Runnable r = new Runnable(){  
//	        public void run(){  
//	          try{
//	            // setDataSource�|�N�ɮצs��SD�d 
//	            if (setDataSource(mLinkURL_Secret + mStrFileName_Secret)){
//		            //�ѩ������O�`�ǰ���
//		            
//	            	// �ɮפU������ -> �����Y
////	            	Log.d(SenaoSalseClass.TAG, "DL Success --> " + mLinkURL_Secret + mStrFileName_Secret);
//	            	
////	            	mStrDlReStr = SenaoSalseClass.DEF_SERVICE_RESULT_UNZIP;
//	            	
//	    			// ���o�ɦW�����U���ؿ�
//	    			String strFolderName = mStrFileName_Secret.substring(0, mStrFileName_Secret.length() - 4);
//	    			
//	    			// �إߦU���ؿ�
//	    			SenaoFile.createFolder(SenaoFile.getSDPath() + File.separator + SenaoSalseClass.DEF_DIR_SENAO_PHOTO + File.separator + strFolderName);
//	    			
////	            	Log.d(SenaoSalseClass.TAG, "UnZip --> " + currentTempFilePath_Secret);
//	    			// �����Y
//	    			SenaoXZip.UnZipFolder(currentTempFilePath_Secret, 
//	    					SenaoFile.getSDPath() + File.separator + SenaoSalseClass.DEF_DIR_SENAO_PHOTO + File.separator + strFolderName + File.separator);
//	    			
////	    			Log.d(SenaoSalseClass.TAG, "UnZip To--> " + SenaoFile.getSDPath() + File.separator + SenaoSalseClass.DEF_DIR_SENAO_PHOTO + File.separator + strFolderName + File.separator);
//	    			
//	            	mStrDlReStr = SenaoSalseClass.DEF_SERVICE_RESULT_OK; 
//	            	
////	            	Log.d(SenaoSalseClass.TAG, "DEF_SERVICE_RESULT_OK!!");
//	            	
//	            	
////		            //�G�|�bsetDataSource�������prepare()	            	
////		            player.prepare();
////		            
////		            // �}�l����mp3 
////		            player.start();
////		            IsPlay = true;
////		            bIsReleased = false;
//	            }
//
//	          }catch (Exception e){
//	          }
//	        }
//	      };  
//	      new Thread(r).start();		
	}
	
	  // �ۭq����x�sURL��mp3�ɮצ�SD�O�Хd 
	  private boolean setDataSource(String strPath) throws Exception {
////		  Log.d(SenaoSalseClass.TAG, "===========setDataSource()==========" + strPath + mStrFileName_Secret);
////		  Log.d(TAG, "setDataSource: " + strPath);
//	    // �P�_�ǤJ����}�O�_��URL
//	    if (!URLUtil.isNetworkUrl(strPath)){
////	    	//�ǤJ�D URL 
////	    	player.setDataSource(strPath);
//	    }else{
//	    	//�ǤJ�� URL
//	    	boolean bDLFile = true;
//	    	
//	    	//�ˬd���|�O�_�w�s�b
//	    	//if (checkSDCardFile()){
//	    	if (SenaoFile.createFolder(SenaoFile.getSDPath() + File.separator + mSavePath_SenaoPhoto)){
//	    		
//	             //File myFile = new File(vSDCard.getAbsolutePath() + File.separator + "MyFile.txt");
////	             File myFile = new File(vSDCard.getAbsolutePath() + "/japansong/" + mStrFileName_Secret);
//	    		File myFile = new File(SenaoFile.getSDPath()  + File.separator + mSavePath_SenaoPhoto + File.separator + mStrFileName_Secret);
//	    		//File.separator 
//
//	             //�P�_�ɮ׬O�_�s�b (�O�_�U���ѥ~���M�w)
////	             if (myFile.exists()) {
////	            	 mStrDlReStr = SenaoSalseClass.DEF_SERVICE_RESULT_OK;
////	            	 bDLFile = false;//�s�b, ���U��
////	             }
//	             
//	             //�g�J�ɮ׸��|
////	             currentTempFilePath_Secret = vSDCard.getParent() + vSDCard.getName() +  "/japansong/" + mStrFileName_Secret;
//	             currentTempFilePath_Secret = SenaoFile.getSDPath()  + File.separator + mSavePath_SenaoPhoto + File.separator + mStrFileName_Secret;
//	    	}else{
//	    		mStrDlReStr = SenaoSalseClass.DEF_SERVICE_RESULT_FAIL;
////	    		Log.d(TAG, "MS: SD Not Exist!!!!!");
//	    		return false;
//	    	}
//	    	
//	    	if (bDLFile){
////	    		Log.d(SenaoSalseClass.TAG, "===========setDataSource()-bDLFile==========" + strPath + mStrFileName_Secret);
//	    		
//		        // �إ�URL����
//		        URL myURL = new URL(strPath);
//		        URLConnection conn = myURL.openConnection();
//				conn.setConnectTimeout(SenaoSalseClass.DEF_Server_Connection_TimeOut);
//			    conn.setReadTimeout(SenaoSalseClass.DEF_Server_Connection_TimeOut);
//		        
////		        Log.d(SenaoSalseClass.TAG, "conn.connect() strPath! --> " + strPath);
//		        
//	    		try{
//			        conn.connect();	    			
//	    		}catch(Exception ex){
//	    			mStrDlReStr = SenaoSalseClass.DEF_SERVICE_RESULT_FAIL;
////	    			Log.d(SenaoSalseClass.TAG, "conn.connect() Fail! --> " + ex.getMessage(), ex);
//	    			return false;
//	    		}
//		        
////	    		int iFileSize = conn.getContentLength();
////	    		Log.d(SenaoSalseClass.TAG, "conn.connect() iFileSize! --> " + iFileSize);
//	    		
//		        // ���oURLConnection��InputStream
//		        InputStream is = conn.getInputStream();
//		        
//		        if (is == null)
//		        {
//		          throw new RuntimeException("stream is null");
//		        }
//		        
//		        try
//		        {
//			        // �إ߷s���Ȧs�� 
//			        //File myTempFile = File.createTempFile("hippoplayertmp", "."+getFileExtension(strPath));
//			        //File myTempFile = File.createTempFile("abc", "."+getFileExtension(strPath));
//			    	//File myTempFile = File.createTempFile(vSDCard.getAbsolutePath() + "/japansong/" + "001.mp3");
////			        File myTempFile = new File(vSDCard.getAbsolutePath() + "/japansong/",  mStrFileName_Secret);
////		        	File myTempFile = new File(getSDPath() + "/" + mDirName + "/",  mStrFileName_Secret);
//		        	File myTempFile = new File(currentTempFilePath_Secret);
//		        	
////		        	Log.d(SenaoSalseClass.TAG, "currentTempFilePath_Secret: " + currentTempFilePath_Secret);
//		        	
////			        currentTempFilePath_Secret = myTempFile.getAbsolutePath();
//			        
//			        //currentTempFilePath_Secret = "/sdcard/hippoplayertmp39327.mp3";
//			        //currentTempFilePath_Secret = "/sdcard/abc.mp3";
//			        
//			        FileOutputStream fos = new FileOutputStream(myTempFile);
//			        byte buf[] = new byte[1024];
//	//		        do
//	//		        { 
//	//		          int numread = is.read(buf);
//	//		          if (numread <= 0)
//	//		          {
//	//		            break;
//	//		          }
//	//		          fos.write(buf, 0, numread);
//	//		        }while (true);
//	
//			        int len1 = 0;
//		            while ((len1 = is.read(buf)) > 0) {
//		            	mIntFileTransSize_Secret += 1024;
//		            	fos.write(buf, 0, len1);
////		            	Log.d(SenaoSalseClass.TAG, "mIntFileTransSize--->: " + mIntFileTransSize_Secret);
//		            	
//		            }
//		            fos.close();
//			        
//		            is.close();
//		        }   
//		        catch (Exception ex)
//		        {
////		        	Log.d(SenaoSalseClass.TAG, "setDataSource: File Exception!!" + ex.getMessage(), ex);
//		        	mStrDlReStr = SenaoSalseClass.DEF_SERVICE_RESULT_FAIL;
//		        	return false;
//		        }
//		        
//		    	//���o�ǿ�j�p < �̤p����j�p (�h���������)
//		    	if (mIntFileTransSize_Secret < mIntLimitFileSize_Secret){
//		    		mStrDlReStr = SenaoSalseClass.DEF_SERVICE_RESULT_FAIL;
//		    		return false;
//		    	}
//		    	
//	    	}//if (bDLFile)
//	    	
////	        if(currentTempFilePath_Secret!=""){
////	        	//Log.d(TAG, "currentTempFilePath_Secret -> " + currentTempFilePath_Secret);
////				// ����fos�x�s�����A�I�sMediaPlayer.setDataSource
////	        	if (player != null)
////	        		player.setDataSource(currentTempFilePath_Secret);
////	        	
//////	            if (mCallbacks != null){
//////	            	int iNumber = mCallbacks.beginBroadcast();  
//////		            //�^�ǹ����h�� Activity
//////		            for(int i=0; i<iNumber; i++)
//////		            	mCallbacks.getBroadcastItem(i).onNotifyDL(true);
//////	            	mCallbacks.finishBroadcast();
//////	            }
////	        }
//	        return true;
//	    }
	    return false;
	  }
	  
	  // ========================================= GPS
		// �Y GPS�BWiFi�B3G...�ҵL�h���|���� onLocationChanged()
	LocationListener mLocationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {

			Log.d(GpsMapV2Class.TAG,
					"LocationListener(R)-Lat: " + location.getLatitude() + " Lng: " + location.getLongitude());

	        final int N = mCallbacks.beginBroadcast();  
	        for (int i=0; i<N; i++) {   
	            try {  
///	                mCallbacks.getBroadcastItem(i).actionPerformed(val);
	            	mCallbacks.getBroadcastItem(i).gpsLocationManagerLatlng(location.getProvider(), location.getLatitude(), location.getLongitude(), location.getAccuracy());
	            }  
	            catch (RemoteException e) {   
	                // The RemoteCallbackList will take care of removing   
	                // the dead object for us.     
	            }  
	        }  
	        mCallbacks.finishBroadcast(); 

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onProviderDisabled(String provider) {

		}

	};
	
}
