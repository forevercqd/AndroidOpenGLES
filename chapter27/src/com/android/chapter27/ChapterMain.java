package com.android.chapter27;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;

public class ChapterMain extends Activity
{
    private GLSurfaceView mGLSurfaceView;
    private GLRender mGlRender;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //  µ¿˝ªØGLSurfaceView
        mGLSurfaceView = new GLSurfaceView(this);
        
        mGlRender = new GLRender();
        
        // …Ë÷√‰÷»æ∆˜
        mGLSurfaceView.setRenderer(mGlRender);

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
    
	public boolean onTouchEvent(final MotionEvent ev)
	{
		mGlRender.onTouchEvent(ev);
		return true;
	}
}