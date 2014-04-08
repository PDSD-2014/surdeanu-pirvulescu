package ro.pirvulescusurdeanu.septica.utils;

import android.util.Log;

public class Logger {
	private final static String TAG = "Septica";
	
	public static void info(String msg) {
		Log.i(Logger.TAG, msg);
	}
	
	public static void info(String msg, Throwable tr) {
		Log.i(Logger.TAG, msg, tr);
	}
	
	public static void warning(String msg) {
		Log.w(Logger.TAG, msg);
	}
	
	public static void warning(Throwable tr) {
		Log.w(Logger.TAG, tr);
	}
	
	public static void error(String msg) {
		Log.e(Logger.TAG, msg);
	}
	
	public static void error(String msg, Throwable tr) {
		Log.e(Logger.TAG, msg, tr);
	}
}
