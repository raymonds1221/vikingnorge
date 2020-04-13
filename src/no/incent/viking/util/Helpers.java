package no.incent.viking.util;

import no.incent.viking.R;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.Writer;
import java.io.OutputStreamWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Helpers {
	private static final String TAG = "VIKING";
	public static Object lockObject = new Object();
	
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		
		if(networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}
	
	public static void showMessage(Context context, String title, String message) {
		final AlertDialog alert = new AlertDialog.Builder(context)
		.setTitle(title)
		.setMessage(message)
		.setCancelable(false)
		.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).create();
		alert.show(); 
	}
	
	public static void showMessage(Context context, String title, String message, DialogInterface.OnClickListener action) {
		final AlertDialog alert = new AlertDialog.Builder(context)
								.setTitle(title)
								.setMessage(message)
								.setCancelable(false)
								.setNeutralButton("OK", action)
								.create();
		alert.show();
	}
	
	public static byte[] toByteArray(InputStream is) {
		byte[] buffer = new byte[2048];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try {
			while((is.read(buffer, 0, buffer.length)) > -1) {
				baos.write(buffer);
			}
			return baos.toByteArray();
		} catch(IOException ex) {
			Log.e(TAG, ex.getMessage());
		}
		
		return null;
	}
	
	public static String parseXMLNode(String xml, String node) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(new InputSource(new StringReader(xml)));
			NodeList nl = document.getElementsByTagName(node);
			
			return nl.item(0).getFirstChild().getNodeValue();
		} catch(ParserConfigurationException ex) {
			Log.e(TAG, ex.getMessage());
		} catch(IOException ex) {
			Log.e(TAG, ex.getMessage());
		} catch(SAXException ex) {
			Log.e(TAG, ex.getMessage());
		}
		
		return "";
	}
	
	public static Bitmap resizeBitmap(Context context, Bitmap bitmap, int newWidth, int newHeight) {
		Bitmap resizedBitmap = null;
		
		if(bitmap != null) {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			
			float scaleWidth = ((float) newWidth) / width;
			float scaleHeight = ((float) newHeight) / height;
			
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			
			resizedBitmap  = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
			
		} else {
			resizedBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.car_img_frame);
		}
		
		return resizedBitmap;
	}
	
	public static Typeface getArialFont(Context context) {
		Typeface arialFont = Typeface.createFromAsset(context.getAssets(), "ArialNor.ttf");
		return arialFont;
	}
	
	public static final File createDirectory(String name) {
		File f = new File(Environment.getExternalStorageDirectory(), name);
		
		if(f.exists()) {
			return f;
		} else {
			if(f.mkdir())
				return f;
		}
		return null;
	}
	
	public static final void saveFile(File f, byte[] data, boolean replace) {
		try {
			if(f.exists() && replace)
				f.delete();
			
			if(data != null) {
				FileOutputStream fos = new FileOutputStream(f);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				fos.write(data);
				bos.flush();
				bos.close();
				fos.flush();
				fos.close();
			}
		} catch(IOException ex) {
			Log.e(TAG, ex.getMessage());
		}
	}
	
	public static final void saveFileFromBitmap(File file, Bitmap bitmap, Bitmap.CompressFormat format) {
		if(bitmap == null)
			throw new NullPointerException("bitmap is null");
		try {
			OutputStream outputStream = new FileOutputStream(file);
			bitmap.compress(format, 100, outputStream);
			outputStream.flush();
			outputStream.close();
		} catch(IOException ex) {
			Log.e(TAG, ex.getMessage());
		}
	}
	
	public static void writeNoMedia() {
		File f = new File(Environment.getExternalStorageDirectory(), "viking/.nomedia");
		try {
			FileOutputStream fos = new FileOutputStream(f);
			Writer writer = new OutputStreamWriter(fos, "utf-8");
			writer.write("");
			writer.close();
		} catch (IOException ex) {
			Log.e(TAG, ex.getMessage());
		}
		
	}
	
	public static final String getLastPathSegment(String path) {
		String[] segments = path.split("/");
		if(segments.length > 0) {
			return segments[segments.length - 1];
		}
		return null;
	}
	
	public static final boolean isValidDownloadFile(String filename) {
		if(filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".png") ||
			filename.endsWith(".gif") || filename.endsWith(".bmp") || filename.endsWith(".3gp")) {
				return true;
			}
		return false;
	}
	
	
	public static class Constants {
		public static boolean mIsLoggedIn = false;
		public static int mNotLoggedInCount = 0;
		public static final int PAGE_REGISTER = 0x000021;
		public static final int PAGE_DAMAGEREPORT = 0x000031;
		public static final int RESULT_REGISTERED = 0X000022;
		public static final int RESULT_DAMAGE_REPORT_SUCCESS = 0x000032;
		public static boolean mDirectoryCreated = false;
		public static boolean mFinishRequestingPhoneCategories = false;
		public static boolean mFinishRequestingTraffics = false;
		public static boolean mFinishRequestingAllCars = false;
		public static boolean mMyCarRequested = false;
	}
}
