package com.moga.example;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.bda.controller.Controller;
import com.bda.controller.ControllerListener;
import com.bda.controller.KeyEvent;
import com.bda.controller.MotionEvent;
import com.bda.controller.StateEvent;
import com.codebutler.android_websockets.WebSocketClient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity {
	private String ip = "192.168.1.135";
	private String path = "http://" + ip+":8080/?action=stream";
    private MjpegView mv = null;
    private TextView TV;
    private boolean arm = false;
    PWMTask pTask = new PWMTask();
    WebSocketClient client;
	Controller mController = null;
	final ExampleControllerListener mListener = new ExampleControllerListener();
	BackgroundRun br = new BackgroundRun();
	ProgressBar tb;
	String t1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		//br.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path);
		
		mv = (MjpegView) findViewById(R.id.mv);
		tb = (ProgressBar) findViewById(R.id.throttleBar);
		TV = (TextView) findViewById(R.id.geigerText);
		//new DoRead().execute(path);
		//mv.setSource(path);
        //if(result!=null) result.setSkip(1);
        mv.setDisplayMode(MjpegView.SIZE_FULLSCREEN);
        mv.showFps(false);
        
		mController = Controller.getInstance(this);
		mController.init();
		mController.setListener(mListener, null);
		br.execute(path);
		client = new WebSocketClient(URI.create("ws://"+ip+":8000/ws"), new WebSocketClient.Listener(){
            @Override
            public void onConnect() {
            	//System.out.println("commected");
                //Log.d(TAG, "Connected!");
            }

            @Override
            public void onMessage(final String message) {
                //Log.d(TAG, String.format("Got string message! %s", message));
            	//System.out.println(message);
            	runOnUiThread(new Runnable() {
            	     @Override
            	     public void run() {

            	    	 TV.setText(message+" cps");

            	    }

            	});
            	
            }

            @Override
            public void onMessage(byte[] data) {
                //Log.d(TAG, String.format("Got binary message! %s", toHexString(data));
            }

            @Override
            public void onDisconnect(int code, String reason) {
                //Log.d(TAG, String.format("Disconnected! Code: %d Reason: %s", code, reason));
            }

            @Override
            public void onError(Exception error) {
                //Log.e(TAG, "Error!", error);
            	System.out.println("ERROR:: "+error);
            }
        }, null);
        
        client.connect();

	}
	
	
	@Override
	protected void onDestroy() {
		mController.exit();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		if(mv!=null){
        	mv.stopPlayback();
        }
		mController.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mController.onResume();
	}
	
	public class DoRead extends AsyncTask<String, Void, MjpegInputStream> {
        protected MjpegInputStream doInBackground(String... url) {
            //TODO: if camera has authentication deal with it and don't just not work
            HttpResponse res = null;
            DefaultHttpClient httpclient = new DefaultHttpClient(); 
            HttpParams httpParams = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 5*1000);
            try {
                res = httpclient.execute(new HttpGet(URI.create(url[0])));
                if(res.getStatusLine().getStatusCode()==401){
                    //You must turn off camera User Access Control before this will work
                	System.out.println("turn off cam user access control");
                    return null;
                }
                return new MjpegInputStream(res.getEntity().getContent());  
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                System.out.println(e.toString());
                //Error connecting to camera
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.toString());
                //Error connecting to camera
            }
            return null;
        }

        protected void onPostExecute(MjpegInputStream result) {
            //mv.setSource(result);
            //if(result!=null) result.setSkip(1);
            mv.setDisplayMode(MjpegView.SIZE_FULLSCREEN);
            mv.showFps(true);
        }
    }
	
	class ExampleControllerListener implements ControllerListener {
		
		@Override
		public void onKeyEvent(KeyEvent event) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_BUTTON_A:
				if(event.getAction() == 0){
					arm = true;
					System.out.println("sleep");
				}
				break;

			case KeyEvent.KEYCODE_BUTTON_B:
				break;

			case KeyEvent.KEYCODE_BUTTON_START:
				break;
			case KeyEvent.KEYCODE_BUTTON_X:
				break;

			case KeyEvent.KEYCODE_BUTTON_Y:
				break;

			case KeyEvent.KEYCODE_BUTTON_SELECT:
				break;
			}
		}
		
		

		//ch1: 5.1 7.4 9.4
		//ch2:
		//ch3:
		//ch4:5.3 7.4 9.4
		@Override
		public void onMotionEvent(final MotionEvent event) {
			/*
			String ppm = "";
			ppm = percValue(event.getAxisValue(MotionEvent.AXIS_Z)) + " " +
					percValue(event.getAxisValue(MotionEvent.AXIS_RZ)) + " " +
					 percValue(event.getAxisValue(MotionEvent.AXIS_Y)) + " " +
					 percValue(event.getAxisValue(MotionEvent.AXIS_X)) + " .074";
			

			if(pTask.getStatus() == AsyncTask.Status.FINISHED){
				pTask = new PWMTask();
			}
			
			if((pTask.getStatus() != AsyncTask.Status.RUNNING)){
				pTask.execute(ppm);
			}
			*/
		}

		@Override
		public void onStateEvent(StateEvent event) {
			
		}
		
		public String percValue(float v1){
			float pVal;
			v1 = v1 + 1;
			v1=v1/100;
			pVal = (float) (.053 + (v1 * 2.05));
			return Float.toString(pVal);
		}
	}

	public class BackgroundRun extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... v) {
            //TODO: if camera has authentication deal with it and don't just not work
        	System.out.println("background");
        	float preX = 0, preY = -1, preZ = 0, preRZ = 0;
        	float axisY = -1;
        	while(1 == 1){
        		if(arm == false){
        			//System.out.println(mController.getKeyCode(Controller.KEYCODE_BUTTON_R1));
		        	final int buttonR = mController.getKeyCode(Controller.KEYCODE_BUTTON_R1);
		        	final int buttonL = mController.getKeyCode(Controller.KEYCODE_BUTTON_L1);
					final float axisX = mController.getAxisValue(Controller.AXIS_X);
					//final float axisY = mController.getAxisValue(Controller.AXIS_Y);
					final float axisZ = mController.getAxisValue(Controller.AXIS_Z);
					final float axisRZ = mController.getAxisValue(Controller.AXIS_RZ);
					String ppm = axisX + " " + axisY + " " + axisZ + " " + axisRZ + " " + buttonR ;
					
					
					
					if(buttonR == 0 & axisY < 1){
						axisY = (float) (axisY + .05);
					}
					if(buttonL == 0 & axisY > -1){
						axisY = (float) (axisY - .05);
					}
					//System.out.println(ppm);
					if(Math.abs(axisX - preX) > .05 || Math.abs(axisY - preY) > .05 || Math.abs(axisZ - preZ) > .05 || Math.abs(axisRZ - preRZ) > .05){
						ppm = percValue(axisZ) + " " +
								percValue(axisRZ * (-1)) + " " +
								 percValue(axisY) + " " +
								 percValue(axisX) + " .051";
						try{
							Socket s = null;
							PrintWriter out;
							s = new Socket(ip, 8181);
							out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);
							out.println(ppm);
							out.flush();
							out.close();
							s.close();
							tb.setProgress((int) ((axisY +1) * 1000));
						}catch(Exception e){
							System.out.println("PWM error " + e.toString());
						}
						preX = axisX;
						preY = axisY;
						preZ = axisZ;
						preRZ = axisRZ;
						
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		}else{
        			String ppm = ".074 .074 .051 .094 .051";
        			//System.out.println(ppm);
					try{
						Socket s = null;
						PrintWriter out;
						s = new Socket(ip, 8181);
						out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);
						out.println(ppm);
						out.flush();
						out.close();
						s.close();
					}catch(Exception e){
						System.out.println("PWM error " + e.toString());
					}
					try {
						Thread.sleep(7000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					ppm = ".074 .074 .051 .074 .051";
					try{
						Socket s = null;
						PrintWriter out;
						s = new Socket(ip, 8181);
						out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);
						out.println(ppm);
						out.flush();
						out.close();
						s.close();
					}catch(Exception e){
						System.out.println("PWM error " + e.toString());
					}
					System.out.println("wake");
					arm = false;
        		}
        	}
        	
        }

        protected void onPostExecute(String s) {
            
        }
        
        public String percValue(float v1){
			float pVal;
			v1 = v1 + 1;
			v1=v1/100;
			pVal = (float) (.053 + (v1 * 2.05));
			return Float.toString(pVal);
		}
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onBackPressed()
	{
		client.send("QUIT");
	     // code here to show dialog
	     super.onBackPressed();  // optional depending on your needs
	}
	
	


}
