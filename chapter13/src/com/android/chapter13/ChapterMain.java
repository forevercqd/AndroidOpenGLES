package com.android.chapter13;

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
		
		//  µ¿˝ªØGLSurfaceView
		mGLSurfaceView = new GLSurfaceView(this);
		
		mGLRender=new GLRender(this);
		// …Ë÷√‰÷»æ∆˜
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
	
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		mGLRender.onKeyDown(keyCode, event);
		return super.onKeyDown(keyCode, event);
	}
}