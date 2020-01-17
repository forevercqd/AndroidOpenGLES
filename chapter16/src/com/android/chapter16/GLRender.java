package com.android.chapter16;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.GLSurfaceView.Renderer;

public class GLRender implements Renderer
{
	private Context						mContext;

	// 光效
	private final static float			lightAmb[]	= { 0.5f, 0.5f, 0.5f, 1.0f };
	private final static float			lightDif[]	= { 1.0f, 1.0f, 1.0f, 1.0f };
	private final static float			lightPos[]	= { 0.0f, 0.0f, 2.0f, 1.0f };

	private final static FloatBuffer	lightAmbBfr;
	private final static FloatBuffer	lightDifBfr;
	private final static FloatBuffer	lightPosBfr;

	private IntBuffer					texturesBuffer;

	// 旋转及旋转的速度
	private static float				xRot;
	private static float				yRot;
	static float						xSpeed;
	static float						ySpeed;

	// 是否开启光效
	private static boolean				lighting	= false;
	// 选择纹理过滤的方式
	private static int					filter		= 0;

	private static int					objectIdx;
	private static GlCube				cube;
	private static GlCylinder			cylinder;
	private static GlDisk				disk;
	private static GlSphere				sphere;
	private static GlCylinder			cone;
	private static GlDisk				partialDisk;

	private static GlPlane				background;

	static
	{
		lightAmbBfr = FloatBuffer.wrap(lightAmb);
		lightDifBfr = FloatBuffer.wrap(lightDif);
		lightPosBfr = FloatBuffer.wrap(lightPos);

		cube = new GlCube(1.0f, true, true);
		cylinder = new GlCylinder(1.0f, 1.0f, 3.0f, 16, 4, true, true);
		disk = new GlDisk(0.5f, 1.5f, 16, 4, true, true);
		sphere = new GlSphere(1.3f, 16, 8, true, true);
		cone = new GlCylinder(1.0f, 0.0f, 3.0f, 16, 4, true, true);
		partialDisk = new GlDisk(0.5f, 1.5f, 16, 4, (float) (Math.PI / 4), (float) (7 * Math.PI / 4), true, true);

		background = new GlPlane(16, 12, true, true);
	}


	public GLRender(Context context)
	{
		mContext = context;
	}


	private void LoadTextures(GL10 gl)
	{

		gl.glEnable(GL10.GL_TEXTURE_2D);
		int[] textureIDs = new int[] { R.drawable.bg, R.drawable.reflect };

		// create textures
		texturesBuffer = IntBuffer.allocate(3 * textureIDs.length);
		gl.glGenTextures(3, texturesBuffer);

		for (int i = 0; i < textureIDs.length; i++)
		{
			// load bitmap
			Bitmap texture = Utils.getTextureFromBitmapResource(mContext, textureIDs[i]);

			// setup texture 0
			gl.glBindTexture(GL10.GL_TEXTURE_2D, texturesBuffer.get(3 * i));
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, texture, 0);

			// setup texture 1
			gl.glBindTexture(GL10.GL_TEXTURE_2D, texturesBuffer.get(3 * i + 1));
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, texture, 0);

			// setup texture 2
			gl.glBindTexture(GL10.GL_TEXTURE_2D, texturesBuffer.get(3 * i + 2));
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR_MIPMAP_NEAREST);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
			// 设置多级纹理
			Utils.generateMipmapsForBoundTexture(texture);

			// free bitmap
			texture.recycle();
		}
	}


	@Override
	public void onDrawFrame(GL10 gl)
	{
		// TODO Auto-generated method stub

		// 首先清理屏幕
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// 设置模型视图矩阵
		gl.glMatrixMode(GL10.GL_MODELVIEW);

		// 重置矩阵
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

		// 创建一个透视投影矩阵（设置视口大小）
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 100);
	}


	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		// TODO Auto-generated method stub

		// 告诉系统需要对透视进行修正
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

		// 设置清理屏幕的颜色
		gl.glClearColor(0, 0, 0, 1);

		// 启用深度缓存
		gl.glEnable(GL10.GL_DEPTH_TEST);

		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);

		gl.glCullFace(GL10.GL_BACK);

		// 设置光效
		gl.glEnable(GL10.GL_LIGHT0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbBfr);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDifBfr);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPosBfr);

		LoadTextures(gl);
	}


	private void draw(GL10 gl)
	{
		// set object color
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		// update lighting
		if (lighting)
		{
			gl.glEnable(GL10.GL_LIGHTING);
		}
		else
		{
			gl.glDisable(GL10.GL_LIGHTING);
		}

		// draw background
		gl.glPushMatrix();
		gl.glTranslatef(0, 0, -3);
		background.draw(gl);
		gl.glPopMatrix();

		// position object
		gl.glTranslatef(0, 0, -1);
		gl.glRotatef(xRot, 1, 0, 0);
		gl.glRotatef(yRot, 0, 1, 0);

		// compute reflection variables

		// 眼睛的向量
		GlVertex vEye = new GlVertex(0, 0, 1); 
		
		// 构建一个当前矩阵的对立矩阵
		GlMatrix mInv = new GlMatrix(); 
		mInv.rotate(-yRot, 0, 1, 0);
		mInv.rotate(-xRot, 1, 0, 0);
		mInv.translate(0, 0, 2);
		// 在模型视图中平移眼睛的位置
		mInv.transform(vEye);

		// 旋转矩阵
		GlMatrix mInvRot = new GlMatrix(); 
											
		mInvRot.rotate(xRot, 1, 0, 0);
		mInvRot.rotate(yRot, 0, 1, 0);

		// identify object to draw
		GlObject object = null;
		boolean doubleSided = false;
		switch (objectIdx)
		{
			case 0:
				object = cube;
				doubleSided = false;
				break;
			case 1:
				object = cylinder;
				doubleSided = true;
				break;
			case 2:
				object = disk;
				doubleSided = true;
				break;
			case 3:
				object = sphere;
				doubleSided = false;
				break;
			case 4:
				object = cone;
				doubleSided = true;
				break;
			case 5:
				object = partialDisk;
				doubleSided = true;
				break;
		}

		// adjust rendering parameters
		if (doubleSided)
		{
			gl.glDisable(GL10.GL_CULL_FACE);
			gl.glLightModelx(GL10.GL_LIGHT_MODEL_TWO_SIDE, lighting ? GL10.GL_TRUE : GL10.GL_FALSE);
		}
		else
		{
			gl.glEnable(GL10.GL_CULL_FACE);
			gl.glLightModelx(GL10.GL_LIGHT_MODEL_TWO_SIDE, GL10.GL_FALSE);
		}

		// draw object
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texturesBuffer.get(3 + filter));
		// 计算反射的纹理坐标
		object.calculateReflectionTexCoords(vEye, mInvRot);
		object.draw(gl);

		// update rotations
		xRot += xSpeed;
		yRot += ySpeed;
	}


	public static void toggleLighting()
	{
		lighting = !lighting;
	}


	public static void switchToNextFilter()
	{
		filter = (filter + 1) % 3;
	}


	public static void switchToNextObject()
	{
		objectIdx = (objectIdx + 1) % 6;
	}

}
