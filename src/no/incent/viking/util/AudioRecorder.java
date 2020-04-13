package no.incent.viking.util;

import java.io.File;
import java.io.IOException;

import android.os.Environment;
import android.content.Context;
import android.content.Intent;
import android.content.ContentValues;
import android.content.ContentResolver;
import android.media.MediaRecorder;
import android.provider.MediaStore;
import android.net.Uri;
import android.util.Log;

public class AudioRecorder {
	private final String TAG = "VIKING";
	private File audioFile;
	private MediaRecorder mediaRecorder;
	private Context context;
	
	public AudioRecorder(Context context) {
		this.context = context;
	}
	
	public void startRecording(String filename) {
		File sampleDir = new File(Environment.getExternalStorageDirectory(), "viking/carevents/sound");
		
		try {
			audioFile = File.createTempFile(filename, ".3gp", sampleDir);
			
			mediaRecorder = new MediaRecorder();
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
			mediaRecorder.prepare();
			mediaRecorder.start();
		} catch(IOException ex) {
			Log.e(TAG, ex.getMessage());
		}
	}
	
	public void stopRecording() {
		mediaRecorder.stop();
		mediaRecorder.release();
		
		ContentValues contentValues = new ContentValues();
		contentValues.put(MediaStore.Audio.Media.TITLE, audioFile.getName());
		contentValues.put(MediaStore.Audio.Media.DATE_ADDED, (int) System.currentTimeMillis() / 1000);
		contentValues.put(MediaStore.Audio.Media.MIME_TYPE, "audio/3gpp");
		contentValues.put(MediaStore.Audio.Media.DATA, audioFile.getAbsolutePath());
		ContentResolver contentResolver = context.getContentResolver();
		
		Uri newUri = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues);
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
	}
	
	public File getAudioFile() {
		return audioFile;
	}
}
