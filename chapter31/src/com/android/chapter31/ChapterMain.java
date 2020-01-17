package com.android.chapter31;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.KeyEvent;

public class ChapterMain extends Activity
{

	private GLSurfaceView	mGLSurfaceView;
	
	private GLRender mGlRender;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		//  µ¿˝ªØGLSurfaceView
		mGLSurfaceView = new GLSurfaceView(this);
		
		mGlRender = new GLRender(this);
		// …Ë÷√‰÷»æ∆˜
		mGLSurfaceView.setRenderer(mGlRender);
		
		mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		
		setContentView(mGLSurfaceView);
	}

	protected void onResume()
	{
		super.onResume();
	}

	protected void onPause()
	{
		super.onPause();
	}
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		return super.onKeyDown(keyCode, event);
	}
}