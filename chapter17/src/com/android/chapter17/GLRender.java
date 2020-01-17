package com.android.chapter17;

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
	private float rot = 0.0f;
	
	//顶点数组
	private FloatBuffer vertices = FloatBuffer.wrap(new float[] {
			// FRONT
			 0.5f, -0.5f,  0.5f,
			 0.5f,  0.5f,  0.5f,
			-0.5f, -0.5f,  0.5f,
			-0.5f,  0.5f,  0.5f,
			// BACK
			-0.5f, -0.5f, -0.5f,
			-0.5f,  0.5f, -0.5f,
			 0.5f, -0.5f, -0.5f,
			 0.5f,  0.5f, -0.5f,
			// LEFT
			-0.5f, -0.5f,  0.5f,
			-0.5f,  0.5f,  0.5f,
			-0.5f, -0.5f, -0.5f,
			-0.5f,  0.5f, -0.5f,
			// RIGHT
			 0.5f, -0.5f, -0.5f,
			 0.5f,  0.5f, -0.5f,
			 0.5f, -0.5f,  0.5f,
			 0.5f,  0.5f,  0.5f,
			// TOP
			-0.5f,  0.5f,  0.5f,
			 0.5f,  0.5f,  0.5f,
			 -0.5f,  0.5f, -0.5f,
			 0.5f,  0.5f, -0.5f,
			// BOTTOM
			-0.5f, -0.5f,  0.5f,
			-0.5f, -0.5f, -0.5f,
			 0.5f, -0.5f,  0.5f,
			 0.5f, -0.5f, -0.5f,
		}); 
	
	//纹理坐标数组
	private FloatBuffer texCoords = FloatBuffer.wrap(new float[] {
			// FRONT
			 0.0f, 1.0f,
			 0.0f, 0.0f,
			 1.0f, 1.0f,
			 1.0f, 0.0f,
			// BACK
			 1.0f, 0.0f,
			 1.0f, 1.0f,
			 0.0f, 0.0f,
			 0.0f, 1.0f,
			// LEFT
			 1.0f, 0.0f,
			 1.0f, 1.0f,
			 0.0f, 0.0f,
			 0.0f, 1.0f,
			// RIGHT
			 1.0f, 0.0f,
			 1.0f, 1.0f,
			 0.0f, 0.0f,
			 0.0f, 1.0f,
			// TOP
			 0.0f, 0.0f,
			 1.0f, 0.0f,
			 0.0f, 1.0f,
			 1.0f, 1.0f,
			// BOTTOM
			 1.0f, 0.0f,
			 1.0f, 1.0f,
			 0.0f, 0.0f,
			 0.0f, 1.0f
		});
	
	private Bitmap mBitmapTexture1,mBitmapTexture2;
	
	private int mTexture[];
	
	float lightAmbient[] = new float[] { 0.2f, 0.2f, 0.2f, 1.0f };
	float lightDiffuse[] = new float[] { 1f, 1f, 1f, 1.0f };
	float[] lightPos = new float[] {0,0,3,1};
	
	float matAmbient[] = new float[] { 1f, 1f, 1f, 1.0f };
	float matDiffuse[] = new float[] { 1f, 1f, 1f, 1.0f };
	
	public GLRender(Context context)
	{
		mTexture = new int[2];
		mBitmapTexture1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.texture);
		mBitmapTexture2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.light);
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
		
		drawCube(gl);
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
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 1000);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		// TODO Auto-generated method stub
		
		//告诉系统需要对透视进行修正
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		
		//设置清理屏幕的颜色
		gl.glClearColor(0, 0, 0, 1);
		
		//启用深度缓存
		gl.glEnable(GL10.GL_DEPTH_TEST);
		
		// 光效材质设置
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_LIGHT0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, matAmbient, 0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, matDiffuse, 0);
		
		// 光效
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient,	0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse,	0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos, 0);
		
		// 开启深度缓存
		gl.glEnable(GL10.GL_DEPTH_TEST);
		// 设置深度缓存的类型
		gl.glDepthFunc(GL10.GL_LEQUAL);
		
		// 允许2D纹理贴图
		gl.glEnable(GL10.GL_TEXTURE_2D);
		// 设置深度测试
		gl.glClearDepthf(1.0f);
		
		// 剔除背面
		gl.glEnable(GL10.GL_CULL_FACE);
		// 开启平滑阴影
		gl.glShadeModel(GL10.GL_SMOOTH);
		
		// 装载纹理
		loadTexture(gl);
	}
	
	private void loadTexture(GL10 gl)
	{
		IntBuffer intBuffer = IntBuffer.allocate(2);
		// 创建纹理
		gl.glGenTextures(2, intBuffer);
		mTexture[0] = intBuffer.get();
		
		//绑定纹理
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture[0]);
		//当纹理需要被放大和缩小时都使用线性插值方法调整图像
		gl.glTexParameterx(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_LINEAR); 
		gl.glTexParameterx(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_LINEAR); 
		//生成纹理（加载图像）
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmapTexture1, 0);
		
		//绑定纹理
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture[1]);
		//当纹理需要被放大和缩小时都使用线性插值方法调整图像
		gl.glTexParameterx(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_LINEAR); 
		gl.glTexParameterx(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_LINEAR); 
		//生成纹理（加载图像）
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmapTexture2, 0);	
		
		mBitmapTexture1.recycle();
		mBitmapTexture1 = null;
		mBitmapTexture2.recycle();
		mBitmapTexture2 = null;
	}
	
	
	private void drawCube(GL10 gl)
	{
		gl.glFrontFace(GL10.GL_CCW);
		
		//平移操作
		gl.glTranslatef(0.0f,0.0f,-3.0f);
		
		//旋转操作
		gl.glRotatef(rot,1.0f,0.0f,0.0f);
		gl.glRotatef(rot,0.0f,1.0f,0.0f);
		gl.glRotatef(rot,0.0f,0.0f,1.0f);
		
		//缩放操作
		gl.glScalef(4.0f, 4.0f, 4.0f);
		
		//允许设置顶点数组
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		
		//允许设置纹理坐标数组
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		gl.glShadeModel(GL10.GL_SMOOTH);
		
		//设置顶点数组
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices);
		
		// 设置第一层纹理
		gl.glActiveTexture(GL10.GL_TEXTURE0); 
		gl.glClientActiveTexture(GL10.GL_TEXTURE0); 
		gl.glEnable(GL10.GL_TEXTURE_2D); 
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture[0]); 
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texCoords);
		
		// 设置第二层纹理
		gl.glActiveTexture(GL10.GL_TEXTURE1); 
		gl.glClientActiveTexture(GL10.GL_TEXTURE1); 
		gl.glEnable(GL10.GL_TEXTURE_2D); 
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture[1]); 
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texCoords);

		// 设置纹理映射方式
		gl.glTexEnvx(GL10.GL_TEXTURE_ENV , GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
		/**
		 * target: 必须为GL_TEXTURE_FILTER_CONTROL或GL_TEXTURE_ENV
		 * pname: 如target为GL_TEXTURE_FILTER_CONTROL, pname必须为GL_TEXTURE_LOD_BIAS.
         *	      如target为GL_TEXTURE_ENV, pname可为GL_TEXTURE_ENV_MODE, GL_TEXTURE_ENV_COLOR
		 * param: 如pname为GL_TEXTURE_LOD_BIAS, param是一个浮点数, 用于指定GL_TEXTURE_LOD_BIAS的值.
       	 *   	  如pname为GL_TEXTURE_ENV_MODE,param的取值为GL_DECAL,GL_REPLACE,GL_MODULATE,GL_BLEND, GL_ADD或GL_COMBINE. 指定了如何将纹理值和片元的颜色值合并起来.
       	 *	      如pname为GL_TEXTURE_ENV_COLOR, 则参数pname是一个包含4个浮点数的数组, 这4个元素分别是R, G, B, A分量, 指定了一种用于GL_BLEND操作的颜色.
		 */
		
		//绘制
		{
			gl.glColor4f(1.0f, 1, 1, 1.0f);
			gl.glNormal3f(0,0,1);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
			gl.glNormal3f(0,0,-1);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
		
			gl.glColor4f(1, 1.0f, 1, 1.0f);
			gl.glNormal3f(-1,0,0);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 8, 4);
			gl.glNormal3f(1,0,0);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 12, 4);
			
			gl.glColor4f(1, 1, 1.0f, 1.0f);
			gl.glNormal3f(0,1,0);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 16, 4);
			gl.glNormal3f(0,-1,0);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 20, 4);
		}
		// 激活第一层纹理
		gl.glActiveTexture(GL10.GL_TEXTURE0); 
		gl.glClientActiveTexture(GL10.GL_TEXTURE0);
		
		//用完两个通道．全都关闭．
		gl.glClientActiveTexture(GL10.GL_TEXTURE0);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glClientActiveTexture(GL10.GL_TEXTURE1);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		//关闭第二个纹理通道
		gl.glActiveTexture(GL10.GL_TEXTURE1);
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glActiveTexture(GL10.GL_TEXTURE0);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		//取消顶点数组的设置
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		
		//更改旋转角度
		rot+=2.0f;
	}

}

