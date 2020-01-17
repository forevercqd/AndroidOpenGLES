package com.android.chapter31;

import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.os.SystemClock;

import com.android.chapter31.ms3d.IMS3DModel;
import com.android.chapter31.utils.TextureFactory;
import com.android.chapter31.utils.TextureInfo;

public class GLRender implements Renderer
{

	/**
	 * 是否渲染骨骼Helper
	 */
	public static boolean gbShowJoints = false;
	/**
	 * 是否自动播放动画
	 */
	public static boolean gbEnableAnimation = true;
	
	private Context				mContext;

	private IMS3DModel			mModel;

	private int					mFrames;
	private int					mMsPerFrame;
	private final static int	SAMPLE_PERIOD_FRAMES	= 10;
	private final static float	SAMPLE_FACTOR			= 1.0f / SAMPLE_PERIOD_FRAMES;
	private long				mStartTime;

	public float				mfAngleY				= 0.0f;
	public float				mfAngleX				= 0.0f;

	/**
	 * 相机坐标
	 */
	private float				mfEyeX, mfEyeY, mfEyeZ;
	private float				mfCenterX, mfCenterY, mfCenterZ;

	/**
	 * 记录上次触屏位置的坐标
	 */
	private float				mPreviousX, mPreviousY;

	public GLRender(Context context)
	{
		mContext = context;
	}

	public void onDrawFrame(GL10 gl)
	{
		// 一般的opengl程序，首先要做的就是清屏
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// 紧接着设置模型视图矩阵
		setUpCamera(gl);

		// 渲染物体
		drawModel(gl);

		// 更新时间
		updateTime();
	}

	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		// 设置视口
		gl.glViewport(0, 0, width, height);

		// 设置投影矩阵
		float ratio = (float) width / height;// 屏幕宽高比
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluPerspective(gl, 45.0f, ratio, 1, 5000);
		// 每次修改完GL_PROJECTION后，最好将当前矩阵模型设置回GL_MODELVIEW
		gl.glMatrixMode(GL10.GL_MODELVIEW);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		// 全局性设置
		gl.glDisable(GL10.GL_DITHER);

		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
		// 设置清屏背景颜色
		gl.glClearColor(0.0f, 0.0f, 0.5f, 1);
		// 设置着色模型为平滑着色
		gl.glShadeModel(GL10.GL_SMOOTH);

		// 启用背面剪裁
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glCullFace(GL10.GL_BACK);
		// 启用深度测试
		gl.glEnable(GL10.GL_DEPTH_TEST);
		// 禁用光照和混合
		gl.glDisable(GL10.GL_LIGHTING);
		gl.glDisable(GL10.GL_BLEND);

		loadModel(gl, R.drawable.zombie, R.drawable.zombie_t);

		mfAngleY = mfAngleX = 0.0f;
		mfEyeX = mModel.getSphereCenter().x;
		mfEyeY = mModel.getSphereCenter().y;
		mfEyeZ = mModel.getSphereCenter().z + mModel.getSphereRadius() * 2.8f;
		mfCenterX = mfCenterZ = 0;
		mfCenterY = mfEyeY;
	}

	/**
	 * 设置相机矩阵
	 * 
	 * @param gl
	 */
	private void setUpCamera(GL10 gl)
	{
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		GLU.gluLookAt(gl, mfEyeX, mfEyeY, mfEyeZ, mfCenterX, mfCenterY, mfCenterZ, 0, 1, 0);
	}

	/**
	 * 渲染模型
	 * 
	 * @param gl
	 */
	private void drawModel(GL10 gl)
	{
		gl.glPushMatrix();
		{
			// 首先对模型进行旋转
			gl.glRotatef(mfAngleX, 1, 0, 0);// 绕X轴旋转
			gl.glRotatef(mfAngleY, 0, 1, 0);// 绕Y轴旋转
			if (gbEnableAnimation && mModel.containsAnimation())
			{
				// 如果模型有动画，那么按时间就更新动画
				if (mMsPerFrame > 0)
				{
					mModel.animate(mMsPerFrame * 0.001f);// 将毫秒数转化为秒, /1000
				}
				mModel.fillRenderBuffer();// 更新顶点缓存
			}
			mModel.render(gl);// 渲染模型
			if (gbShowJoints)
			{
				mModel.renderJoints(gl);// 渲染关节，骨骼
			}
		}
		gl.glPopMatrix();
	}

	private void updateTime()
	{
		long time = SystemClock.uptimeMillis();
		if (mStartTime == 0)
		{
			mStartTime = time;
		}
		if (mFrames++ == SAMPLE_PERIOD_FRAMES)
		{
			mFrames = 0;
			long delta = time - mStartTime;
			mStartTime = time;
			mMsPerFrame = (int) (delta * SAMPLE_FACTOR);
		}
	}

	/**
	 * 载入模型--单个纹理
	 * 
	 * @param gl
	 * @param idxModel
	 *            - 模型资源索引
	 * @param idxTex
	 *            - 纹理索引
	 */
	private void loadModel(GL10 gl, int idxModel, int idxTex)
	{
		loadModel(gl, idxModel, new int[]{idxTex});
	}

	/**
	 * 载入模型--多个纹理
	 * 
	 * @param gl
	 * @param idxModel
	 *            - 模型资源索引
	 * @param pIdxTex
	 *            - 纹理数组
	 */
	private void loadModel(GL10 gl, int idxModel, int[] pIdxTex)
	{
		try
		{
			TextureInfo[] pTexInfos = new TextureInfo[pIdxTex.length];
			mModel = new IMS3DModel();

			// 打开模型二进制流
			InputStream is = mContext.getResources().openRawResource(idxModel);

			if (mModel.loadModel(is))
			{
				// 载入模型成功，开始载入纹理
				for (int i = 0; i < pTexInfos.length; i++)
				{
					pTexInfos[i] = new TextureInfo();
					// 得到创建成功的纹理对象名称
					pTexInfos[i].mTexID = TextureFactory.getTexture(mContext, gl, pIdxTex[i]);
				}
				// 赋予纹理
				mModel.setTexture(pTexInfos);
			}
			else
			{
				System.out.println("Load Model Failed. IdxModel:" + idxModel);
			}

			is.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
