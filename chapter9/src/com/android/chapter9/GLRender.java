package com.android.chapter9;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.GLSurfaceView.Renderer;

public class GLRender implements Renderer
{

	private Bitmap		mBitmapTexture	= null;

	private int					mTexture[];
	
	// X��ѭ������
	private int	xloop;	
	// Y��ѭ������
	private int	yloop;						

	private float xrot, yrot, zrot;
	// �����ɫ��Ϊ��ɫ
	private float fogColor[]= {0.5f, 0.5f, 0.5f, 1.0f};		
	
	// ������ӵ���ʾ�б�
	FloatBuffer boxVertices = FloatBuffer.allocate(60);
	FloatBuffer boxTexCoords= FloatBuffer.allocate(40);
	
	// ������Ӷ�������ʾ�б�
	FloatBuffer topVertices = FloatBuffer.allocate(12);
	FloatBuffer topTexCoords= FloatBuffer.allocate(8);
	
	float[][] boxcol = {
			{1.0f, 0.0f, 0.0f},
			{1.0f, 0.5f, 0.0f},
			{1.0f, 1.0f, 0.0f},
			{0.0f, 1.0f, 0.0f},
			{0.0f, 1.0f, 1.0f},
	};
	
	float[][] topcol= {
			{0.5f, 0.0f, 0.0f},
			{0.5f, 0.25f, 0.0f},
			{0.5f, 0.5f, 0.0f},
			{0.0f, 0.5f, 0.0f},
			{0.0f, 0.5f, 0.5f},
	};
	
	public GLRender(Context context)
	{
		mBitmapTexture = BitmapFactory.decodeResource(context.getResources(), R.drawable.img);
		mTexture = new int[1];
	}
	
	@Override
	public void onDrawFrame(GL10 gl)
	{
		// TODO Auto-generated method stub
		
		// ����������Ļ
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		// ����ģ����ͼ����
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		
		//���þ���
		gl.glLoadIdentity();
		
		// �ӵ�任
		GLU.gluLookAt(gl, 0, 0, 3, 0, 0, 0, 0, 1, 0);

		// �����б�
		drawList(gl);
		
	    xrot+=0.5f;
	    yrot+=0.6f; 
	    zrot+=0.3f; 
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		// TODO Auto-generated method stub
		
		float ratio = (float) width / height;
		
		// �����ӿ�(OpenGL�����Ĵ�С)
		gl.glViewport(0, 0, width, height);

		// ����ͶӰ����Ϊ͸��ͶӰ
		gl.glMatrixMode(GL10.GL_PROJECTION);
		
		// ����ͶӰ������Ϊ��λ����
		gl.glLoadIdentity();
		
		//����һ��͸��ͶӰ���������ӿڴ�С��
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 1000f);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		// TODO Auto-generated method stub
		
		//����ϵͳ��Ҫ��͸�ӽ�������
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		
		//����������Ļ����ɫ
		gl.glClearColor(0.5f,0.5f,0.5f,1.0f);
		
		//������Ȼ���
		gl.glEnable(GL10.GL_DEPTH_TEST);
		
		//�޳����棨�������ǲ����ı��棩
		gl.glEnable(GL10.GL_CULL_FACE);
		
		// ������Ӱƽ��
		gl.glShadeModel(GL10.GL_SMOOTH);
		
		// ������Ȼ���
		gl.glClearDepthf(30.0f);
		
		//��Ȳ��Ե�����(���С����ȵ�ʱ��Ҳ��Ⱦ)
		gl.glDepthFunc(GL10.GL_LEQUAL);
		//���С��ʱ�����Ⱦ
		//gl.glDepthFunc(GL10.GL_LESS);
		
		loadTexture(gl);
		
		// ���ù�Ч
		setupLight(gl);					
						
		// ʹ����ɫ����
		gl.glEnable(GL10.GL_COLOR_MATERIAL);
		
