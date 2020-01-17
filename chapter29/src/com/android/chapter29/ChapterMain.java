package com.android.chapter29;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.KeyEvent;

public class ChapterMain extends Activity
{
	private GLSurfaceView mGLSurfaceView;
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
	
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		mGlRender.onKeyUp(keyCode,event);
		return true;
	}
}
