package com.android.chapter15;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.opengl.GLUtils;

public final class Utils
{
	private static Matrix	yFlipMatrix;

	static
	{
		yFlipMatrix = new Matrix();
		yFlipMatrix.postScale(-1, 1); // flip Y axis
	}


	public static Bitmap getTextureFromBitmapResource(Context context, int resourceId)
	{

		Bitmap bitmap = null;
		try
		{
			bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
			return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), yFlipMatrix, false);
		}
		finally
		{
			if (bitmap != null)
			{
				bitmap.recycle();
			}
		}
	}

	// 产生多重纹理
	public static void generateMipmapsForBoundTexture(Bitmap texture)
	{

		// generate the full texture (mipmap level 0)
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, texture, 0);

		Bitmap currentMipmap = texture;

		int width = texture.getWidth();
		int height = texture.getHeight();
		int level = 0;

		boolean reachedLastLevel;
		do
		{

			if (width > 1)
				width /= 2;
			if (height > 1)
				height /= 2;
			level++;
			reachedLastLevel = (width == 1 && height == 1);

			// 生成下一个纹理的贴图（缩放）
			Bitmap mipmap = Bitmap.createScaledBitmap(currentMipmap, width, height, true);
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, level, mipmap, 0);

			if (currentMipmap != texture)
			{
				currentMipmap.recycle();
			}

			currentMipmap = mipmap;

		}
		while (!reachedLastLevel);

		if (currentMipmap != texture)
		{
			currentMipmap.recycle();
		}
	}

	// 将x,y,z分别按顺序填放到数组中
	public static void setXYZ(float[] vector, int offset, float x, float y, float z)
	{
		vector[offset] = x;
		vector[offset + 1] = y;
		vector[offset + 2] = z;
	}

	// 将x,y,z分别对应的正、余弦值以及正切值放入数组中
	public static void setXYZn(float[] vector, int offset, float x, float y, float z)
	{
		float r = (float) Math.sqrt(x * x + y * y + z * z);
		setXYZ(vector, offset, x / r, y / r, z / r);
	}

	// 将x,y分别按顺序填放到数组中
	public static void setXY(float[] vector, int offset, float x, float y)
	{
		vector[offset] = x;
		vector[offset + 1] = y;
	}
}
