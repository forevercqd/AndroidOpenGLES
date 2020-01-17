package com.android.chapter30;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.KeyEvent;

import com.android.chapter30.md2.IntMesh;
import com.android.chapter30.md2.MD2Loader;
import com.android.chapter30.md2.Model;

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

		MD2Loader ld = new MD2Loader();
		ld.setFactory(IntMesh.factory());

		try
		{
			Model model = ld.load(getResources().openRawResource(R.drawable.tris), 0.1f, "tris_t.jpg");
			mGlRender = new GLRender(model, this);
			// …Ë÷√‰÷»æ∆˜
			mGLSurfaceView.setRenderer(mGlRender);
		}catch (java.io.IOException ex){}

		setContentView(mGLSurfaceView);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		mGlRender.onKeyDown(keyCode, event);
		return super.onKeyDown(keyCode, event);
	}
}