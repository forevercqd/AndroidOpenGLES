package com.android.chapter30;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.GLSurfaceView.Renderer;
import android.view.KeyEvent;

import com.android.chapter30.md2.Animation;
import com.android.chapter30.md2.FixedPointUtils;
import com.android.chapter30.md2.Mesh;
import com.android.chapter30.md2.Model;

public class GLRender implements Renderer
{
	private Model m;
	Bitmap bmp = null;

	int tex;
	int verts;
	Animation cAnim;
	int anim_ix;
	int angle;
	
	int lightAmbient[] = new int[] { FixedPointUtils.toFixed(0.2f), 
									 FixedPointUtils.toFixed(0.3f), 
									 FixedPointUtils.toFixed(0.6f), FixedPointUtils.ONE };
	int lightDiffuse[] = new int[] { FixedPointUtils.ONE, FixedPointUtils.ONE, FixedPointUtils.ONE, FixedPointUtils.ONE };

	int matAmbient[] = new int[] { FixedPointUtils.ONE, FixedPointUtils.ONE, FixedPointUtils.ONE, FixedPointUtils.ONE };
	int matDiffuse[] = new int[] { FixedPointUtils.ONE, FixedPointUtils.ONE, FixedPointUtils.ONE, FixedPointUtils.ONE };

	int[] pos = new int[] {0,20<<16,20<<16, FixedPointUtils.ONE};

	int current = 0;
	int next = 1;
	int mix = 0;

	private IntBuffer vertices;
	private IntBuffer normals;
	private IntBuffer texCoords;
	private ShortBuffer indices;
	
	// 构建纹理
	protected static int loadTexture(GL10 gl, Bitmap bmp)
	{

		int[] tmp_tex = new int[1];

		gl.glGenTextures(1, tmp_tex, 0);
		int tx = tmp_tex[0];
		gl.glBindTexture(GL10.GL_TEXTURE_2D, tx);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);
		bmp.recycle();

		return tx;
	}
	
	
	// 下一个动画
	protected void nextAnimation()
	{
		if (m.getAnimationCount() > 0)
		{
			anim_ix = (anim_ix + 1) % m.getAnimationCount();
			cAnim = m.getAnimation(anim_ix);
			current = cAnim.getStartFrame();
			next = current + 1;
		}
		else
		{
			cAnim = null;
		}
	}
	
	public GLRender(Model m, Context c)
	{
		this.m = m;
		anim_ix = -1;
		nextAnimation();

		// 装载纹理贴图
		bmp = BitmapFactory.decodeResource(c.getResources(), R.drawable.tris_t);
	}
	
	protected void interpolate() {
		Mesh m1 = m.getFrame(current).getMesh();
		Mesh m2 = m.getFrame(next).getMesh();
		int ct = 0;
		for (int i=0;i<m1.getFaceCount();i++) {
			int[] face = m1.getFace(i);
			int[] face_n = m1.getFaceNormals(i);
			for (int j=0;j<3;j++) {
				int[] n1 = m1.getNormalx(face_n[j]);
				int[] v1 = m1.getVertexx(face[j]);
				int[] n2 = m2.getNormalx(face_n[j]);
				int[] v2 = m2.getVertexx(face[j]);

				for (int k=0;k<3;k++) {
					vertices.put(ct, v1[k]+FixedPointUtils.multiply(v2[k]-v1[k],mix));
					normals.put(ct, n1[k]+FixedPointUtils.multiply(n2[k]-n1[k], mix));
					ct++;
				}
			}
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		switch (event.getKeyCode())
		{
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			angle -= 5;
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			angle += 5;
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
			nextAnimation();
			break;
		}
		return true;
	}
	
	public void onDrawFrame(GL10 gl)
	{
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		GLU.gluLookAt(gl, 0, 0, 5, 0, 0, 0, 0, 1, 0);
		
		gl.glTranslatef(0, 0, -5);
		gl.glRotatex(-(angle << 16), 0, 0x10000, 0);

		interpolate();

		gl.glVertexPointer(3, GL10.GL_FIXED, 0, vertices);
		gl.glNormalPointer(GL10.GL_FIXED, 0, normals);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);

		gl.glBindTexture(GL10.GL_TEXTURE_2D, tex);
		gl.glDrawElements(GL10.GL_TRIANGLES, verts, GL10.GL_UNSIGNED_SHORT, indices);

		mix += 0x8000;
		if (mix >= 0x10000)
		{
			current = next;
			next++;
			if (cAnim != null)
			{
				if (next > cAnim.getEndFrame()) next = cAnim.getStartFrame();
			}
			else
			{
				if (next > m.getFrameCount()) next = 0;
			}
			mix = 0x0;
		}
	}

	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glViewport(0,0,width,height);
		GLU.gluPerspective(gl, 45.0f, ((float)width)/height, 1f, 100f);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glClearColor(0, 0, 0.5f, 1);

		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);

		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_LIGHT0);
		gl.glEnable(GL10.GL_NORMALIZE);

		gl.glLightxv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient, 0);
		gl.glLightxv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse, 0);
		gl.glLightxv(GL10.GL_LIGHT0, GL10.GL_POSITION, pos, 0);

		gl.glMaterialxv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, matAmbient, 0);
		gl.glMaterialxv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, matDiffuse, 0);

		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		gl.glEnable(GL10.GL_CULL_FACE);

		ByteBuffer bb;

		Mesh msh = m.getFrame(0).getMesh();

		if (msh.getTextureFile() != null)
		{
			gl.glEnable(GL10.GL_TEXTURE_2D);
			tex = loadTexture(gl, bmp);
		}

		verts = msh.getFaceCount() * 3;

		bb = ByteBuffer.allocateDirect(verts * 3 * 4);
		bb.order(ByteOrder.nativeOrder());
		vertices = bb.asIntBuffer();

		bb = ByteBuffer.allocateDirect(verts * 3 * 4);
		bb.order(ByteOrder.nativeOrder());
		normals = bb.asIntBuffer();

		bb = ByteBuffer.allocateDirect(verts * 2 * 4);
		bb.order(ByteOrder.nativeOrder());
		texCoords = bb.asIntBuffer();

		bb = ByteBuffer.allocateDirect(verts * 2);
		bb.order(ByteOrder.nativeOrder());
		indices = bb.asShortBuffer();

		short ct = 0;
		for (int i = 0; i < msh.getFaceCount(); i++)
		{
			int[] face = msh.getFace(i);
			int[] face_n = msh.getFaceNormals(i);
			int[] face_tx = msh.getFaceTextures(i);
			for (int j = 0; j < 3; j++)
			{
				int[] n = msh.getNormalx(face_n[j]);
				int[] v = msh.getVertexx(face[j]);
				for (int k = 0; k < 3; k++)
				{
					vertices.put(v[k]);
					normals.put(n[k]);
				}
				int[] tx = msh.getTextureCoordinatex(face_tx[j]);
				texCoords.put(tx[0]);
				texCoords.put(tx[1]);
				indices.put(ct++);
			}
		}
		vertices.position(0);
		normals.position(0);
		texCoords.position(0);
		indices.position(0);

		gl.glTexCoordPointer(2, GL10.GL_FIXED, 0, texCoords);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}

}
