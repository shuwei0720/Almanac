package com.twm.gpsmapv2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class LogFileUtil {
	static String DEFAULT_PATH = Environment.getExternalStorageDirectory() + "/GpsMapV2.txt";
	// static private String mFilePath = "/sdcard/LogPhoto.txt" ; // init null
	private static String mFilePath = null;
	static Context mContext = null;

	/**
	 * @param path
	 *            (ex: /sdcard/log_file.txt)
	 */
	public static void setFileAbsolutePath(String path) {
		mFilePath = path;
	}

	/**
	 * @param name
	 *            Just set the file name (ex: "Log") , it saved on sdcard.
	 */
	public static void setFileName(String name) {
		mFilePath = Environment.getExternalStorageDirectory() + "/" + name
				+ ".txt";
	}

	/**
	 * Use for to write on device, it must be set before.
	 * 
	 * @param context
	 */
	public static void setContext(Context context) {
		mContext = context;
	}

	public static void writeToFile(String strLog, boolean bWriteAppend) {
		// if (!android.os.Environment.getExternalStorageState().equals(
		// android.os.Environment.MEDIA_MOUNTED)){
		// return;
		// }
		if (GpsMapV2Class.DEF_LOG_FILE) {
			File file;
			if (mFilePath != null) {
				file = new File(mFilePath);
			} else {
				file = new File(DEFAULT_PATH);
			}
			// Log.e("ERROR", "DEFAULT_PATH : " + mFilePath);
			String strMessage = getTime() + "   " + strLog + "\r\n";
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					Log.e("ERROR", "File Can't Create" + mFilePath);
				}
			}

			byte[] data = strMessage.getBytes();
			try {
				FileOutputStream fos = new FileOutputStream(file, bWriteAppend);
				fos.write(data);
				fos.close();

			} catch (FileNotFoundException e) {
				// writeToDevice("File FileNotFoundException : " + strLog);
				Log.e("ERROR", "File FileNotFoundException");
			} catch (IOException e) {
				// writeToDevice("File IOException : " + strLog);
				Log.e("ERROR", "File IOException");
			} catch (SecurityException e) {
				// writeToDevice("File SecurityException : " + strLog);
				Log.e("ERROR", "File SecurityException");
			}
		}
	}

	public static void writeToFile(String strLog) {
		// if (!android.os.Environment.getExternalStorageState().equals(
		// android.os.Environment.MEDIA_MOUNTED)){
		// return;
		// }
		if (GpsMapV2Class.DEF_LOG_FILE) {
			File file;

			if (mFilePath != null) {
				file = new File(mFilePath);
			} else {
				file = new File(DEFAULT_PATH);
			}
			// Log.e("ERROR", "DEFAULT_PATH : " + mFilePath);
			String strMessage = getTime() + "   " + strLog + "\r\n";
			if (!file.exists()) {
				try {
					file.createNewFile();
					Log.e("ERROR", "File Create");
				} catch (IOException e) {
					Log.e("ERROR", "File Can't Create");
				}
			}

			byte[] data = strMessage.getBytes();
			try {
				FileOutputStream fos = new FileOutputStream(file, true);
				fos.write(data);
				fos.close();
			} catch (FileNotFoundException e) {

				// writeToDevice("File FileNotFoundException : " + strLog);
				Log.e("ERROR", "File FileNotFoundException");
			} catch (IOException e) {
				// writeToDevice("File IOException : " + strLog);
				Log.e("ERROR", "File IOException");
			} catch (SecurityException e) {
				// writeToDevice("File SecurityException : " + strLog);
				Log.e("ERROR", "File SecurityException");
			}
		}
	}

	public static void writeToDevice(String strLog) {
		if (GpsMapV2Class.DEF_LOG_FILE) {
			try {
				FileOutputStream fos = mContext.openFileOutput("Weather.txt",
						Context.MODE_APPEND);
				String strMessage = getTime() + "   " + strLog + "\r\n";
				byte[] data = strMessage.getBytes();
				fos.write(data);
				fos.close();

			} catch (FileNotFoundException e) {
				Log.e("ERROR", "writeToDevice File FileNotFoundException");
			} catch (IOException e) {
				Log.e("ERROR", "writeToDevice File IOException");
			} catch (SecurityException e) {
				Log.e("ERROR", "writeToDevice File SecurityException");
			} catch (Exception e) {
				Log.e("ERROR", "writeToDevice File Exception");
			}
		}
	}

	public static String getTime() {
		Calendar c = Calendar.getInstance();
		long milliSecond = System.currentTimeMillis() % 1000;
		Date date = c.getTime();
		if (milliSecond < 10)
			return date.toLocaleString() + ".00" + milliSecond;
		else if (milliSecond < 100)
			return date.toLocaleString() + ".0" + milliSecond;
		else
			return date.toLocaleString() + "." + milliSecond;
	}

}
