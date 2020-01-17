package com.android.chapter13;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.GLSurfaceView.Renderer;
import android.view.KeyEvent;

public class GLRender implements Renderer
{
	private Bitmap mBitmap;
	
	// ������������
	public final static int MAX_PARTICLES =1000;
	
	// �л�Ч��
	boolean rainbow=true;  
	// �������
	Random random = new Random();
	// ���������ٶ�
	float slowdown=0.5f;  
	
	// �ٶ�
	float xspeed=1;        
	float yspeed=3; 
	// ����
	float zoom=-30.0f;    

	int loop;           
	int col=0;          
	int delay;         
	// ����
	int mTtexture[];     
	
	// ��ɫ����
	static float colors[][]=
	{
	   {1.0f,  0.5f,  0.5f},
	   {1.0f,  0.75f, 0.5f},
	   {1.0f,  1.0f,  0.5f},
	   {0.75f, 1.0f,  0.5f},
	   {0.5f,  1.0f,  0.5f},
	   {0.5f,  1.0f,  0.75f},
	   {0.5f,  1.0f,  1.0f},
	   {0.5f,  0.75f, 1.0f},
	   {0.5f,  0.5f,  1.0f},
	   {0.75f, 0.5f,  1.0f},
	   {1.0f,  0.5f,  1.0f},
	   {1.0f,  0.5f,  0.75f}
	};
	
