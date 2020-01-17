package com.android.chapter10;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;

public class GLRender implements Renderer
{
	private Context mcontext;
	
	private float rot = 0.0f;
	
	//��������
	private FloatBuffer vertices = FloatBuffer.wrap(new float[]{
			 0.f, 			-0.525731f,  0.850651f,
			 0.850651f, 		   0.f,  0.525731f,
			 0.850651f, 		   0.f, -0.525731f,
			-0.850651f, 		   0.f, -0.525731f,
			-0.850651f, 		   0.f,  0.525731f,
			-0.525731f, 	 0.850651f,  0.f,
			 0.525731f, 	 0.850651f,  0.f,
			 0.525731f, 	-0.850651f,  0.f,
			-0.525731f, 	-0.850651f,  0.f,
			 0.f, 			-0.525731f, -0.850651f,
			 0.f, 			 0.525731f, -0.850651f,
			 0.f, 			 0.525731f,  0.850651f}); 
	
	//��ɫ����
	private FloatBuffer colors = FloatBuffer.wrap(new float[]{
			1.0f, 0.0f, 0.0f, 1.0f,
			1.0f, 0.5f, 0.0f, 1.0f,
			1.0f, 1.0f, 0.0f, 1.0f,
			0.5f, 1.0f, 0.0f, 1.0f,
			0.0f, 1.0f, 0.0f, 1.0f,
			0.0f, 1.0f, 0.5f, 1.0f,
			0.0f, 1.0f, 1.0f, 1.0f,
			0.0f, 0.5f, 1.0f, 1.0f,
			0.0f, 0.0f, 1.0f, 1.0f,
			0.5f, 0.0f, 1.0f, 1.0f,
			1.0f, 0.0f, 1.0f, 1.0f,
			1.0f, 0.0f, 0.5f, 1.0f});
	
	//��������
	private ByteBuffer icosahedronFaces = ByteBuffer.wrap(new byte[]{
			 1,  2,  6,
	         1,  7,  2,
	         3,  4,  5,
	         4,  3,  8,
	         6,  5, 11,
	         5,  6, 10,
	         9, 10,  2,
	        10,  9,  3,
	         7,  8,  9,
	         8,  7,  0,
	        11,  0,  1,
	         0, 11,  4,
	         6,  2, 10,
	         1,  6, 11,
	         3,  5, 10,
	         5,  4, 11,
	         2,  7,  9,
	         7,  1,  0,
	         3,  9,  8,
	         4,  8,  0});
	
	public GLRender(Context context)
	{
		mcontext = context;
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
		
		draw3D(gl);
		
		drawText(gl);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		// TODO Auto-generated method stub
		
		mWidth = width;
		
		mHeight = height;
		
		float ratio = (float) width / height;
		
		// �����ӿ�(OpenGL�����Ĵ�С)
		gl.glViewport(0, 0, width, height);

		// ����ͶӰ����Ϊ͸��ͶӰ
		gl.glMatrixMode(GL10.GL_PROJECTION);
		
		// ����ͶӰ������Ϊ��λ����
		gl.glLoadIdentity();
		
		//����һ��͸��ͶӰ���������ӿڴ�С��
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 1000);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		// TODO Auto-generated method stub
		
		//����ϵͳ��Ҫ��͸�ӽ�������
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		
		//����������Ļ����ɫ
		gl.glClearColor(0, 0, 0, 1);
		
		//������Ȼ���
		gl.glEnable(GL10.GL_DEPTH_TEST);
		
		initText(gl);
	}
	
	private void draw3D(GL10 gl)
	{
		gl.glFrontFace(GL10.GL_CCW);
		
		//ƽ�Ʋ���
		gl.glTranslatef(0.0f,-1.0f,-3.0f);
		
		//��ת����
		gl.glRotatef(rot,1.0f,1.0f,1.0f);
		
		//���Ų���
		gl.glScalef(3.0f, 3.0f, 3.0f);
		
		//�������ö�������
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		
		//����������ɫ����
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);;
		
		gl.glShadeModel(GL10.GL_SMOOTH);
		
		//���ö�������
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices);
		
		//������ɫ����
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, colors);
		
		//����
		gl.glDrawElements(GL10.GL_TRIANGLES, 60, GL10.GL_UNSIGNED_BYTE, icosahedronFaces);

		//ȡ�������������ɫ���������
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

		//������ת�Ƕ�
		rot+=0.5;
	}
	
	////////////////////////////////
	private LabelMaker mLabels;
	private int mString1,mString2,mString3;
	private Paint mLabelPaint;
	private int mWidth,mHeight;
	private Typeface mFace; 
	
	private void initText(GL10 gl)
	{
		mLabelPaint = new Paint();
		mLabelPaint.setAntiAlias(true);
		// �������
		mLabelPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

		mFace = Typeface.createFromAsset(mcontext.getAssets(),"fonts/samplefont.ttf");  
		
		mLabelPaint.setTypeface(mFace);   
		
		if (mLabels != null)
		{
			mLabels.shutdown(gl);
		}
		else
		{
			mLabels = new LabelMaker(true, 320, 480);
		}
		mLabels.initialize(gl);
		mLabels.beginAdding(gl);
		mLabelPaint.setARGB(0xff, 0xff, 0x00, 0x00);
		mLabelPaint.setTextSize(20);
		mString1 = mLabels.add(gl, "��AndroidӦ�ÿ������ء�", mLabelPaint);
		mLabelPaint.setARGB(0xff, 0xff, 0xff, 0x00);
		mString2 = mLabels.add(gl, "Android OpenGL ES����", mLabelPaint);
		mLabelPaint.setARGB(0xff, 0xff, 0xff, 0xff);
		mLabelPaint.setTextSize(15);
		mString3 = mLabels.add(gl, "��ʮ����2D����", mLabelPaint);
		mLabels.endAdding(gl);
	}
	
	private void drawText(GL10 gl)
	{		
		// ��ʼ����
        mLabels.beginDrawing(gl, mWidth, mHeight);
        
        float x = (mWidth - mLabels.getWidth(mString1))/2;
        mLabels.draw(gl, x, 350, mString1);
        
        x = (mWidth - mLabels.getWidth(mString2))/2;
        mLabels.draw(gl, x, 350-mLabels.getHeight(mString1), mString2);
        
        x = (mWidth - mLabels.getWidth(mString3))/2;
        mLabels.draw(gl, x, 350-mLabels.getHeight(mString1)-mLabels.getHeight(mString2), mString3);
        // ��������
        mLabels.endDrawing(gl);
	}
}
