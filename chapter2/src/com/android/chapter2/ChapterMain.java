package com.android.chapter2;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class ChapterMain extends Activity
{
	private GLSurfaceView mGLSurfaceView;
	
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		//  µ¿˝ªØGLSurfaceView
		mGLSurfaceView = new GLSurfaceView(this);
		
		// …Ë÷√‰÷»æ∆˜
		mGLSurfaceView.setRenderer(new GLRender());
		
		setContentView(mGLSurfaceView);
	}
	
    protected void onResume()
	{
		super.onResume();
		mGLSurfaceView.onResume();
	}


	protected void onPause()
	{
		super.onPause();
		mGLSurfaceView.onPause();
	}
}
