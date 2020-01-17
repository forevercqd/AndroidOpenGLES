package com.android.chapter12;

import java.nio.ByteBuffer;
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
	
	private Bitmap mBitmapMask1,mBitmapMask2;
	private Bitmap mBitmapImage1,mBitmapImage2;
	private Bitmap mBitmapLogo;
	
	// Ҫװ�����������
	private final static int NUM_TEXTURES = 5;

	// ��ת����
	private float xrot;                   
	private float yrot;                   
	private float zrot;                  
	private float roll;       
	
	// ��������
	private int texture[] = new int[NUM_TEXTURES];  
	
	// �Ƿ�ʹ���ɰ弼��
	private boolean masking=true;  
	// ��Ļ�л�
	private boolean scene=false;               
	
	public GLRender(Context context)
	{
		// װ��ͼƬ
		mBitmapMask1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.mask1);
		mBitmapMask2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.mask2);
		mBitmapImage1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.image1);
		mBitmapImage2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.image2);
		mBitmapLogo = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
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
		
		//����һ��͸��ͶӰ���������ӿڴ�С��
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 50);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		// TODO Auto-generated method stub
		
		//����ϵͳ��Ҫ��͸�ӽ�������
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		
		//����������Ļ����ɫ
		gl.glClearColor(0, 0, 0, 1);
		
		LoadTextures(gl);
	}
	
	/* װ������ */
	public void LoadTextures(GL10 gl)
	{
		// ����������ͼ
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		IntBuffer textureBuffer = IntBuffer.allocate(5);
		// ��������
		gl.glGenTextures(5, textureBuffer);
		texture = textureBuffer.array();
		
        // ������
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[0]);
        // װ����ͼ����
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmapLogo, 0);
        // ʹ�����Բ�ֵ�㷨
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        

		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[1]);

		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmapMask1, 0);
        /* Linear Filtering */
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        
        /* Typical Texture Generation Using Data From The Bitmap */
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[2]);
        /* Generate The Texture */
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmapImage1, 0);
        /* Linear Filtering */
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        
        /* Typical Texture Generation Using Data From The Bitmap */
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[3]);
        /* Generate The Texture */
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmapMask2, 0);
        /* Linear Filtering */
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        
        /* Typical Texture Generation Using Data From The Bitmap */
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[4]);
        /* Generate The Texture */
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmapImage2, 0);
        /* Linear Filtering */
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	}
	
	
	private void draw(GL10 gl)
	{
		ByteBuffer indices = ByteBuffer.wrap(new byte[]{1, 0, 2, 3});
		FloatBuffer vertices = FloatBuffer.wrap(new float[12]);
		FloatBuffer texcoords = FloatBuffer.wrap(new float[8]);
		
		gl.glScalef(10, 10, 10);
		
		// �������ö������顢������������
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		// ���ö������顢������������
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texcoords);

	    // ƽ�Ʋ���
		gl.glTranslatef(0.0f, 0.0f, -2.0f);
		
	    // ����ͼ
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[0]);
		
	    // ���涥�����顢������������
		texcoords.clear();
		texcoords.put(0.0f); texcoords.put(-roll+3.0f);
		texcoords.put(3.0f); texcoords.put(-roll+3.0f);
		texcoords.put(3.0f); texcoords.put(-roll+0.0f);
		texcoords.put(0.0f); texcoords.put(-roll+0.0f);
	    vertices.clear();
	    vertices.put(-1.1f); vertices.put(-1.1f); vertices.put(0.0f);
	    vertices.put(1.1f);  vertices.put(-1.1f); vertices.put(0.0f);
	    vertices.put(1.1f);  vertices.put(1.1f);  vertices.put(0.0f);
	    vertices.put(-1.1f); vertices.put(1.1f);  vertices.put(0.0f);
	    // ����
	    gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_BYTE, indices);
	    
	    // �������
	    gl.glEnable(GL10.GL_BLEND);
	    // ��ֹ��Ȳ���
	    gl.glDisable(GL10.GL_DEPTH_TEST);

	    // ʹ���ɰ�����Ļ��ɫ
	    if (masking)
	    {
	    	gl.glBlendFunc(GL10.GL_DST_COLOR, GL10.GL_ZERO);
	    }
		
	    // ��Ļѡ��
	    if (scene)
	    {
	        // ƽ��
	    	gl.glTranslatef(0.0f, 0.0f, -1.0f);
	        // ��ת
	    	gl.glRotatef(roll*360, 0.0f, 0.0f, 1.0f);

	    	//ʹ���ɰ�
	        if (masking)
	        {
	            // ������
	            gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[3]);

	            // ��������
	            texcoords.clear();
	            texcoords.put(0.0f); texcoords.put(1.0f);
	            texcoords.put(1.0f); texcoords.put(1.0f);
	            texcoords.put(1.0f); texcoords.put(0.0f);
	            texcoords.put(0.0f); texcoords.put(0.0f);
	    
	            vertices.clear();
	            vertices.put(-1.1f); vertices.put(-1.1f); vertices.put(0.0f);
	            vertices.put(1.1f);  vertices.put(-1.1f); vertices.put(0.0f);
	            vertices.put(1.1f);  vertices.put(1.1f);  vertices.put(0.0f);
	            vertices.put(-1.1f); vertices.put(1.1f);  vertices.put(0.0f);

	            // ����
	            gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_BYTE, indices);
	        }

	        // ָ����ɫ�ķ���
	        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE);

	        //����ͼ
	        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[4]);

	        // ��������
	        texcoords.clear();
	        texcoords.put(0.0f); texcoords.put(1.0f);
	        texcoords.put(1.0f); texcoords.put(1.0f);
	        texcoords.put(1.0f); texcoords.put(0.0f);
	        texcoords.put(0.0f); texcoords.put(0.0f);
	        
	        vertices.clear();
	        vertices.put(-1.1f); vertices.put(-1.1f); vertices.put(0.0f);
	        vertices.put(1.1f);  vertices.put(-1.1f); vertices.put(0.0f);
	        vertices.put(1.1f);  vertices.put(1.1f);  vertices.put(0.0f);
	        vertices.put(-1.1f); vertices.put(1.1f);  vertices.put(0.0f);

	        // ����
	        gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_BYTE, indices);
	    }
	    else
	    {
	        if (masking)
	        {
	            // ������
	        	gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[1]);

	           
	        	texcoords.clear();
	        	texcoords.put(roll+0.0f); texcoords.put(4.0f);
	        	texcoords.put(roll+4.0f); texcoords.put(4.0f);
	        	texcoords.put(roll+4.0f); texcoords.put(0.0f);
	        	texcoords.put(roll+0.0f); texcoords.put(0.0f);
	        	vertices.clear();
	        	vertices.put(-1.1f); vertices.put(-1.1f); vertices.put(0.0f);
	        	vertices.put(1.1f);  vertices.put(-1.1f); vertices.put(0.0f);
	        	vertices.put(1.1f);  vertices.put(1.1f);  vertices.put(0.0f);
	        	vertices.put(-1.1f); vertices.put(1.1f);  vertices.put(0.0f);

	            
	            gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_BYTE, indices);
	        }

	        
	        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE);
	        
	        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[2]);

	        texcoords.clear();
	        texcoords.put(roll+0.0f); texcoords.put(4.0f);
	        texcoords.put(roll+4.0f); texcoords.put(4.0f);
	        texcoords.put(roll+4.0f); texcoords.put(0.0f);
	        texcoords.put(roll+0.0f); texcoords.put(0.0f);
	        vertices.clear();
	        vertices.put(-1.1f); vertices.put(-1.1f); vertices.put(0.0f);
	        vertices.put(1.1f);  vertices.put(-1.1f); vertices.put(0.0f);
	        vertices.put(1.1f);  vertices.put(1.1f);  vertices.put(0.0f);
	        vertices.put(-1.1f); vertices.put(1.1f);  vertices.put(0.0f);

	        gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_BYTE, indices);
	    }
	    
	    // ����Ȳ���
	    gl.glEnable(GL10.GL_DEPTH_TEST); 
	    //�رջ�ɫ
	    gl.glDisable(GL10.GL_BLEND);     

	    // ��ֹ���ö������顢������������
	    gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	    gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

	    // ��ת�������ø����
	    roll+=0.002f; 
	    if (roll>1.0f)
	    {
	        roll-=1.0f;
	    }
	    
	    // �ı���ת�ĽǶ�
	    xrot+=0.3f; 
	    yrot+=0.2f; 
	    zrot+=0.4f; 
	}
	
	
	public void UseMasking()
	{
		masking = !masking;
	}
	
	public void ChangeScreen()
	{
		scene = !scene;
	}

}
