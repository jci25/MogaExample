package com.moga.example;

import android.widget.TextView;

import com.bda.controller.ControllerListener;
import com.bda.controller.KeyEvent;
import com.bda.controller.MotionEvent;
import com.bda.controller.StateEvent;

public class ExampleControllerListener implements ControllerListener {
	TextView tv1, tv2, tv3, tv4;
	
	ExampleControllerListener(TextView t1, TextView t2, TextView t3, TextView t4){
		tv1 = t1;
		tv2 = t2;
		tv3 = t3;
		tv4 = t4;
	}

	@Override
	public void onKeyEvent(KeyEvent event) {

		
	}

	@Override
	public void onMotionEvent(MotionEvent event) {
		tv1.setText(Float.toString(event.getAxisValue(MotionEvent.AXIS_X)));
		tv2.setText(Float.toString(event.getAxisValue(MotionEvent.AXIS_Y)));
		//tv3.setText(Float.toString(event.getAxisValue(MotionEvent.AXIS_Z)));
		//tv4.setText(Float.toString(event.getAxisValue(MotionEvent.AXIS_RZ)));
	}

	@Override
	public void onStateEvent(StateEvent event) {
		
	}
}