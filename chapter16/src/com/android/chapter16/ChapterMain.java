package com.android.chapter16;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.KeyEvent;

public class ChapterMain extends Activity
{
	private GLSurfaceView	mGLSurfaceView;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		//  µ¿˝ªØGLSurfaceView
		mGLSurfaceView = new GLSurfaceView(this);

		// …Ë÷√‰÷»æ∆˜
		mGLSurfaceView.setRenderer(new GLRender(this));

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


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		switch (keyCode)
		{
			case KeyEvent.KEYCODE_L:
				GLRender.toggleLighting();
				break;
			case KeyEvent.KEYCODE_F:
				GLRender.switchToNextFilter();
				break;
			case KeyEvent.KEYCODE_SPACE:
				GLRender.switchToNextObject();
				break;
			case KeyEvent.KEYCODE_DPAD_CENTER:
				GLRender.xSpeed = GLRender.ySpeed = 0;
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				GLRender.ySpeed -= 0.1f;
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				GLRender.ySpeed += 0.1f;
				break;
			case KeyEvent.KEYCODE_DPAD_UP:
				GLRender.xSpeed -= 0.1f;
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				GLRender.xSpeed += 0.1f;
				break;
		}
		return super.onKeyDown(keyCode, event);
	}
}