		setupFog(gl);
	}
	
	/* �����б� */
	public void BuildLists(GL10 gl)
	{
		boxTexCoords.put(new float[]{1.0f, 1.0f,0.0f, 1.0f,0.0f, 0.0f,1.0f,0.0f});
		boxVertices.put(new float[]{-1.0f, -1.0f, -1.0f,1.0f, -1.0f, -1.0f,1.0f, -1.0f,  1.0f,-1.0f, -1.0f,  1.0f});
		
		boxTexCoords.put(new float[]{0.0f, 0.0f,1.0f, 0.0f,1.0f, 1.0f,0.0f, 1.0f});
		boxVertices.put(new float[]{-1.0f, -1.0f,  1.0f,1.0f, -1.0f,  1.0f,1.0f,  1.0f,  1.0f,-1.0f,  1.0f,  1.0f});
		
		boxTexCoords.put(new float[]{1.0f, 0.0f,1.0f, 1.0f,0.0f, 1.0f,0.0f, 0.0f});
		boxVertices.put(new float[]{-1.0f, -1.0f, -1.0f,-1.0f,  1.0f, -1.0f,1.0f,  1.0f, -1.0f,1.0f, -1.0f, -1.0f});
		
		boxTexCoords.put(new float[]{1.0f, 0.0f,1.0f, 1.0f,0.0f, 1.0f,0.0f, 0.0f});
		boxVertices.put(new float[]{1.0f, -1.0f, -1.0f,1.0f,  1.0f, -1.0f,1.0f,  1.0f,  1.0f,1.0f, -1.0f,  1.0f});
		
		boxTexCoords.put(new float[]{0.0f, 0.0f,1.0f, 0.0f,1.0f, 1.0f,0.0f, 1.0f});
		boxVertices.put(new float[]{-1.0f, -1.0f, -1.0f,-1.0f, -1.0f,  1.0f,-1.0f,  1.0f,  1.0f,-1.0f,  1.0f, -1.0f});
		
		topTexCoords.put(new float[]{0.0f, 1.0f,0.0f, 0.0f,1.0f, 0.0f,1.0f, 1.0f});
		topVertices.put(new float[]{-1.0f,  1.0f, -1.0f,-1.0f,  1.0f,  1.0f,1.0f,  1.0f,  1.0f,1.0f,  1.0f, -1.0f});
	}
	
	/* װ����ͼ */
	private void loadTexture(GL10 gl)
	{
		// ����2D������ͼ
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		IntBuffer intBuffer = IntBuffer.allocate(1);
		// ��������
		gl.glGenTextures(1, intBuffer);
		mTexture[0] = intBuffer.get();
		// ������
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture[0]);
		
		// ��������
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmapTexture, 0);
		// ����ͼ��
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR_MIPMAP_NEAREST);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		// ������ʾ�б�
		BuildLists(gl);		
	}
	
	
	private void drawList(GL10 gl)
	{
		// ������
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture[0]);
		
		// �������ö�������
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		
		// ��������������ͼ��������
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		for (yloop=1;yloop<6;yloop++)
		{
			for (xloop=0;xloop<yloop;xloop++)
			{
				// ����ģ����ͼ����
				gl.glLoadIdentity();	
				
				// ���ú��ӵ�λ�ã�ƽ�ƣ�
				gl.glTranslatef(1.4f+((float)(xloop)*2.8f)-((float)(yloop)*1.4f),((6.0f-(float)(yloop))*2.4f)-7.0f,-20.0f);
				
				// ��ת(x��)
				gl.glRotatef(45.0f-(2.0f*yloop)+xrot,1.0f,0.0f,0.0f);
				// ��ת(y��)
				gl.glRotatef(45.0f+yrot,0.0f,1.0f,0.0f);
				// ������ɫ
				gl.glColor4f(boxcol[yloop-1][0], boxcol[yloop-1][1], boxcol[yloop-1][2], 1.0f);
				
				// ���ö�������
				gl.glVertexPointer(3, GL10.GL_FLOAT, 0, boxVertices);
				// ����������������
				gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, boxTexCoords);
				
				// ����
				gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);
				gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 4, 4);
				gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 8, 4);
				gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 12, 4);
				gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 16, 4);

	            // ���õ�һ��ɫ
				gl.glColor4f(topcol[yloop-1][0], topcol[yloop-1][1], topcol[yloop-1][2], 1.0f);
				// ���ö�������
				gl.glVertexPointer(3, GL10.GL_FLOAT, 0, topVertices);
				// ����������������
				gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, topTexCoords);
				// ����
				gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);
			}
		}
		
		// ��ֹ����������������
	    gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	    // ��ֹ���ö�������
	    gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
	
	private void setupLight(GL10 gl)
	{
		//������Ч
		gl.glEnable(GL10.GL_LIGHTING);

		//����0�Ź�Դ
		gl.glEnable(GL10.GL_LIGHT0);
		
		//���������ɫ
		FloatBuffer light0Ambient = FloatBuffer.wrap(new float[]{0.5f,0.5f,0.5f,1.0f}); 
		//���û�����
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, light0Ambient);
		
		//ɢ������ɫ
		FloatBuffer light0Diffuse = FloatBuffer.wrap(new float[]{1.0f,1.0f,1.0f,1.0f});
		//����ɢ���
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, light0Diffuse);
		
		//�߹����ɫ
		FloatBuffer light0Position = FloatBuffer.wrap(new float[]{0.0f,0.0f,2.0f,1.0f}); 
		//���ø߹�
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, light0Position);
	}

	private void setupFog(GL10 gl)
	{
		// ����������ģʽ
		//gl.glFogx(GL10.GL_FOG_MODE, GL10.GL_EXP);	
		
		//gl.glFogx(GL10.GL_FOG_MODE, GL10.GL_EXP2);
		gl.glFogx(GL10.GL_FOG_MODE, GL10.GL_LINEAR);
		
		// ������������ɫ
		gl.glFogfv(GL10.GL_FOG_COLOR, fogColor,0);	
		
		// �����������ܶ�
		gl.glFogf(GL10.GL_FOG_DENSITY, 0.35f);	
		
		// ��������(������������Ⱦ��ʽ)
		gl.glHint(GL10.GL_FOG_HINT, GL10.GL_DONT_CARE);	
		
		// �����Ŀ�ʼλ��
		gl.glFogf(GL10.GL_FOG_START, 1.0f);	
		
		// �����Ľ���λ��
		gl.glFogf(GL10.GL_FOG_END, 35.0f);	
		
		// ��������
		gl.glEnable(GL10.GL_FOG);					
	}
}