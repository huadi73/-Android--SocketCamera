package huadi.com.socketcamera;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

//http://www.cnblogs.com/GoodHelper/archive/2011/07/08/android_socket_chart.html
public class MainActivity extends Activity implements SurfaceHolder.Callback,
	Camera.PreviewCallback
{
	String id = "";

	SurfaceView surfaceView = null;
	SurfaceHolder surfaceHolder = null;
	Camera camera = null;

	String userName;
	String serverIP;
	int port;
	boolean isConnect = false;

	int currentCameraId = 0;
	boolean isVideoSending = false; // 正在傳影像
	int videoWidth = 0;
	int videoHeight = 0;
	int videoQuality; // 畫質
	int videoFormat = 0; // 影像格式

	Menu mmenu = null; // 為了另一可以控制另一顆按鈕

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 保持螢幕亮著

		surfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);

		if (Camera.getNumberOfCameras() == 1) // 只有一個鏡頭就沒得選啦
			mmenu.findItem(R.id.action_Camchange).setEnabled(false);

		id = UUID.randomUUID().toString(); // 裝置ID
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		mmenu = menu;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle item selection
		switch (item.getItemId())
		{
			case R.id.action_connect:
				if (!isConnect) // 連接
				{
					Thread thread = new CommandSend("CONNECT|" + userName + "|" + id + "|", serverIP, port);
					thread.start();
					isConnect = true;
					item.setTitle(R.string.action_disconnect);
					mmenu.findItem(R.id.action_video).setEnabled(true); // 可以按下影像按鈕

					// Thread thread2 = new Receive(serverIP, port);
					// thread2.start();
				}
				else
				// 段開
				{
					Thread thread = new CommandSend("DISCONNECT|" + userName + "|" + id + "|", serverIP, port);
					thread.start();
					isConnect = false;
					item.setTitle(R.string.action_connect);

					mmenu.findItem(R.id.action_video).setEnabled(false); // 禁止影像傳輸按鈕
					mmenu.findItem(R.id.action_video).setTitle(R.string.action_videoStart);
					isVideoSending = false;
				}
				return true;
			case R.id.action_video:
				if (!isVideoSending) // 開始傳輸
				{
					isVideoSending = true;
					item.setTitle(R.string.action_videoStop);
				}
				else
				// 停止
				{
					isVideoSending = false;
					item.setTitle(R.string.action_videoStart);
				}
				return true;
			case R.id.action_Camchange: // 切換前後相機
				Thread t = new Thread()
				{
					public void run()
					{
						camera.setPreviewCallback(null);
						camera.stopPreview();
						camera.release();

						if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK)
							currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
						else
							currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

						camera = Camera.open(currentCameraId);

						try
						{
							camera.setPreviewDisplay(surfaceHolder);
							camera.setDisplayOrientation(90); // 攝影機旋轉
							camera.startPreview();
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
				};
				t.start();
				return true;
			case R.id.action_settings:
				Intent intent = new Intent(this, SettingActivity.class);
				startActivity(intent);
				return true;
			case R.id.action_about:
				new AlertDialog.Builder(this).setTitle("關於").setMessage("huadi73@gmail.com").show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera)
	{
		if (isVideoSending)
		{
			if (data != null)
			{
				YuvImage image = new YuvImage(data, videoFormat, videoWidth, videoHeight, null);
				ByteArrayOutputStream outstream = new ByteArrayOutputStream();
				image.compressToJpeg(new Rect(0, 0, videoWidth, videoHeight), videoQuality, outstream);

				Thread thread = new FileSend(outstream, userName, id, serverIP, port); // 將圖發出去
				thread.start();
			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		// 當surfaceView 大小或格式改變時呼叫
		if (camera == null)
			return;

		camera.stopPreview();
		camera.setPreviewCallback(this); // 會呼叫 onPreviewFrame
		camera.setDisplayOrientation(90); // 攝影機旋轉

		Camera.Parameters parameters = camera.getParameters();
		//parameters.setPreviewFpsRange(20, 60);
		parameters.setPictureFormat(ImageFormat.JPEG);
		// parameters.setPreviewSize(videoWidth, videoHeight);
		// camera.setParameters(parameters); // android2.3.3以後不需要

		Size size = parameters.getPreviewSize();
		videoWidth = size.width;
		videoHeight = size.height;
		videoFormat = parameters.getPreviewFormat();

		camera.startPreview();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		// SurfaceView 剛被實作時
		if (camera != null)
		{
			try
			{
				camera.setPreviewDisplay(surfaceHolder);
				camera.startPreview();
			}
			catch (IOException e)
			{
				// TODO 自動產生的 catch 區塊
				e.printStackTrace();
			}
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		// TODO 自動產生的方法 Stub
		if (null != camera)
		{
			camera.setPreviewCallback(null); // 必在 stopPreview 前
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) // 螢幕選轉時會呼叫
	{
		super.onConfigurationChanged(newConfig);

		// Checks the orientation of the screen
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
		{
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);

			Toast.makeText(this, "landscape\n" + dm.widthPixels + " , " + dm.heightPixels, Toast.LENGTH_SHORT).show();
		}
		else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);

			Toast.makeText(this, "portrait\n" + dm.widthPixels + " , " + dm.heightPixels, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onStart()// 重新啟動的時候
	{
		surfaceHolder = surfaceView.getHolder(); // 綁定SurfaceView，取得SurfaceHolder對像
		surfaceHolder.addCallback(this); // SurfaceHolder加入回調接口
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// 設置顯示器類型，setType必須設置
		// 讀取配置文件
		SharedPreferences preParas = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
		userName = preParas.getString("userName", "Huadi");
		serverIP = preParas.getString("serverIP", "192.168.4.1");

		String tempStr = preParas.getString("port", "9527");
		port = Integer.parseInt(tempStr);

		tempStr = preParas.getString("videoQuality", "100");
		videoQuality = Integer.parseInt(tempStr);

		super.onStart();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		camera = openCameraGingerbread();
	}

	private Camera openCameraGingerbread() // 有的裝置只有前(後)鏡頭
	{
		int cameraCount = 0;
		Camera cam = null;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		cameraCount = Camera.getNumberOfCameras();

		for (int camIdx = 0; camIdx < cameraCount; camIdx++)
		{
			Camera.getCameraInfo(camIdx, cameraInfo);
			// if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
			// {
			try
			{
				cam = Camera.open(camIdx);
				currentCameraId = camIdx;
				// Log.e("id", "" + camIdx + "/" + cameraCount );
			}
			catch (RuntimeException e)
			{
				Log.e("Camera", "Camera failed to open: " + e.getLocalizedMessage());
			}
			// }
		}

		return cam;
	}

	// @Override
	// protected void onPause()
	// {
	// if (null != camera)
	// {
	// camera.setPreviewCallback(null); // 必在 stopPreview 前
	// camera.stopPreview();
	// camera.release();
	// camera = null;
	// }
	// }

}
