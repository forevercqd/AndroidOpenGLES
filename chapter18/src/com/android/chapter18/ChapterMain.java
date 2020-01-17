package com.android.chapter18;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class ChapterMain extends Activity
{
	private GLSurfaceView mGLSurfaceView;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// ʵ����GLSurfaceView
		mGLSurfaceView = new GLSurfaceView(this);
		
		// ������Ⱦ��
		mGLSurfaceView.setRenderer(new GLRender());
		
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
}
