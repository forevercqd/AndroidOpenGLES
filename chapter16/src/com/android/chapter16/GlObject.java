package com.android.chapter16;

import javax.microedition.khronos.opengles.GL10;

public abstract class GlObject
{
	public abstract void draw(GL10 gl);
	public abstract void calculateReflectionTexCoords(GlVertex vEye, GlMatrix mInvRot);
}

