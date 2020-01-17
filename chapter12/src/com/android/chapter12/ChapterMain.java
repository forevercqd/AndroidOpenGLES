package com.android.chapter12;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.KeyEvent;

public class ChapterMain extends Activity
{
	private GLSurfaceView mGLSurfaceView;
	
	private GLRender mGLRender;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// ÊµÀý»¯GLSurfaceView
		mGLSurfaceView = new GLSurfaceView(this);
		
		//ÊµÀý»¯äÖÈ¾Æ÷
		mGLRender = new GLRender(this);
		
		// ÉèÖÃäÖÈ¾Æ÷
		mGLSurfaceView.setRenderer(mGLRender);
		
		setContentView(mGLSurfaceView);
	}
	
    @Override
    protected void onResume()
	{
		super.onResume();
		mGLSurfaceView.onResume();
	}


	@Override
	protected void onPause()
	{
		super.onPause();
		mGLSurfaceView.onPause();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode)
		{
		case KeyEvent.KEYCODE_SPACE:
			mGLRender.ChangeScreen();
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
			mGLRender.UseMasking();
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
}