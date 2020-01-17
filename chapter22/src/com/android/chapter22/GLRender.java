package com.android.chapter22;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
import android.view.KeyEvent;

public class GLRender implements Renderer {
	// 贴图
	private Bitmap mBitmapTexture = null;

	private float rotz = 0.0f;
	private boolean rotateLeft = false;
	private boolean rotateRight = false;
	// 是否显示控制点
	private boolean showCPoints = true;
	// 贝塞尔曲面类
	private BezierPatch mybezier = new BezierPatch();

	// 细分精度，控制曲面的显示精度
	private int divs = 7;
	private GL10 gl10 = null;

	public GLRender(Context context) {
		mBitmapTexture = BitmapFactory.decodeResource(context.getResources(), R.drawable.img);
	}

	public void onDrawFrame(GL10 gl) {

		drawGLScene(gl);
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		float ratio = (float) width / height;

		height = (height == 0) ? 1 : height;

		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();

		GLU.gluPerspective(gl, 45, ratio, 1, 1000);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl10 = gl;
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glClearColor(0.0f, 0.0f, 0.5f, 0.5f);
		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

		int[] texture = new int[1];

		loadGLTexture(gl, texture);

		mybezier.setTexture(texture[0]);
		initBezier(mybezier);

		mybezier.genBezier(gl10, divs);
	}

	public void increaseDivisions() {
		divs++;
		if (divs > 64) {
			divs--;
		}
		mybezier.genBezier(gl10, divs);
	}

	public void decreaseDivisions() {
		divs--;
		if (divs < 64) {
			divs++;
		}
		mybezier.genBezier(gl10, divs);
	}

	public void toggleControlPoints() {
		showCPoints = !showCPoints;
	}

	public void rotateLeft() {
		this.rotateLeft = !this.rotateLeft;
		rotateRight = false;
	}

	public void rotateRight() {
		this.rotateRight = !this.rotateRight;
		rotateLeft = false;
	}

	// 设置贝塞尔曲面的控制点
	private void initBezier(BezierPatch bezier) {
		bezier.anchors[0][0] = new Point3D(-0.75f, -0.75f, -0.5f);
		bezier.anchors[0][1] = new Point3D(-0.25f, -0.75f, 0.0f);
		bezier.anchors[0][2] = new Point3D(0.25f, -0.75f, 0.0f);
		bezier.anchors[0][3] = new Point3D(0.75f, -0.75f, -0.5f);
		bezier.anchors[1][0] = new Point3D(-0.75f, -0.25f, -0.75f);
		bezier.anchors[1][1] = new Point3D(-0.25f, -0.25f, 0.5f);
		bezier.anchors[1][2] = new Point3D(0.25f, -0.25f, 0.5f);
		bezier.anchors[1][3] = new Point3D(0.75f, -0.25f, -0.75f);
		bezier.anchors[2][0] = new Point3D(-0.75f, 0.25f, 0.0f);
		bezier.anchors[2][1] = new Point3D(-0.25f, 0.25f, -0.5f);
		bezier.anchors[2][2] = new Point3D(0.25f, 0.25f, -0.5f);
		bezier.anchors[2][3] = new Point3D(0.75f, 0.25f, 0.0f);
		bezier.anchors[3][0] = new Point3D(-0.75f, 0.75f, -0.5f);
		bezier.anchors[3][1] = new Point3D(-0.25f, 0.75f, -1.0f);
		bezier.anchors[3][2] = new Point3D(0.25f, 0.75f, -1.0f);
		bezier.anchors[3][3] = new Point3D(0.75f, 0.75f, -0.5f);
	}

