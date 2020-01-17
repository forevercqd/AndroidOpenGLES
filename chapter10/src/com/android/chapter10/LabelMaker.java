package com.android.chapter10;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.opengl.GLUtils;

public class LabelMaker
{
	private int				mStrikeWidth;
	private int				mStrikeHeight;
	// ��ɫ����ѡ��
	private boolean			mFullColor;
	// ��������ƵĻ���
	private Bitmap				mBitmap;
	// Canvas��paint
	private Canvas				mCanvas;
	private Paint				mClearPaint;
	// ������ͼ
	private int				mTextureID;
	// ��������
	private int				mU;
	private int				mV;
	private int				mLineHeight;
	
	// ��ǩ�б�
	private ArrayList<Label>	mLabels				= new ArrayList<Label>();
	
	// ����״̬
	private int				mState;
	// ����״̬
	private static final int	STATE_NEW			= 0;
	// ��ʼ��״��
	private static final int	STATE_INITIALIZED	= 1;
	// ����ַ���״̬
	private static final int	STATE_ADDING		= 2;
	// �����ַ���״̬
	private static final int	STATE_DRAWING		= 3;
	
	// ѡ����ɫ����Ⱥ͸߶�
	public LabelMaker(boolean fullColor, int strikeWidth, int strikeHeight)
	{
		mFullColor = fullColor;
		mStrikeWidth = strikeWidth;
		mStrikeHeight = strikeHeight;
		mClearPaint = new Paint();
		mClearPaint.setARGB(0, 0, 0, 0);
		mClearPaint.setStyle(Style.FILL);
		mState = STATE_NEW;
	}


	//  ��ʼ��
	public void initialize(GL10 gl)
	{
		mState = STATE_INITIALIZED;
		
		//  ��������
		int[] textures = new int[1];
		gl.glGenTextures(1, textures, 0);
		mTextureID = textures[0];
		
		// ������
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureID);

