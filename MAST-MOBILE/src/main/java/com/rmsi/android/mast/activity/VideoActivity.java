package com.rmsi.android.mast.activity;


import java.io.File;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.Toast;

import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.domain.Media;
import com.rmsi.android.mast.util.CommonFunctions;

public class VideoActivity extends Activity implements Callback
{

	Context context;
	MediaRecorder recorder;
	File file;
	Media media_video=new Media();
	SurfaceHolder holder;
	CommonFunctions cf = CommonFunctions.getInstance();
	boolean recording = false;
	Long featureId = 0L;
	Long groupId=0L;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);
		context=this;
		Bundle extras = getIntent().getExtras();
		if (extras != null) 
		{
			featureId = extras.getLong("featureid");			
		}
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		recorder = new MediaRecorder();

		//Initializing context in common functions in case of a crash
		try{CommonFunctions.getInstance().Initialize(getApplicationContext());}catch(Exception e){}

		SurfaceView cameraView = (SurfaceView) findViewById(R.id.surfaceView1);
		final Button record = (Button)findViewById(R.id.button1);

		DisplayMetrics displaymetrics = new DisplayMetrics(); //DisplayMetrics to set height of surface view
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics); //according to device.
		int height = displaymetrics.heightPixels;
		int newHeight = (height/5)*4;	
		LayoutParams params = cameraView.getLayoutParams();
		params.height=newHeight;

		cameraView.setLayoutParams(params);

		holder = cameraView.getHolder();
		holder.addCallback(this);
		record.setOnClickListener(new Button.OnClickListener()
		{
			public void onClick(View v) 
			{
				if (recording) {
					recorder.stop();
					record.setText("Record");
					recording = false;
					recorder.release();
					try {
						groupId = cf.getGroupId();
						media_video.setFeatureId(featureId);
						media_video.setPath(file.getAbsolutePath());
						media_video.setId(groupId);
						media_video.setType("video");
						DbController.getInstance(context).saveMedia(media_video);
						Log.i("Database Insertion", "Video Inserted Succefully");
					} catch (Exception e) {
						e.printStackTrace();
						Log.i("Database Problem", "Problem in data insert operation");
					}
					Intent result = new Intent();
					result.putExtra("Group_id",groupId);
					setResult(RESULT_OK, result);

					finish();
				}
				else 
				{
					initRecorder();
					prepareRecorder();
					recorder.start();
					record.setText("Stop");
					recording = true;

				}
			}
		});
	}

	private void initRecorder() {
		recorder = new MediaRecorder(); //initialize MediaRecorder
		recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

		CamcorderProfile cpVga = CamcorderProfile.get(CamcorderProfile.QUALITY_QVGA);//Res 320*240;
		recorder.setProfile(cpVga);
		recorder.setOutputFile(this.initFile().getPath()); //initFile is the name of the file 
		recorder.setMaxDuration(50000); // 50 seconds
		recorder.setMaxFileSize(3000000); // Approximately 5 megabytes
		//	recorder.setOrientationHint(90);
		Log.i("Init", "Initialize done");

	}

	private void prepareRecorder() {
		recorder.setPreviewDisplay(holder.getSurface()); //Preview

		try {
			recorder.prepare();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i("Prepare", "Prepare done");
	}

	private File initFile() {
		//File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "Vaibhav"); //set folder Vaibhav  
		File dir = new File (Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+CommonFunctions.parentFolderName+"/"+CommonFunctions.mediaFolderName);																											//	 in Movie dir

		if (!dir.exists()) {
			Log.wtf("Video Cap","Failed to create storage directory: "+ dir.getPath());
			Toast.makeText(this, "not record", Toast.LENGTH_SHORT).show();
			file = null;
		} 
		else {
			java.util.Date date= new java.util.Date();
			String timeStamp = new SimpleDateFormat("MMdd_HHmmss").format(date.getTime());
			file = new File(dir.getPath()+ File.separator +"mast_"+ timeStamp + ".mp4"); //set the file name in mp4 format
		}
		return file;
	}

	public void surfaceCreated(SurfaceHolder holder) {
		prepareRecorder();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		if (recording) {
			recorder.stop();
			recording = false;

		}
		recorder.release();
		finish();


	}

}