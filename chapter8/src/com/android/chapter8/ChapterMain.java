package com.android.chapter8;

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
		
		// 实例化GLSurfaceView
		mGLSurfaceView = new GLSurfaceView(this);
		
		//构建渲染器
		mGLRender = new GLRender(this);
		// 设置渲染器
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
	
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		mGLRender.onKeyUp(keyCode, event);
		return false;
	}
}