		// ʹ���������������ͼ��.
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);

		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

		//������������������ɫ����ʽ
	    //ֻ��������ɫ�����������������ɫ
		gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);
		
	    //�����������ɫ�������㡣
	    //gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);

	    //���ں����㡣
		//gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_BLEND);
	}


	// ж��-�����ڹر�ʱ����Ҫж��
	public void shutdown(GL10 gl)
	{
		if (gl != null)
		{
			if (mState > STATE_NEW)
			{
				int[] textures = new int[1];
				textures[0] = mTextureID;
				gl.glDeleteTextures(1, textures, 0);
				mState = STATE_NEW;
			}
		}
	}


	// ������ַ�����ǩ֮ǰ���ã�����������еı�ǩ
	public void beginAdding(GL10 gl)
	{
		checkState(STATE_INITIALIZED, STATE_ADDING);
		// �������б�ǩ
		mLabels.clear();
		mU = 0;
		mV = 0;
		mLineHeight = 0;
		// ����һ��ͼ��Bitmap
		Bitmap.Config config = mFullColor ? Bitmap.Config.ARGB_4444 : Bitmap.Config.ALPHA_8;
		mBitmap = Bitmap.createBitmap(mStrikeWidth, mStrikeHeight, config);
		// ��Canvas
		mCanvas = new Canvas(mBitmap);
		mBitmap.eraseColor(0);
	}


	// ��ӱ�ǩ��gl,�ַ��������ʣ�
	public int add(GL10 gl, String text, Paint textPaint)
	{
		return add(gl, null, text, textPaint);
	}


	// ��ӱ�ǩ��gl,����ͼƬ,�ַ��������ʣ�
	public int add(GL10 gl, Drawable background, String text, Paint textPaint)
	{
		return add(gl, background, text, textPaint, 0, 0);
	}

	// ���һ���ַ���Ϊ�հ׵ı�ǩ
	public int add(GL10 gl, Drawable drawable, int minWidth, int minHeight)
	{
		return add(gl, drawable, null, null, minWidth, minHeight);
	}

	// ��ӱ�ǩ��gl,����ͼƬ,�ַ���,����,��С���,��С�߶ȣ�
	public int add(GL10 gl, Drawable background, String text, Paint textPaint, int minWidth, int minHeight)
	{
		checkState(STATE_ADDING, STATE_ADDING);
		boolean drawBackground = background != null;
		boolean drawText = (text != null) && (textPaint != null);

		Rect padding = new Rect();
		// �жϱ����Ƿ���ڣ���������С��Ⱥ͸߶�
		if (drawBackground)
		{
			background.getPadding(padding);
			minWidth = Math.max(minWidth, background.getMinimumWidth());
			minHeight = Math.max(minHeight, background.getMinimumHeight());
		}

		int ascent = 0;
		int descent = 0;
		int measuredTextWidth = 0;
		
		// ��������ַ�������ȡ���ַ����Ŀ��
		if (drawText)
		{
			ascent = (int) Math.ceil(-textPaint.ascent());
			descent = (int) Math.ceil(textPaint.descent());
			measuredTextWidth = (int) Math.ceil(textPaint.measureText(text));
		}
		int textHeight = ascent + descent;
		int textWidth = Math.min(mStrikeWidth, measuredTextWidth);

		int padHeight = padding.top + padding.bottom;
		int padWidth = padding.left + padding.right;
		int height = Math.max(minHeight, textHeight + padHeight);
		int width = Math.max(minWidth, textWidth + padWidth);
		int effectiveTextHeight = height - padHeight;
		int effectiveTextWidth = width - padWidth;

		int centerOffsetHeight = (effectiveTextHeight - textHeight) / 2;
		int centerOffsetWidth = (effectiveTextWidth - textWidth) / 2;


		int u = mU;
		int v = mV;
		int lineHeight = mLineHeight;

		if (width > mStrikeWidth)
		{
			width = mStrikeWidth;
		}

		// ����Ⱥ͸߶��Ƿ��ܹ���ʾ�ַ���
		if (u + width > mStrikeWidth)
		{
			// No room, go to the next line:
			u = 0;
			v += lineHeight;
			lineHeight = 0;
		}
		lineHeight = Math.max(lineHeight, height);
		if (v + lineHeight > mStrikeHeight)
		{
			throw new IllegalArgumentException("Out of texture space.");
		}

		int vBase = v + ascent;

		// ���Ʊ���
		if (drawBackground)
		{
			background.setBounds(u, v, u + width, v + height);
			background.draw(mCanvas);
		}

		// �����ַ���
		if (drawText)
		{
			mCanvas.drawText(text, u + padding.left + centerOffsetWidth, vBase + padding.top + centerOffsetHeight, textPaint);
		}

		// We know there's enough space, so update the member variables
		mU = u + width;
		mV = v;
		mLineHeight = lineHeight;
		mLabels.add(new Label(width, height, u, v + height, width, -height));
		
		// ���ظñ�ǩ��ID
		return mLabels.size() - 1;
	}


	// ��ӽ���
	public void endAdding(GL10 gl)
	{
		checkState(STATE_ADDING, STATE_INITIALIZED);
		// �����ǻ��Ƶ�����
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureID);
		
		// װ������
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmap, 0);
		// �ͷ�����
		mBitmap.recycle();
		mBitmap = null;
		mCanvas = null;
	}


	// �õ����
	public float getWidth(int labelID)
	{
		return mLabels.get(labelID).width;
	}


	// �õ��߶�
	public float getHeight(int labelID)
	{
		return mLabels.get(labelID).height;
	}


	// ��ʼ����
	public void beginDrawing(GL10 gl, float viewWidth, float viewHeight)
	{
		checkState(STATE_INITIALIZED, STATE_DRAWING);
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureID);
		gl.glShadeModel(GL10.GL_FLAT);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glColor4x(0x10000, 0x10000, 0x10000, 0x10000);
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		
		// �ı�͸��Ϊ����
		gl.glOrthof(0.0f, viewWidth, 0.0f, viewHeight, 0.0f, 1.0f);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		
		// Magic offsets to promote consistent rasterization.
		gl.glTranslatef(0.375f, 0.375f, 0.0f);
		
	}


	// ����
	public void draw(GL10 gl, float x, float y, int labelID)
	{
		checkState(STATE_DRAWING, STATE_DRAWING);
		gl.glPushMatrix();
		float snappedX = (float) Math.floor(x);
		float snappedY = (float) Math.floor(y);
		gl.glTranslatef(snappedX, snappedY, 0.0f);
		Label label = mLabels.get(labelID);
		// ����2D��ͼ
		gl.glEnable(GL10.GL_TEXTURE_2D);
		// ��������GL_TEXTURE_CROP_RECT_OES����ӳ��
		((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, label.mCrop, 0);
		((GL11Ext) gl).glDrawTexiOES((int) snappedX, (int) snappedY, 0, (int) label.width, (int) label.height);
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glPopMatrix();
	}


	// ���ƽ���
	public void endDrawing(GL10 gl)
	{
		checkState(STATE_DRAWING, STATE_INITIALIZED);
		gl.glDisable(GL10.GL_BLEND);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glPopMatrix();	
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glPopMatrix();
	}

	// ���״̬�Ƿ���ȷ
	private void checkState(int oldState, int newState)
	{
		if (mState != oldState)
		{
			throw new IllegalArgumentException("Can't call this method now.");
		}
		mState = newState;
	}

	private static class Label
	{
		public float	width;
		public float	height;
		public int[]	mCrop;

		public Label(float width, float height,int cropU, int cropV, int cropW, int cropH)
		{
			this.width = width;
			this.height = height;
			int[] crop = new int[4];
			crop[0] = cropU;
			crop[1] = cropV;
			crop[2] = cropW;
			crop[3] = cropH;
			mCrop = crop;
		}
	}
}
