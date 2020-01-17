package com.android.chapter26;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;

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
		
		mGLRender=new GLRender(this);
		
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
	
	public boolean onTouchEvent(MotionEvent event)
	{

		// »ñµÃ´¥ÃþµÄ×ø±ê
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction())
		{
			// ´¥ÃþÆÁÄ»Ê±¿Ì
			case MotionEvent.ACTION_DOWN:
				mGLRender.startDrag(new Point(x,y));
				break;
			// ´¥Ãþ²¢ÒÆ¶¯Ê±¿Ì
			case MotionEvent.ACTION_MOVE:
				mGLRender.drag(new Point(x,y));
				break;
			// ÖÕÖ¹´¥ÃþÊ±¿Ì
			case MotionEvent.ACTION_UP:
				//mGLRender.drag(new Point(x,y));
				break;
		}
		return true;
	} 
	
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		if ( keyCode == KeyEvent.KEYCODE_SPACE )
		{
			mGLRender.reset();
		}
		return false;
	}
}