	// 装载纹理贴图
	private void loadGLTexture(GL10 gl, int[] textures) {

		gl.glGenTextures(1, textures, 0);

		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmapTexture, 0);
	}

	// 更新--旋转..
	private void update() {
		if (rotateLeft)
			rotz -= 10.0f;
		if (rotateRight)
			rotz += 10.0f;
	}

	// 绘制
	private void drawGLScene(GL10 gl) {

		int i, j;
		float baseverts[][] = new float[4][3];

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		gl.glTranslatef(0.0f, 0.5f, -4.0f);
		gl.glRotatef(-75.0f, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(rotz, 0.0f, 0.0f, 1.0f);

		gl.glBindTexture(GL10.GL_TEXTURE_2D, mybezier.texture[0]);

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, makeFloatBuffer(mybezier.vertices));
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, makeFloatBuffer(mybezier.texcoords));

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		for (i = 0; i < divs; i++) {
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, i * (divs + 1) * 2, (divs + 1) * 2);
		}

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		if (showCPoints) {
			gl.glDisable(GL10.GL_TEXTURE_2D);

			gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);

			for (i = 0; i < 4; i++) {
				for (j = 0; j < 4; j++) {
					baseverts[j][0] = mybezier.anchors[i][j].x;
					baseverts[j][1] = mybezier.anchors[i][j].y;
					baseverts[j][2] = mybezier.anchors[i][j].z;
				}
				gl.glVertexPointer(3, GL10.GL_FLOAT, 0, makeFloatBuffer(baseverts));
				gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, 4);
			}

			for (i = 0; i < 4; i++) {
				for (j = 0; j < 4; j++) {
					baseverts[j][0] = mybezier.anchors[j][i].x;
					baseverts[j][1] = mybezier.anchors[j][i].y;
					baseverts[j][2] = mybezier.anchors[j][i].z;
				}
				gl.glVertexPointer(3, GL10.GL_FLOAT, 0, makeFloatBuffer(baseverts));
				gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, 4);
			}

			gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			gl.glEnable(GL10.GL_TEXTURE_2D);
		}

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

		update();
	}

	// 3D空间的点
	private static class Point3D {
		public float x, y, z;

		public Point3D() {
		}

		public Point3D(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public Point3D add(Point3D q) {
			return new Point3D(x + q.x, y + q.y, z + q.z);
		}

		public Point3D scale(float c) {
			return new Point3D(x * c, y * c, z * c);
		}

		// 计算贝塞尔方程的值
		// 变量u的范围在0-1之间
		public static Point3D bernstein(float u, Point3D[] p) {
			Point3D a = p[0].scale((float) Math.pow(u, 3));
			Point3D b = p[1].scale(3 * (float) Math.pow(u, 2) * (1 - u));
			Point3D c = p[2].scale(3 * u * (float) Math.pow((1 - u), 2));
			Point3D d = p[3].scale((float) Math.pow((1 - u), 3));

			return a.add(b).add(c).add(d);
		}
	}

	/* 贝塞尔曲面类 */
	private static class BezierPatch {
		// 由4x4网格组成
		public Point3D[][] anchors;
		// 贴图
		private int[] texture = new int[1];

		// 顶点数组和纹理坐标数组
		float vertices[][] = new float[(64) * (64 + 1) * 2][3];
		float texcoords[][] = new float[(64) * (64 + 1) * 2][2];

		// 初始化贝塞尔曲面数据
		public BezierPatch() {
			anchors = new Point3D[4][4];
			for (int i = 0; i < anchors.length; i++) {
				Point3D[] anchor = anchors[i];
				for (int j = 0; j < anchor.length; j++) {
					anchor[j] = new Point3D();
				}
			}
		}

		// 设置贴图
		public void setTexture(int texture) {
			this.texture[0] = texture;
		}

		// 生成贝塞尔曲面的数据（顶点数组和纹理坐标数组）
		private void genBezier(GL10 gl, int divs) {
			int u = 0;
			int v;
			float py, px, pyold;
			Point3D[] temp = new Point3D[4];
			Point3D[] last = new Point3D[divs + 1];

			// 获得u方向的四个控制点
			temp[0] = anchors[0][3];
			temp[1] = anchors[1][3];
			temp[2] = anchors[2][3];
			temp[3] = anchors[3][3];

			// 根据细分数，创建各个分割点额参数
			for (v = 0; v <= divs; v++) {
				px = ((float) v) / ((float) divs);
				// 使用Bernstein函数求的分割点的坐标
				last[v] = Point3D.bernstein(px, temp);
			}

			for (u = 1; u <= divs; u++) {

				// 计算v方向上的细分点的参数
				py = ((float) u) / ((float) divs);
				// 上一个v方向上的细分点的参数
				pyold = ((float) u - 1.0f) / ((float) divs);

				// 计算每个细分点v方向上贝塞尔曲面的控制点
				temp[0] = Point3D.bernstein(py, anchors[0]);
				temp[1] = Point3D.bernstein(py, anchors[1]);
				temp[2] = Point3D.bernstein(py, anchors[2]);
				temp[3] = Point3D.bernstein(py, anchors[3]);

				// 计算贝塞尔曲面数据
				for (v = 0; v <= divs; v++) {
					px = ((float) v) / ((float) divs);

					texcoords[(u - 1) * 2 * (divs + 1) + v * 2][0] = pyold;
					texcoords[(u - 1) * 2 * (divs + 1) + v * 2][1] = 1.0f - px;

					vertices[(u - 1) * 2 * (divs + 1) + v * 2][0] = last[v].x;
					vertices[(u - 1) * 2 * (divs + 1) + v * 2][1] = last[v].y;
					vertices[(u - 1) * 2 * (divs + 1) + v * 2][2] = last[v].z;

					last[v] = Point3D.bernstein(px, temp);

					texcoords[(u - 1) * 2 * (divs + 1) + v * 2 + 1][0] = py;
					texcoords[(u - 1) * 2 * (divs + 1) + v * 2 + 1][1] = 1.0f - px;

					vertices[(u - 1) * 2 * (divs + 1) + v * 2 + 1][0] = last[v].x;
					vertices[(u - 1) * 2 * (divs + 1) + v * 2 + 1][1] = last[v].y;
					vertices[(u - 1) * 2 * (divs + 1) + v * 2 + 1][2] = last[v].z;
				}
			}

			last = null;
		}
	}

	// 一维数组转换成缓冲区
	protected static FloatBuffer makeFloatBuffer(float[] arr) {

		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(arr);
		fb.position(0);
		return fb;
	}

	// 二维数组转换成缓冲区
	protected static FloatBuffer makeFloatBuffer(float[][] arr) {

		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * arr[0].length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		for (int i = 0; i < arr.length; i++) {
			fb.put(arr[i]);
		}

		fb.position(0);
		return fb;
	}

	// 一维数整型组转换成缓冲区
	protected static IntBuffer makeIntBuffer(int[] arr) {
		// 分配指定大小的缓冲区
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
		// 修改此缓冲区的字节顺序(获取底层平台的本机字节顺序)
		bb.order(ByteOrder.nativeOrder());
		// 创建此字节缓冲区的视图，作为 int 缓冲区
		IntBuffer ib = bb.asIntBuffer();
		// 将数据写入缓冲区
		ib.put(arr);
		// 缓冲区的位置设置为0
		ib.position(0);
		// 返回此缓冲区的位置
		return ib;
	}

	// Bitmap转换成缓冲区
	protected static ByteBuffer makeByteBuffer(Bitmap bmp) {
		ByteBuffer bb = ByteBuffer.allocateDirect(bmp.getHeight() * bmp.getWidth() * 4);
		// 修改缓冲区的字节顺序
		bb.order(ByteOrder.BIG_ENDIAN);
		IntBuffer ib = bb.asIntBuffer();

		for (int y = 0; y < bmp.getHeight(); y++)
			for (int x = 0; x < bmp.getWidth(); x++) {
				int pix = bmp.getPixel(x, bmp.getHeight() - y - 1);
				// Convert ARGB -> RGBA
				byte alpha = (byte) ((pix >> 24) & 0xFF);
				byte red = (byte) ((pix >> 16) & 0xFF);
				byte green = (byte) ((pix >> 8) & 0xFF);
				byte blue = (byte) ((pix) & 0xFF);

				ib.put(((red & 0xFF) << 24) | ((green & 0xFF) << 16) | ((blue & 0xFF) << 8) | ((alpha & 0xFF)));
			}
		ib.position(0);
		bb.position(0);
		return bb;
	}

	// 按键处理
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			rotateLeft();
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			rotateRight();
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			increaseDivisions();
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			decreaseDivisions();
		} else if (keyCode == KeyEvent.KEYCODE_SPACE) {
			toggleControlPoints();
		}
		return false;
	}
}
