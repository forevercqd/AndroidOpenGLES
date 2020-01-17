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
	 * �Ƿ���Ⱦ����Helper
	 */
	public static boolean gbShowJoints = false;
	/**
	 * �Ƿ��Զ����Ŷ���
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
	 * �������
	 */
	private float				mfEyeX, mfEyeY, mfEyeZ;
	private float				mfCenterX, mfCenterY, mfCenterZ;

	/**
	 * ��¼�ϴδ���λ�õ�����
	 */
	private float				mPreviousX, mPreviousY;

	public GLRender(Context context)
	{
		mContext = context;
	}

	public void onDrawFrame(GL10 gl)
	{
		// һ���opengl��������Ҫ���ľ�������
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// ����������ģ����ͼ����
		setUpCamera(gl);

		// ��Ⱦ����
		drawModel(gl);

		// ����ʱ��
		updateTime();
	}

	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		// �����ӿ�
		gl.glViewport(0, 0, width, height);

		// ����ͶӰ����
		float ratio = (float) width / height;// ��Ļ��߱�
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluPerspective(gl, 45.0f, ratio, 1, 5000);
		// ÿ���޸���GL_PROJECTION����ý���ǰ����ģ�����û�GL_MODELVIEW
		gl.glMatrixMode(GL10.GL_MODELVIEW);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		// ȫ��������
		gl.glDisable(GL10.GL_DITHER);

		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
		// ��������������ɫ
		gl.glClearColor(0.0f, 0.0f, 0.5f, 1);
		// ������ɫģ��Ϊƽ����ɫ
		gl.glShadeModel(GL10.GL_SMOOTH);

		// ���ñ������
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glCullFace(GL10.GL_BACK);
		// ������Ȳ���
		gl.glEnable(GL10.GL_DEPTH_TEST);
		// ���ù��պͻ��
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
	 * �����������
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
	 * ��Ⱦģ��
	 * 
	 * @param gl
	 */
	private void drawModel(GL10 gl)
	{
		gl.glPushMatrix();
		{
			// ���ȶ�ģ�ͽ�����ת
			gl.glRotatef(mfAngleX, 1, 0, 0);// ��X����ת
			gl.glRotatef(mfAngleY, 0, 1, 0);// ��Y����ת
			if (gbEnableAnimation && mModel.containsAnimation())
			{
				// ���ģ���ж�������ô��ʱ��͸��¶���
				if (mMsPerFrame > 0)
				{
					mModel.animate(mMsPerFrame * 0.001f);// ��������ת��Ϊ��, /1000
				}
				mModel.fillRenderBuffer();// ���¶��㻺��
			}
			mModel.render(gl);// ��Ⱦģ��
			if (gbShowJoints)
			{
				mModel.renderJoints(gl);// ��Ⱦ�ؽڣ�����
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
	 * ����ģ��--��������
	 * 
	 * @param gl
	 * @param idxModel
	 *            - ģ����Դ����
	 * @param idxTex
	 *            - ��������
	 */
	private void loadModel(GL10 gl, int idxModel, int idxTex)
	{
		loadModel(gl, idxModel, new int[]{idxTex});
	}

	/**
	 * ����ģ��--�������
	 * 
	 * @param gl
	 * @param idxModel
	 *            - ģ����Դ����
	 * @param pIdxTex
	 *            - ��������
	 */
	private void loadModel(GL10 gl, int idxModel, int[] pIdxTex)
	{
		try
		{
			TextureInfo[] pTexInfos = new TextureInfo[pIdxTex.length];
			mModel = new IMS3DModel();

			// ��ģ�Ͷ�������
			InputStream is = mContext.getResources().openRawResource(idxModel);

			if (mModel.loadModel(is))
			{
				// ����ģ�ͳɹ�����ʼ��������
				for (int i = 0; i < pTexInfos.length; i++)
				{
					pTexInfos[i] = new TextureInfo();
					// �õ������ɹ��������������
					pTexInfos[i].mTexID = TextureFactory.getTexture(mContext, gl, pIdxTex[i]);
				}
				// ��������
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
