package com.android.chapter28;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.view.KeyEvent;

public class GLRender implements Renderer
{
	private Context	mContext;

	public GLRender(Context context)
	{
		mContext = context;
	}

	private InputStream getFile(String name)
	{
		AssetManager am = mContext.getResources().getAssets();
		try
		{
			return am.open(name);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public void onDrawFrame(GL10 gl)
	{
		update();

		// Clear Color Buffer, Depth Buffer
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		GLU.gluLookAt(gl, 212, 60, 194, 186, 55, 171, 0, 1, 0);
		gl.glScalef(scaleValue, scaleValue * HEIGHT_RATIO, scaleValue);
		renderHeightMap(gl, heightMap);
	}

	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		height = (height == 0) ? 1 : height;

		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();

		GLU.gluPerspective(gl, 45, (float) width / height, 1, 1000);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

		try
		{
			loadRawFile("terrain.raw", heightMap);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}

	}

	/********************************************/
	// 高度图的大小
	private static final int	MAP_SIZE		= 1024;
	// 相邻顶点的距离
	private static final int	STEP_SIZE		= 16;
	// 保存高度数据
	private byte[]				heightMap		= new byte[MAP_SIZE * MAP_SIZE];
	// 地形的缩放比例
	private float				scaleValue		= .15f;
	// 缩放
	private boolean				zoomIn;
	private boolean				zoomOut;
	// 在高度方向的缩放比例，越大地形看起来越陡峭。
	private float				HEIGHT_RATIO	= 1.5f;
	// true为多边形渲染，false为线渲染
	private boolean				renderMode		= true;

	public void zoomOut(boolean zoom)
	{
		zoomIn = !zoomIn;
		zoomOut = zoom;
	}

	public void zoomIn(boolean zoom)
	{
		zoomOut = !zoomOut;
		zoomIn = zoom;
	}

	// 装载高度图
	private void loadRawFile(String strName, byte[] pHeightMap) throws IOException
	{
		InputStream input = getFile(strName);
		readBuffer(input, pHeightMap);
		input.close();

		for (int i = 0; i < pHeightMap.length; i++)
			pHeightMap[i] &= 0xFF;
	}
	
	// 读取指定大小的数据
	private static void readBuffer(InputStream in, byte[] buffer) throws IOException
	{
		int bytesRead = 0;
		int bytesToRead = buffer.length;
		while (bytesToRead > 0)
		{
			int read = in.read(buffer, bytesRead, bytesToRead);
			bytesRead += read;
			bytesToRead -= read;
		}
	}

	// 按高度设置顶点的颜色，越高的地方越亮
	private void setVertexColor(GL10 gl, byte[] pHeightMap, int x, int y, float colors[][], int col, int row)
	{
		float fColor = -0.15f + (height(pHeightMap, x, y) / 256.0f);
		colors[col][0] = 0.0f;
		colors[col][1] = 0.0f;
		colors[col][2] = fColor;
		colors[col][3] = 1.0f;
	}

	// 返回(x,y)点的高度
	private int height(byte[] pHeightMap, int X, int Y)
	{
		// 限制X,Y的值在0-1024之间
		int x = X % MAP_SIZE;
		int y = Y % MAP_SIZE;
		// 返回（x,y)的高度
		return pHeightMap[x + (y * MAP_SIZE)] & 0xFF;
	}

	// 更新
	private void update()
	{
		if (zoomIn) scaleValue += 0.001f;
		if (zoomOut) scaleValue -= 0.001f;
	}

	void renderHeightMap(GL10 gl, byte[] pHeightMap)
	{
		int X = 0, Y = 0;
		int x, y, z;

		// 颜色数组和顶点数组
		float colors[][] = new float[(MAP_SIZE / STEP_SIZE) * 4][4];
		short vertices[][] = new short[(MAP_SIZE / STEP_SIZE) * 4][3];

		// 确认高度图存在
		if (pHeightMap == null) { return; }

		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		
		// 循环渲染
		for (X = 0; X < MAP_SIZE; X += STEP_SIZE)
		{
			for (Y = 0; Y < MAP_SIZE; Y += STEP_SIZE)
			{
				x = X;
				y = height(pHeightMap, X, Y);
				z = Y;

				setVertexColor(gl, pHeightMap, x, z, colors, (Y / STEP_SIZE) * 4 + 0, 0);
				vertices[(Y / STEP_SIZE) * 4 + 0][0] = (short) x;
				vertices[(Y / STEP_SIZE) * 4 + 0][1] = (short) y;
				vertices[(Y / STEP_SIZE) * 4 + 0][2] = (short) z;

				x = X;
				y = height(pHeightMap, X, Y + STEP_SIZE);
				z = Y + STEP_SIZE;

				setVertexColor(gl, pHeightMap, x, z, colors, (Y / STEP_SIZE) * 4 + 1, 0);
				vertices[(Y / STEP_SIZE) * 4 + 1][0] = (short) x;
				vertices[(Y / STEP_SIZE) * 4 + 1][1] = (short) y;
				vertices[(Y / STEP_SIZE) * 4 + 1][2] = (short) z;

				x = X + STEP_SIZE;
				y = height(pHeightMap, X + STEP_SIZE, Y + STEP_SIZE);
				z = Y + STEP_SIZE;

				// 根据渲染模式设置顶点
				if (renderMode)
				{
					setVertexColor(gl, pHeightMap, x, z, colors, (Y / STEP_SIZE) * 4 + 3, 0);
					vertices[(Y / STEP_SIZE) * 4 + 3][0] = (short) x;
					vertices[(Y / STEP_SIZE) * 4 + 3][1] = (short) y;
					vertices[(Y / STEP_SIZE) * 4 + 3][2] = (short) z;

					x = X + STEP_SIZE;
					y = height(pHeightMap, X + STEP_SIZE, Y);
					z = Y;

					setVertexColor(gl, pHeightMap, x, z, colors, (Y / STEP_SIZE) * 4 + 2, 0);
					vertices[(Y / STEP_SIZE) * 4 + 2][0] = (short) x;
					vertices[(Y / STEP_SIZE) * 4 + 2][1] = (short) y;
					vertices[(Y / STEP_SIZE) * 4 + 2][2] = (short) z;
				}
				else
				{
					setVertexColor(gl, pHeightMap, x, z, colors, (Y / STEP_SIZE) * 4 + 2, 0);
					vertices[(Y / STEP_SIZE) * 4 + 2][0] = (short) x;
					vertices[(Y / STEP_SIZE) * 4 + 2][1] = (short) y;
					vertices[(Y / STEP_SIZE) * 4 + 2][2] = (short) z;

					x = X + STEP_SIZE;
					y = height(pHeightMap, X + STEP_SIZE, Y);
					z = Y;

					setVertexColor(gl, pHeightMap, x, z, colors, (Y / STEP_SIZE) * 4 + 3, 0);
					vertices[(Y / STEP_SIZE) * 4 + 3][0] = (short) x;
					vertices[(Y / STEP_SIZE) * 4 + 3][1] = (short) y;
					vertices[(Y / STEP_SIZE) * 4 + 3][2] = (short) z;
				}
			}

			gl.glVertexPointer(3, GL10.GL_SHORT, 0, makeShortBuffer(vertices));
			gl.glColorPointer(4, GL10.GL_FLOAT, 0, makeFloatBuffer(colors));
			// 选择渲染模式
			if (renderMode)
			{
				gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, (MAP_SIZE / STEP_SIZE) * 4);
			}
			else
			{
				gl.glDrawArrays(GL10.GL_LINES, 0, (MAP_SIZE / STEP_SIZE) * 4);
			}
		}

		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

	protected FloatBuffer makeFloatBuffer(float[][] arr)
	{
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * arr[0].length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		for (int i = 0; i < arr.length; i++)
		{
			fb.put(arr[i]);
		}
		fb.position(0);
		return fb;
	}

	protected ShortBuffer makeShortBuffer(short[][] arr)
	{
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * arr[0].length * 4);
		bb.order(ByteOrder.nativeOrder());
		ShortBuffer fb = bb.asShortBuffer();
		for (int i = 0; i < arr.length; i++)
		{
			fb.put(arr[i]);
		}
		fb.position(0);
		return fb;
	}

	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		if (event.getAction() != KeyEvent.ACTION_UP) { return false; }
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
		{
		}
		else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
		{
		}
		else if (keyCode == KeyEvent.KEYCODE_DPAD_UP)
		{
			zoomIn(!zoomIn);
		}
		else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
		{
			zoomOut(!zoomOut);
		}
		else if (keyCode == KeyEvent.KEYCODE_SPACE)
		{
			renderMode = !renderMode;
		}
		return false;
	}
}