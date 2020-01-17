package com.android.chapter11;

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

	private Bitmap mBitmapTexture = null;
	
	int mTexture[];
	
	// Points���񶥵�����(45,45,3)
	float vertex[][][]=new float[45][45][3];	
	// ָ�����β��˵��˶��ٶ�
	int wiggle_count = 0;
	// ��ʱ����
	float hold;	
	// ��ת����
	float xrot, yrot, zrot;
	
	FloatBuffer texCoord = FloatBuffer.allocate(8);
	FloatBuffer points = FloatBuffer.allocate(12);
	
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
		
		// ����
		draw(gl);
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
		
		// ����һ��͸��ͶӰ���������ӿڴ�С��
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 30);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		// TODO Auto-generated method stub
		
		// ����ϵͳ��Ҫ��͸�ӽ�������
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		
		// ����������Ļ����ɫ
		gl.glClearColor(0, 0, 0, 1);
		
		// ������Ȼ���
		gl.glEnable(GL10.GL_DEPTH_TEST);
		
		// ������Ȳ���
		gl.glClearDepthf(10.0f);
		
		// ��Ȳ��Ե����ͣ�С�ڻ��ߵ���ʱ���Ƕ���Ⱦ��
		gl.glDepthFunc(GL10.GL_LEQUAL);
		
		// װ������
		loadTexture(gl);
		
		// ��ʼ������
		initData();
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
		
		mBitmapTexture.recycle();
		mBitmapTexture = null;
	}
	
	private void initData()
	{
		// ��Xƽ��ѭ��
		for(int x=0; x<45; x++)
		{
			// ��Yƽ��ѭ��
			for(int y=0; y<45; y++)
			{
				// �������Ӳ���Ч��
				vertex[x][y][0]=((float)x/5.0f)-4.5f;
				vertex[x][y][1]=(((float)y/5.0f)-4.5f);
				vertex[x][y][2]=(float)(Math.sin(((((float)x/5.0f)*40.0f)/360.0f)*3.141592654*2.0f));
			}
		}
	}
	
	private void draw(GL10 gl)
	{
		// ѭ������
		int x, y;				
		// ���������εĲ��˷ָ�ɺ�С���ı���
		float float_x, float_y, float_xb, float_yb;		
		
		// ƽ�Ʋ���
		gl.glTranslatef(0.0f,0.0f,-12.0f);				

		// ��ת����
		gl.glRotatef(xrot,1.0f,0.0f,0.0f);				
		gl.glRotatef(yrot,0.0f,1.0f,0.0f);				
		gl.glRotatef(zrot,0.0f,0.0f,1.0f);				

		// �������ö��������������������
	    gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
	    gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	    
	    // ���ö������顢������������
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, points);
	    gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texCoord);
	    
	    // ������
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture[0]);		
		
		for (x = 0; x < 44; x++)
		{
			for (y = 0; y < 44; y++)
			{
				float_x = (float) (x) / 44.0f; // ����X����ֵ
				float_y = (float) (y) / 44.0f; // ����Y����ֵ
				float_xb = (float) (x + 1) / 44.0f; // X����ֵ+0.0227f
				float_yb = (float) (y + 1) / 44.0f; // Y����ֵ+0.0227f

				// ����������������
				texCoord.clear();
				texCoord.put(float_x);
				texCoord.put(float_y);
				texCoord.put(float_x);
				texCoord.put(float_yb);
				texCoord.put(float_xb);
				texCoord.put(float_yb);
				texCoord.put(float_xb);
				texCoord.put(float_y);

				// ���涥������
				points.clear();
				points.put(vertex[x][y][0]);
				points.put(vertex[x][y][1]);
				points.put(vertex[x][y][2]);

				points.put(vertex[x][y + 1][0]);
				points.put(vertex[x][y + 1][1]);
				points.put(vertex[x][y + 1][2]);

				points.put(vertex[x + 1][y + 1][0]);
				points.put(vertex[x + 1][y + 1][1]);
				points.put(vertex[x + 1][y + 1][2]);

				points.put(vertex[x + 1][y][0]);
				points.put(vertex[x + 1][y][1]);
				points.put(vertex[x + 1][y][2]);

				// ����
				gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);
			}
		}
		
		// ��ֹ���ö������顢������������
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		
		// �������Ͳ����ٶ�(ÿ��2֡һ��)
		if( wiggle_count == 2 )					
		{
			// ��Yƽ��ѭ��
			for( y = 0; y < 45; y++ )			
			{
				// �洢��ǰ��ನ��ֵ
				hold=vertex[0][y][2];
				// ��Xƽ��ѭ��
				for( x = 0; x < 44; x++)		
				{
					// ��ǰ����ֵ�������Ҳ�Ĳ���ֵ
					vertex[x][y][2] = vertex[x+1][y][2];
				}
				// �ղŵ�ֵ��Ϊ�����Ĳ���ֵ
				vertex[44][y][2]=hold;			
			}
			// ����������
			wiggle_count = 0;				
		}
		wiggle_count++;						
		
		//�ı���ת�ĽǶ�
		xrot+=0.3f;					
		yrot+=0.2f;				
		zrot+=0.4f;						
	}

}