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
	
	// 要装在纹理的数量
	private final static int NUM_TEXTURES = 5;

	// 旋转操作
	private float xrot;                   
	private float yrot;                   
	private float zrot;                  
	private float roll;       
	
	// 纹理数组
	private int texture[] = new int[NUM_TEXTURES];  
	
	// 是否使用蒙板技术
	private boolean masking=true;  
	// 屏幕切换
	private boolean scene=false;               
	
	public GLRender(Context context)
	{
		// 装载图片
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
		
		// 首先清理屏幕
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		// 设置模型视图矩阵
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		
		//重置矩阵
		gl.glLoadIdentity();
		
		// 视点变换
		GLU.gluLookAt(gl, 0, 0, 3, 0, 0, 0, 0, 1, 0);
		
		draw(gl);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		// TODO Auto-generated method stub
		
		float ratio = (float) width / height;
		
		// 设置视口(OpenGL场景的大小)
		gl.glViewport(0, 0, width, height);

		// 设置投影矩阵为透视投影
		gl.glMatrixMode(GL10.GL_PROJECTION);
		
		// 重置投影矩阵（置为单位矩阵）
		gl.glLoadIdentity();
		
		//创建一个透视投影矩阵（设置视口大小）
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 50);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		// TODO Auto-generated method stub
		
		//告诉系统需要对透视进行修正
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		
		//设置清理屏幕的颜色
		gl.glClearColor(0, 0, 0, 1);
		
		LoadTextures(gl);
	}
	
	/* 装载纹理 */
	public void LoadTextures(GL10 gl)
	{
		// 允许纹理贴图
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		IntBuffer textureBuffer = IntBuffer.allocate(5);
		// 创建纹理
		gl.glGenTextures(5, textureBuffer);
		texture = textureBuffer.array();
		
        // 绑定纹理
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[0]);
        // 装载贴图数据
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmapLogo, 0);
        // 使用线性差值算法
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
		
		// 允许设置顶点数组、纹理坐标数组
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		// 设置定点数组、纹理坐标数组
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texcoords);

	    // 平移操作
		gl.glTranslatef(0.0f, 0.0f, -2.0f);
		
	    // 绑定贴图
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[0]);
		
	    // 保存顶点数组、纹理坐标数组
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
	    // 绘制
	    gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_BYTE, indices);
	    
	    // 开启混合
	    gl.glEnable(GL10.GL_BLEND);
	    // 禁止深度测试
	    gl.glDisable(GL10.GL_DEPTH_TEST);

	    // 使用蒙板混合屏幕颜色
	    if (masking)
	    {
	    	gl.glBlendFunc(GL10.GL_DST_COLOR, GL10.GL_ZERO);
	    }
		
	    // 屏幕选择
	    if (scene)
	    {
	        // 平移
	    	gl.glTranslatef(0.0f, 0.0f, -1.0f);
	        // 旋转
	    	gl.glRotatef(roll*360, 0.0f, 0.0f, 1.0f);

	    	//使用蒙板
	        if (masking)
	        {
	            // 绑定纹理
	            gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[3]);

	            // 保存数据
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

	            // 绘制
	            gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_BYTE, indices);
	        }

	        // 指定混色的方案
	        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE);

	        //绑定贴图
	        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[4]);

	        // 保存数据
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

	        // 绘制
	        gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_BYTE, indices);
	    }
	    else
	    {
	        if (masking)
	        {
	            // 绑定纹理
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
	    
	    // 打开深度测试
	    gl.glEnable(GL10.GL_DEPTH_TEST); 
	    //关闭混色
	    gl.glDisable(GL10.GL_BLEND);     

	    // 禁止设置顶点数组、纹理坐标数组
	    gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	    gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

	    // 旋转纹理，看得更清楚
	    roll+=0.002f; 
	    if (roll>1.0f)
	    {
	        roll-=1.0f;
	    }
	    
	    // 改变旋转的角度
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