	// ��������
	Particle particles[] = new Particle[MAX_PARTICLES];
	
	
	public GLRender(Context context)
	{
		// װ��ͼƬ
		mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.particle);
		mTtexture = new int[1];
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
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 200);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		// TODO Auto-generated method stub
		
		//����ϵͳ��Ҫ��͸�ӽ�������
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		
		//����������Ļ����ɫ
		gl.glClearColor(0, 0, 0, 1);
		
		/* ����ʹ����Ȳ��� */
		//������Ȼ���
		//gl.glEnable(GL10.GL_DEPTH_TEST);
		
		// �򿪻�� 
		gl.glEnable(GL10.GL_BLEND);

		// ���û�ɫ�ķ�ʽ
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);

		// �Բ����������������������õķ�ʽ��
		gl.glHint(GL10.GL_POINT_SMOOTH_HINT, GL10.GL_NICEST);		
		
		LoadTextures(gl);
		
		initData();
	}
	
	public void LoadTextures(GL10 gl)
	{
		// ����2D������ͼ
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		IntBuffer textureBuffer = IntBuffer.allocate(1);
		// ��������
		mTtexture[0] = textureBuffer.get();

        // ������
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTtexture[0]);
		
        // ���Բ�ֵ�㷨
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        
        // װ������
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmap, 0);
	}

	
	private int rand()
	{
		return Math.abs(random.nextInt(1000));
	}
	
	private void initData()
	{
		for (loop = 0; loop < MAX_PARTICLES; loop++)
		{
			int color = loop*(12/MAX_PARTICLES);
			float xi, yi, zi;

			xi = (float) ((rand() % 50) - 26.0f) * 10.0f;
			yi = zi = (float) ((rand() % 50) - 25.0f) * 10.0f;

			resetParticle(loop, color, xi, yi, zi);
		}
	}
	
	private void resetParticle(int num, int color, float xDir, float yDir, float zDir)
	{
		Particle tmp = new Particle();
	    /* Make the particels active */
		tmp.active=true;
	    /* Give the particles life */
		tmp.life=1.0f;
	    /* Random Fade Speed */
		tmp.fade=(float)(rand()%100)/1000.0f+0.003f;
	    /* Select Red Rainbow Color */
		tmp.r=colors[color][0];
	    /* Select Green Rainbow Color */
		tmp.g=colors[color][1];
	    /* Select Blue Rainbow Color */
		tmp.b=colors[color][2];
	    /* Set the position on the X axis */
		tmp.x=0.0f;
	    /* Set the position on the Y axis */
		tmp.y=0.0f;
	    /* Set the position on the Z axis */
		tmp.z=0.0f;
	    /* Random Speed On X Axis */
		tmp.xi=xDir;
	    /* Random Speed On Y Axi */
		tmp.yi=yDir;
	    /* Random Speed On Z Axis */
		tmp.zi=zDir;
	    /* Set Horizontal Pull To Zero */
	    tmp.xg=0.0f;
	    /* Set Vertical Pull Downward */
	    tmp.yg=-0.5f;
	    /* Set Pull On Z Axis To Zero */
	    tmp.zg=0.0f;

	    particles[num] = tmp;
	    return;
	}
	
	
	private void draw(GL10 gl)
	{
		FloatBuffer vertices = FloatBuffer.wrap(new float[12]);
		FloatBuffer texcoords = FloatBuffer.wrap(new float[8]);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texcoords);

		gl.glLoadIdentity();

		// �޸�ÿһ������
		for (loop = 0; loop < MAX_PARTICLES; loop++)
		{
			// �����Ƿ񱻼���
			if (particles[loop].active)
			{
				float x = particles[loop].x;
				float y = particles[loop].y;

				// z�᷽����ܷŴ�
				float z = particles[loop].z + zoom;

				// ������ɫ����������ֵ����͸����
				gl.glColor4f(particles[loop].r, particles[loop].g, particles[loop].b, particles[loop].life);

				texcoords.clear();
				vertices.clear();
				/* Top Right */
				texcoords.put(1.0f);
				texcoords.put(1.0f);
				vertices.put(x + 0.5f);
				vertices.put(y + 0.5f);
				vertices.put(z);

				/* Top Left */
				texcoords.put(0.0f);
				texcoords.put(1.0f);
				vertices.put(x - 0.5f);
				vertices.put(y + 0.5f);
				vertices.put(z);

				/* Bottom Right */
				texcoords.put(1.0f);
				texcoords.put(0.0f);
				vertices.put(x + 0.5f);
				vertices.put(y - 0.5f);
				vertices.put(z);

				/* Bottom Left */
				texcoords.put(0.0f);
				texcoords.put(0.0f);
				vertices.put(x - 0.5f);
				vertices.put(y - 0.5f);
				vertices.put(z);

				// �����ı���
				gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

				// ͨ��x,y,z3��������ٶȷֱ�ı����ӵ�λ��
				particles[loop].x += particles[loop].xi / (slowdown * 100);
				particles[loop].y += particles[loop].yi / (slowdown * 100);
				particles[loop].z += particles[loop].zi / (slowdown * 100);

				// x,y,z�����ϵ��������ٶ�
				particles[loop].xi += particles[loop].xg;
				particles[loop].yi += particles[loop].yg;
				particles[loop].zi += particles[loop].zg;

				// �������ӵ�������˥��
				particles[loop].life -= particles[loop].fade;

				// ��������������֮�����������һ��
				if (particles[loop].life < 0.0f)
				{
					float xi, yi, zi;

					xi = xspeed + (float) ((rand() % 60) - 32.0f);
					yi = yspeed + (float) ((rand() % 60) - 30.0f);
					zi = (float) ((rand() % 60) - 30.0f);
					resetParticle(loop, col, xi, yi, zi);
				}
			}
		}
		
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		switch (keyCode)
		{
			case KeyEvent.KEYCODE_8:
				if (particles[loop].yg < 1.5f)
				{
					particles[loop].yg += 0.1f;
				}
				break;
			case KeyEvent.KEYCODE_2:
				if (particles[loop].yg > -1.5f)
				{
					particles[loop].yg -= 0.1f;
				}
				break;
			case KeyEvent.KEYCODE_6:
				if ( particles[loop].xg<1.5f )
				{
					particles[loop].xg+=0.1f;
				}
				break;
			case KeyEvent.KEYCODE_4:
				if ( particles[loop].xg>-1.5f )
				{
					 particles[loop].xg-=0.1f;
				}
				break;
			case KeyEvent.KEYCODE_SPACE:// ��Tab����ʹ���ӻص�ԭ��
				particles[loop].x=0.0f;					
				particles[loop].y=0.0f;					
				particles[loop].z=0.0f;					
				particles[loop].xi=(float)((rand()%50)-26.0f)*10.0f;	// ��������ٶ�
				particles[loop].yi=(float)((rand()%50)-25.0f)*10.0f;	
				particles[loop].zi=(float)((rand()%50)-25.0f)*10.0f;
				break;
			case KeyEvent.KEYCODE_D:
				if ( slowdown>1.0f )
				{
					slowdown-=0.1f;		// ��D�ţ���������
				}
				break;
			case KeyEvent.KEYCODE_A:
				if ( slowdown<4.0f )
				{
					slowdown+=0.1f;	// ��a�ţ���������
				}
				break;
			case KeyEvent.KEYCODE_W:
				zoom+=0.1f;		// �����ӿ����ӵ�
				break;
			case KeyEvent.KEYCODE_S:
				zoom-=0.1f;		// ������Զ���ӵ�
				break;
			case KeyEvent.KEYCODE_DPAD_UP:
				if ( yspeed<200 )
				{
					// ������������Y����������ٶ�
					yspeed+=1.0f;
				}
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				if ( yspeed>-200 )
				{
					// ���¼�������Y����������ٶ�
					yspeed-=1.0f;
				}
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				if ( xspeed<200 )
				{
					// ������������X����������ٶ�
					xspeed+=1.0f;
				}
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				if ( xspeed>-200 )
				{
					// �����������X����������ٶ�
					xspeed-=1.0f;
				}
				break;
			
		}
		return false;
	}
}