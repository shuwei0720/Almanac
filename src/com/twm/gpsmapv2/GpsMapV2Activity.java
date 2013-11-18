package com.twm.gpsmapv2;

import java.util.ArrayList;
import java.util.List;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.internal.s;
import com.google.android.gms.internal.cc.g;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GpsMapV2Activity extends Activity {

	
	private RelativeLayout mRlMain = null;
	private RelativeLayout mRlTop = null;
	private RelativeLayout mRlMapLayout = null;
	
//	Polyline mPolylineLeft = null, mPolylineRight = null;
	
	private MapFragment mMapFragmentLeft;
	private MapFragment mMapFragmentRight;
	private GoogleMap mMapLeft;
	private GoogleMap mMapRight;
	private RelativeLayout mRlMapLeft = null;
	private RelativeLayout mRlMapRight = null;
	private int mIntRecordTimesLeft = 0;
	private int mIntRecordTimesRight = 0;
	
	private boolean mShowFlag = true;
	
	private ArrayList<LatLng> mlistLatLngLeft = null, mlistLatLngRight = null;
	
	
//	private float mIntOffsetLeft = 0.0001f;
//	private float mIntOffsetRight = 0.0001f;
	
	private RelativeLayout mRlListView = null;
	private ListView mLvLeft = null, mLvRight = null;
	
	private int DEF_RECORD_TIMES = 3;
	
	private DataAdapter mDataAdapterLeft = null, mDataAdapterRight = null;
	  
	private float mfdensity = 1;
	private int mIntScreenWidth = 480;
	private int mIntScreenHeight = 800;
	private int mIntMapWidth, mIntMapHeight;

	private int DEF_BASE_RESID = 0x4100;
	
	// 台北車站
	private double mdLat = 25.048421;
	private double mdLng = 121.517069;
	
	// 廣播 Screen ON/OFF 
	BroadcastReceiver mReceive=  null;
		
	
	ITaskBinder mService = null;
	
	// Sql
//	private TwmSqlHelper m_TwmSqlHelper = null;
//	private SqlTable_LocationLog m_SqlTable_LocationLog = null;
	
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			try {
				mService = ITaskBinder.Stub.asInterface(service);
				mService.registerCallback(mCallback);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.d(GpsMapV2Class.TAG, "mConnection-onServiceConnected() ");
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
	};
	
	// LocationClient	
	LocationClient mLocationClient = null;
	LocationRequest mLocationRequest = null;
	LocationListener mLocationListener = null;
//	private final int MILLISECONDS_PER_SECOND = 1000; // Milliseconds per second
//	public final int UPDATE_INTERVAL_IN_SECONDS = 300;// Update frequency in seconds
//	private final long UPDATE_INTERVAL =  MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;  // Update frequency in milliseconds
//	private final int FASTEST_INTERVAL_IN_SECONDS = 60;  // The fastest update frequency, in seconds
//	// A fast frequency ceiling in milliseconds
//	private final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
	
	ConnectionCallbacks mConnectionCallbacks = new ConnectionCallbacks(){

		@Override
		public void onConnected(Bundle arg0) {
		    mLocationRequest = LocationRequest.create();
		    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);    // Use high accuracy
//		    mLocationRequest.setInterval(UPDATE_INTERVAL);  // Setting the update interval to  5mins
//		    mLocationRequest.setFastestInterval(FASTEST_INTERVAL);  // Set the fastest update interval to 1 min
		    mLocationRequest.setInterval(500);  // Setting the update interval to  5mins
		    mLocationRequest.setFastestInterval(500);  // Set the fastest update interval to 1 min
		    mLocationListener = new MyLocationListener();
		    mLocationClient.requestLocationUpdates(mLocationRequest,mLocationListener);
		}

		@Override
		public void onDisconnected() {
			
		}
		
		
	};
	
	// LocationClient
	OnConnectionFailedListener mOnConnectionFailedListener = new OnConnectionFailedListener(){

		@Override
		public void onConnectionFailed(ConnectionResult arg0) {
			
		}
		
	};
	
	private class MyLocationListener implements LocationListener {
	     @Override
	     public void onLocationChanged(Location location) {
	         // Report to the UI that the location was updated
//	         mCurrentLocation =location;
	         Context context = getApplicationContext();

			Log.d(GpsMapV2Class.TAG, "MyLocationListener(L)-Lat: " + location.getLatitude() 
			+ " Lng: " + location.getLongitude() + " Acc: " + location.getAccuracy());
	         
//	         msg = Double.toString(location.getLatitude()) + "," +  Double.toString(location.getLongitude());
//	         list.add(mCurrentLocation);
//
//	         Toast.makeText(context, msg,Toast.LENGTH_LONG).show();
	         
				mlistLatLngLeft.add(new LatLng(location.getLatitude(), location.getLongitude()));
//				mlistLatLngLeft.add(new LatLng(location.getLatitude() + mIntOffsetLeft, location.getLongitude() + mIntOffsetLeft));
//				mIntOffsetLeft += 0.0001;
				
				String strAccString = "" + location.getAccuracy();
				if (location.getAccuracy() < 60.f){
					
					strAccString += " -> Draw";
					
					if (mIntRecordTimesLeft >= DEF_RECORD_TIMES){
						mIntRecordTimesLeft = 0;
						 PolylineOptions rectOptions = new PolylineOptions();
						 rectOptions.addAll(mlistLatLngLeft);
						 
						 mMapLeft.addPolyline(rectOptions);
						 
						 mMapLeft.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
//						 mMapLeft.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude() + mIntOffsetLeft, location.getLongitude() + mIntOffsetLeft), 15));
						 mlistLatLngLeft.clear();
						
					}else
						mIntRecordTimesLeft++;
				}

				
				mDataAdapterLeft.setData(0, location.getProvider(), 
						location.getLatitude() + "," + location.getLongitude(), "" + strAccString);
				mDataAdapterLeft.notifyDataSetChanged();

	      }

	        }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TelephonyManager mTelephonyManager =  (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
				
		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		mfdensity = getResources().getDisplayMetrics().density;
		mIntScreenWidth = dm.widthPixels;
		mIntScreenHeight = dm.heightPixels;
		dm = null;
		
		//public LocationClient(android.content.Context context, com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks connectionCallbacks, com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener connectionFailedListener);

		// Create a new location client, using the enclosing class to handle callbacks
	    mLocationClient = new LocationClient(this, mConnectionCallbacks, mOnConnectionFailedListener);   
	    mLocationClient.connect();

	     
		// 建立 Service Connection
//		if (mConnection == null) {
//			
//			Log.d(GpsMapV2Class.TAG, "mConnection == null");
//			
//
//		}
		
		mIntMapWidth = (mIntScreenWidth / 2);
		mIntMapHeight = (mIntScreenHeight / 2);
		
//		if (m_SqlTable_LocationLog == null)
//			m_SqlTable_LocationLog = new SqlTable_LocationLog(m_TwmSqlHelper);

		if (mRlMain == null)
			mRlMain = new RelativeLayout(this);
		mRlMain.setId(++DEF_BASE_RESID);
		
		if (mRlTop == null)
			mRlTop = new RelativeLayout(this);
		else
			mRlTop.removeAllViews();
		
		mRlTop.setId(++DEF_BASE_RESID);
		mRlTop.setBackgroundColor(Color.DKGRAY);
		
		TextView tvMapv2 = new TextView(this);
		TextView tvLocationManager = new TextView(this);
		tvMapv2.setTextColor(Color.WHITE);
		//tvMapv2.setText("【MapV2 MyLocation】");
		tvMapv2.setText("【LocationClient】");
		tvLocationManager.setText("【LocationManager】");
		tvLocationManager.setTextColor(Color.WHITE);
		
		
		RelativeLayout.LayoutParams rllptvMapv2 = new RelativeLayout.LayoutParams(
				mIntMapWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
		rllptvMapv2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		rllptvMapv2.leftMargin = 5;
		
		RelativeLayout.LayoutParams rllptvLocationManager = new RelativeLayout.LayoutParams(
				mIntMapWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
		rllptvLocationManager.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		rllptvLocationManager.leftMargin = 5;
		
		mRlTop.addView(tvMapv2, rllptvMapv2);
		mRlTop.addView(tvLocationManager, rllptvLocationManager);

		RelativeLayout.LayoutParams rllpmRlTop = new RelativeLayout.LayoutParams(
				mIntScreenWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
		mRlMain.addView(mRlTop, rllpmRlTop);
		
		
		// ================================================ ListView
		if (mRlListView == null)
			mRlListView = new RelativeLayout(this);
				
		if (mDataAdapterLeft == null)
			mDataAdapterLeft = new DataAdapter(this);
		
		if (mDataAdapterRight == null)
			mDataAdapterRight = new DataAdapter(this);
		
		if (mLvLeft == null)
			mLvLeft = new ListView(this);
		mLvLeft.setAdapter(mDataAdapterLeft);
//		mLvLeft.setBackgroundColor(Color.GREEN);

		if (mLvRight == null)
			mLvRight = new ListView(this);
		mLvRight.setAdapter(mDataAdapterRight);
//		mLvRight.setBackgroundColor(Color.YELLOW);
		
		RelativeLayout.LayoutParams rllpmLvLeft = new RelativeLayout.LayoutParams(
				mIntMapWidth, mIntMapHeight);
		rllpmLvLeft.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		mRlListView.addView(mLvLeft, rllpmLvLeft);
		
		RelativeLayout.LayoutParams rllpmLvRight = new RelativeLayout.LayoutParams(
				mIntMapWidth, mIntMapHeight);
		rllpmLvRight.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		mRlListView.addView(mLvRight, rllpmLvRight);
		
		RelativeLayout.LayoutParams rllpmRlListView = new RelativeLayout.LayoutParams(
				mIntScreenWidth, mIntMapHeight);
		rllpmRlListView.addRule(RelativeLayout.BELOW, mRlTop.getId());
		mRlMain.addView(mRlListView, rllpmRlListView);
		
		// ========================================== Map Layout		
		if (mlistLatLngLeft == null)
			mlistLatLngLeft = new ArrayList<LatLng>();
		else
			mlistLatLngLeft.clear();

		if (mlistLatLngRight == null)
			mlistLatLngRight = new ArrayList<LatLng>();
		else
			mlistLatLngRight.clear();	
		
		if (mRlMapLayout == null)
			mRlMapLayout = new RelativeLayout(this);

//		GradientDrawable GDBtn = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[] { 0xE1F875B8,
//				0xE1DA0A72 });
//		GDBtn.setCornerRadius(0);
//		GDBtn.setStroke((int) (3 * mfdensity), 0xFFF980BC);

		mRlMapLayout.setBackgroundColor(Color.BLACK);
		
		if (mMapFragmentLeft == null)
			mMapFragmentLeft = new MapFragment();
		mMapFragmentLeft.newInstance();

		if (mMapFragmentRight == null)
			mMapFragmentRight = new MapFragment();
		mMapFragmentRight.newInstance();

		if (mRlMapLeft == null)
			mRlMapLeft = new RelativeLayout(this);
		mRlMapLeft.setId(++DEF_BASE_RESID);

		if (mRlMapRight == null)
			mRlMapRight = new RelativeLayout(this);
		mRlMapRight.setId(++DEF_BASE_RESID);

		RelativeLayout.LayoutParams rllpmRlMapLeft = new RelativeLayout.LayoutParams(mIntMapWidth
				- (int) (5 * mfdensity), mIntMapHeight - (int) (40 * mfdensity));
		rllpmRlMapLeft.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		rllpmRlMapLeft.leftMargin = (int) (3 * mfdensity);
		mRlMapLayout.addView(mRlMapLeft, rllpmRlMapLeft);

		RelativeLayout.LayoutParams rllpmRlMapRight = new RelativeLayout.LayoutParams(mIntMapWidth
				- (int) (5 * mfdensity), mIntMapHeight - (int) (40 * mfdensity));
		rllpmRlMapRight.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		rllpmRlMapRight.rightMargin = (int) (3* mfdensity);
		mRlMapLayout.addView(mRlMapRight, rllpmRlMapRight);

		// 轉換 Map Fragment -> Map RelativeLayout
		FragmentTransaction ft_Layout_L = getFragmentManager().beginTransaction();
		ft_Layout_L.replace(mRlMapLeft.getId(), mMapFragmentLeft).commit();

		FragmentTransaction ft_Layout_R = getFragmentManager().beginTransaction();
		ft_Layout_R.replace(mRlMapRight.getId(), mMapFragmentRight).commit();

		RelativeLayout.LayoutParams rllpmRlMapLayout = new RelativeLayout.LayoutParams(mIntScreenWidth, mIntMapHeight);
		rllpmRlMapLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		mRlMain.addView(mRlMapLayout, rllpmRlMapLayout);
		
		RelativeLayout.LayoutParams rllpmRlMain = new RelativeLayout.LayoutParams(mIntScreenWidth, mIntScreenHeight);
		mRlMain.setBackgroundColor(Color.TRANSPARENT);
		setContentView(mRlMain, rllpmRlMain);

		// 初始化/註冊 Screen ON/OFF Broadcast
		initBroadcastReceiver();
		
		// Bind Service
		try {
			if (GpsMapV2Class.mbIsBind) {// 已經 Bind
				
				Log.d(GpsMapV2Class.TAG, "GpsMapV2Class.mbIsBind: True");
				
				if (mConnection != null && mService != null) {
					// 傳入 CallBack
					mService.registerCallback(mCallback);	
				}
			} else {
				if (mConnection != null) {
					
					Log.d(GpsMapV2Class.TAG, "GpsMapV2Class.mbIsBind: False");
					
					Intent intentBS = new Intent(this, ControlService.class);
					Bundle bundleBS = new Bundle();
					bundleBS.putInt("ServiceMode", ControlService.DEF_SERVICE_MODE_GPS);// Mode
					intentBS.putExtras(bundleBS);
					GpsMapV2Class.mbIsBind = bindService(intentBS, mConnection, Context.BIND_AUTO_CREATE);
					// 傳入 CallBack Connection 剛建好無法設, 於 onServiceConnected() 中處理
					//mService.registerCallback(mCallback);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(GpsMapV2Class.TAG, "GpsMapV2Class.mbIsBind-Exception: " + e.toString());
		}
	
	}
	
	@Override
	protected void onResume() {
	    	
		 if (mMapLeft == null) {
	            // MapFragment  GoogleMap
	            mMapLeft = mMapFragmentLeft.getMap();
	            if (mMapLeft != null) {
	            	
	            	mMapLeft.setMapType(GoogleMap.MAP_TYPE_NORMAL);	// 街道圖
	 
	                // 屋內表示（標準 true）
	                mMapLeft.setIndoorEnabled(false);
	 
	                // 現在地表示
	                mMapLeft.setMyLocationEnabled(true);
	                // UiSettings 表示設定標準設定不要
	                // mMap.getUiSettings().setMyLocationButtonEnabled(true);
	                
	                // 設定 Marker 點擊
//	                mMap.setOnMarkerClickListener(omcl);
	                // 設定 Marker Windows 點擊
//	                mMap.setOnInfoWindowClickListener(oiwc);
	                // 縮放按鈕
	                mMapLeft.getUiSettings().setZoomControlsEnabled(false);
	                // 設定圖層大小
	                mMapLeft.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
	                
            		//經度(-180~180)、緯度(-90~90), 請確認
//            		if ((mdLng >-180 &&  mdLng < 180) || (mdLat > -90 && mdLat < 90)){
//            			// 取得 LBS
//            			mdLat = SenaoServicesClass.mDGPSLat ;
//            			mdLng = SenaoServicesClass.mDGPSLng ;
////            				mbLBSLocation = false;
//            				// 計算 三公里內分店
////            				getThreeAreaStore();
//            		}
	            	
            		LatLng latlngBranch = new LatLng(mdLat,mdLng);
            		mMapLeft.moveCamera(CameraUpdateFactory.newLatLngZoom(latlngBranch, 15));
            		
//				mMap.addMarker(new MarkerOptions()
//						.position(latlngBranch)
//						.title(mStrStoreType + "-" + mStrStoreName)
//						.snippet(mStrStoreAddress + " (距離:" + SenaoShareFunc.m2km("" + SenaoShareFunc.gps2m(mdLat,mdLng, 
//								SenaoServicesClass.mDGPSLat , SenaoServicesClass.mDGPSLng )) + ")")
//						.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
            		
	    	        // LBS
//	    	        mMapLeft.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener(){
//						@Override
//						public void onMyLocationChange(Location location) {
////							Log.d(GpsMapV2Class.TAG, "setOnMyLocationChangeListener(L)-Lat: " + location.getLatitude() 
////									+ " Lng: " + location.getLongitude());
//							
//							mlistLatLngLeft.add(new LatLng(location.getLatitude(), location.getLongitude()));
////							mlistLatLngLeft.add(new LatLng(location.getLatitude() + mIntOffsetLeft, location.getLongitude() + mIntOffsetLeft));
////							mIntOffsetLeft += 0.0001;
//							
//							if (mIntRecordTimesLeft >= DEF_RECORD_TIMES){
//								mIntRecordTimesLeft = 0;
//								 PolylineOptions rectOptions = new PolylineOptions();
//								 rectOptions.addAll(mlistLatLngLeft);
//								 
//								 mMapLeft.addPolyline(rectOptions);
//								 
//								 mMapLeft.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
////								 mMapLeft.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude() + mIntOffsetLeft, location.getLongitude() + mIntOffsetLeft), 15));
//								 mlistLatLngLeft.clear();
//								
//							}else
//								mIntRecordTimesLeft++;
//							
//							mDataAdapterLeft.setData(0, location.getProvider(), 
//									location.getLatitude() + "," + location.getLongitude(), "" + location.getAccuracy());
//							mDataAdapterLeft.notifyDataSetChanged();
//							
//						}
//	    	        	
//	    	        });
	            }
	            
//	            Polyline line = mMap.addPolyline(new PolylineOptions()
//                .add(new LatLng(-37.81319, 144.96298), 
//                     new LatLng(-31.95285, 115.85734))
//                .geodesic(true));
	         
	            // https://developers.google.com/maps/documentation/android/shapes
	         // 線 
//	         // Instantiates a new Polyline object and adds points to define a rectangle
//	            PolylineOptions rectOptions = new PolylineOptions()
//	                    .add(new LatLng(25.049525,121.514579))
//	                    .add(new LatLng(25.049544,121.512863))  // North of the previous point, but at the same longitude
//	                    .add(new LatLng(25.050205,121.509987))  // Same latitude, and 30km to the west
//	                    .add(new LatLng(25.050458,121.506533))  // Same longitude, and 16km to the south
//	                    .add(new LatLng(25.04797,121.504773)); // Closes the polyline.
//	            
//	            rectOptions.color(Color.BLUE);
//	            
//	            // Get back the mutable Polyline
//	            Polyline polyline = mMap.addPolyline(rectOptions);
	     
		         // 矩形
//		         // Instantiates a new Polyline object and adds points to define a rectangle
//	            PolygonOptions rectOptions = new PolygonOptions()
//	                    .add(new LatLng(25.049525,121.514579),
//	                    new LatLng(25.049544,121.512863),  // North of the previous point, but at the same longitude
//	                    new LatLng(25.050205,121.509987),  // Same latitude, and 30km to the west
//	                    new LatLng(25.050458,121.506533),  // Same longitude, and 16km to the south
//	                    new LatLng(25.04797,121.504773)); // Closes the polyline.
//	            
//	            rectOptions.strokeColor(Color.BLUE);
//	            
//	            // Get back the mutable Polyline
//	            Polygon polygon = mMap.addPolygon(rectOptions);
	            
	        }// End of mMap
		 
		 if (mMapRight == null) {
	            // MapFragment  GoogleMap
	            mMapRight = mMapFragmentRight.getMap();
	            if (mMapRight != null) {
	            	
	            	mMapRight.setMapType(GoogleMap.MAP_TYPE_NORMAL);	// 街道圖
	 
	                // 屋內表示（標準 true）
	                mMapRight.setIndoorEnabled(false);
	 
	                // 現在地表示
	                mMapRight.setMyLocationEnabled(false);
	                // UiSettings 表示設定標準設定不要
	                // mMap.getUiSettings().setMyLocationButtonEnabled(true);
	                
	                // 設定 Marker 點擊
//	                mMap.setOnMarkerClickListener(omcl);
	                // 設定 Marker Windows 點擊
//	                mMap.setOnInfoWindowClickListener(oiwc);
	                // 縮放按鈕
	                mMapRight.getUiSettings().setZoomControlsEnabled(false);
	                // 設定圖層大小
	                mMapRight.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
	                
         		//經度(-180~180)、緯度(-90~90), 請確認
//         		if ((mdLng >-180 &&  mdLng < 180) || (mdLat > -90 && mdLat < 90)){
//         			// 取得 LBS
//         			mdLat = SenaoServicesClass.mDGPSLat ;
//         			mdLng = SenaoServicesClass.mDGPSLng ;
////         				mbLBSLocation = false;
//         				// 計算 三公里內分店
////         				getThreeAreaStore();
//         		}
	            	
         		LatLng latlngBranch = new LatLng(mdLat,mdLng);
         		mMapRight.moveCamera(CameraUpdateFactory.newLatLngZoom(latlngBranch, 15));
         		
//				mMap.addMarker(new MarkerOptions()
//						.position(latlngBranch)
//						.title(mStrStoreType + "-" + mStrStoreName)
//						.snippet(mStrStoreAddress + " (距離:" + SenaoShareFunc.m2km("" + SenaoShareFunc.gps2m(mdLat,mdLng, 
//								SenaoServicesClass.mDGPSLat , SenaoServicesClass.mDGPSLng )) + ")")
//						.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
         		
//         		options.add(latLng).width(12).color(Color.parseColor("#32B4E5"))
//				.geodesic(true);
	    	        // LBS
//	    	        mMapRight.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener(){
//						@Override
//						public void onMyLocationChange(Location arg0) {
//							Log.d(GpsMapV2Class.TAG, "setOnMyLocationChangeListener(R)-Lat: " + arg0.getLatitude() 
//									+ " Lng: " + arg0.getLongitude());
////							
////							// 寫入全域位址
////							SenaoServicesClass.mDGPSLat  = arg0.getLatitude();
////							SenaoServicesClass.mDGPSLng = arg0.getLongitude();
//							
////							mlistLatLngLeft.add(new LatLng(arg0.getLatitude(), arg0.getLongitude()));
//							
//						}
//	    	        	
//	    	        });
	            }
	        }// End of mMap
		 
		 // ============================================ Screen
	       if (!GpsScreenReceiver.wasScreenOn) {
	            LogFileUtil.writeToFile("SCREEN TURNED ON");
	        } else {
	            // this is when onResume() is called when the screen state has not changed
	        }
	       
		super.onResume();
	}

	@Override
	protected void onStart() {
		LogFileUtil.writeToFile("GpsMapV2Activity-onStart()");
		super.onStart();
	}

	@Override
	protected void onPause() {
		LogFileUtil.writeToFile("GpsMapV2Activity-onPause()");
	       if (GpsScreenReceiver.wasScreenOn) {
	            // this is the case when onPause() is called by the system due to a screen state change
	    	   LogFileUtil.writeToFile("SCREEN TURNED OFF");
	        } else {
	            // this is when onPause() is called when the screen state has not changed
	        }
		super.onPause();
	}

	@Override
	protected void onStop() {
		LogFileUtil.writeToFile("GpsMapV2Activity-onStop()");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		
		LogFileUtil.writeToFile("GpsMapV2Activity-onDestroy()");
		
		releaseGps();
    	
		super.onDestroy();
	}
	
	private void releaseGps(){
		if (mLocationClient != null){
			mLocationClient.removeLocationUpdates(mLocationListener);
			mLocationClient.disconnect();
		}
		
		if (GpsMapV2Class.mbIsBind && mConnection != null && mCallback != null) {
			try{
				mService.unregisterCallback(mCallback);
				unbindService(mConnection);	
			}catch(Exception e){
			}
		}
    	//反註冊廣播
    	if (mReceive != null)
    		unregisterReceiver(mReceive);
	}
	
	// 初始化 Screen ON/OFF Broadcast
	private void initBroadcastReceiver(){
	    // initialize receiver
        final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceive = new GpsScreenReceiver();
        registerReceiver(mReceive, filter);
	}
	
	private void setLineMapV2(PolylineOptions rectOptions){
		mMapLeft.addPolyline(rectOptions);
	}


	public class DataAdapter extends BaseAdapter{
		
		Context mContext;
		
		// 店家詳細資訊
		private List<String> mListType = new ArrayList<String>();
		private List<String> mListLatLng = new ArrayList<String>();
		private List<String> mListAcc = new ArrayList<String>();
		
		private int mIntLineOneColor = 0xFFC8C8C8;
				
		public DataAdapter(Context context){
			mContext = context;
		}
		
		public void clear(){
			mListType.clear();
			mListLatLng.clear();
			mListAcc.clear();
		}
		
		// 第一次呼叫時 (預設 photo 為空值)
		public void setData(int iPosition, String strType, String strLatLng, String strAcc){
						
			String adapterclsIPeenBonusSearch = null;

			mListType.add(iPosition, strType);
			mListLatLng.add(iPosition, strLatLng);
			mListAcc.add(iPosition, strAcc);
		}
						
		@Override
		public int getCount() {
			return mListType.size();
		}
		@Override
		public Object getItem(int arg0) {
			return null;
		}
		@Override
		public long getItemId(int position) {
			return 0;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			listLayoutView rlList;
			
			if (convertView == null){
				rlList = new listLayoutView(mContext);				
			}else{
				rlList = (listLayoutView) convertView;
			}

			if (position < mListType.size()){

				// Type
				rlList.mTvData[0].setText(mListType.get(position));
				// LatLng
				rlList.mTvData[1].setText(mListLatLng.get(position));
				// Acc
				rlList.mTvData[2].setText("Acc: " + mListAcc.get(position));		
				
				if ((position % 2) == 0){
					rlList.setBackgroundColor(mIntLineOneColor);
//					rlList.mTvData[1].setBackgroundColor(mIntLineOneColor);
//					rlList.mTvData[2].setBackgroundColor(mIntLineOneColor);
				}else
					rlList.setBackgroundColor(Color.TRANSPARENT);
			}

			return rlList;
		}
		
		public class listLayoutView extends RelativeLayout{
			
			public TextView[] mTvData = null;
			
			// 右側 Layout (mTvData, mIvStar, mTvDist)
			RelativeLayout rlRight = null;
			
			private int mIntBaseResId = 0x1000;
			
			public listLayoutView(Context context) {
				super(context);
				
				
				if (mTvData == null)
					mTvData = new TextView[3];
												
				// 右側 Layout (mTvData, mIvStar, mTvDist)
				if (rlRight == null){
					rlRight = new RelativeLayout(context);
					rlRight.setId(++mIntBaseResId);
				}
				
				// name, addr, 優惠 (From 優惠搜尋)
				for(int i=0; i<3; i++){
					mTvData[i] = new TextView(context);
					if (i == 0){
						mTvData[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, 12 * mfdensity);
						mTvData[i].setTextColor(Color.BLACK);
					}else if (i == 1){
						mTvData[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, 12 * mfdensity);
						mTvData[i].setTextColor(0xFF666666);
					}else{
						mTvData[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, 12 * mfdensity);
						mTvData[i].setTextColor(Color.RED);
					}
					
					mTvData[i].setId(++mIntBaseResId);
					
					mTvData[i].setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
					
					mTvData[i].setSingleLine();
					mTvData[i].setEllipsize(TruncateAt.END);
					
					RelativeLayout.LayoutParams rllp = null;
					
					//mTvData[i].setPadding(10, 0, 5, 0);
					
					if (i == 0){
						rllp = new RelativeLayout.LayoutParams(
								RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
					}else{
						rllp = new RelativeLayout.LayoutParams(
								RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);						
						rllp.addRule(RelativeLayout.BELOW, mTvData[i-1].getId());
					}
					rllp.leftMargin = (int) (5 * mfdensity);
					
					rlRight.addView(mTvData[i], rllp);
					
					rllp = null;
				}
				
				RelativeLayout.LayoutParams rllprlRight = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				rllprlRight.leftMargin = (int) (5 * mfdensity);
				addView(rlRight, rllprlRight);				
			}
		}		
	}//End of BaseAdapter

    private ITaskCallback mCallback = new ITaskCallback.Stub() {  
        
        public void actionPerformed(int id) {   
//            printf("callback id=" + id);  
        }
        
		@Override
		public void isGpsProviderEnabled(boolean bEnable) throws RemoteException {
			
			if (!bEnable) {
				new AlertDialog.Builder(GpsMapV2Activity.this).setTitle("GPS 設定").setMessage("您尚未開啟定位服務，要前往設定頁面啟動定位服務嗎？")
						.setCancelable(false).setPositiveButton("設定", new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {
								GpsMapV2Activity.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)); // 行動網路設定
																												// 頁面
								// context.startActivity(new
								// Intent(Settings.ACTION_WIFI_SETTINGS)); //
								// WiFi 頁面
							}
						}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								Toast.makeText(GpsMapV2Activity.this, "未開啟定位服務，將無法取得正確位置!!", Toast.LENGTH_LONG).show();
							}
						}).show();
			}
		}   

		@Override
		public void gpsLocationManagerLatlng(String strProvider, double dLat, double dlng, float fAccuracy) throws RemoteException {
			
			Log.d(GpsMapV2Class.TAG, "GpsMapV2Activity-mCallback-gpsLocationManagerLatlng(): " + dLat + dlng);
			
			mlistLatLngRight.add(new LatLng(dLat, dlng));
			// mlistLatLngRight.add(new LatLng(dLat +
			// mIntOffsetRight, dlng + mIntOffsetRight));
			// mIntOffsetRight += 0.0001;

			if (mIntRecordTimesRight >= DEF_RECORD_TIMES) {
				mIntRecordTimesRight = 0;
				PolylineOptions rectOptions = new PolylineOptions();
				rectOptions.addAll(mlistLatLngRight);

				mMapRight.addPolyline(rectOptions);

				mMapRight.moveCamera(CameraUpdateFactory.newLatLngZoom(
						new LatLng(dLat, dlng), 15));
				// mMapRight.moveCamera(CameraUpdateFactory.newLatLngZoom(new
				// LatLng(dLat + mIntOffsetRight,
				// dlng + mIntOffsetLeft), 15));
				mlistLatLngRight.clear();

			} else
				mIntRecordTimesRight++;

			// mMapRight.moveCamera(CameraUpdateFactory.newLatLngZoom(new
			// LatLng(dLat, dlng), 15));

			mDataAdapterRight.setData(0, strProvider,
					dLat + "," + dlng, "" + fAccuracy);
			mDataAdapterRight.notifyDataSetChanged();
			
		}

    };
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//參數1:群組id, 參數2:itemId, 參數3:item順序, 參數4:item名稱
		menu.add(0, 0, 0, "暫停");
		menu.add(0, 1, 1, "繼續");		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0: {
			mShowFlag = false;
		}
			break;
		case 1: {
			mShowFlag = false;
		}
			break;

		case 2: {
			releaseGps();
		}
			break;
		default:
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			AlertDialog.Builder alertDlg = new AlertDialog.Builder(this).setTitle("訊息");
			alertDlg.setMessage(Html.fromHtml("關閉後" + "<font color = 'yellow'><strong>『紀錄將會刪除』</strong></font>\n是否關閉？\n若要繼續測試請按<font color = 'yellow'><strong>『Home』鍵</strong></font>"));

			alertDlg.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				}
			});

			alertDlg.setPositiveButton("關閉", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {					
					finish();
				}
			});

			alertDlg.show();
		}
		return super.onKeyDown(keyCode, event);
	}
}
