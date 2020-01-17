package com.android.chapter30.md2;

import java.util.Vector;

public class Model
{
	// Ö¡
	Vector<Frame>		frames;
	// ¶¯»­
	Vector<Animation>	animations;

	public Model()
	{
		this.frames = new Vector<Frame>();
		this.animations = new Vector<Animation>();
	}

	public Model(Mesh m)
	{
		this(new Mesh[]{m});
	}

	public Model(Mesh[] m)
	{
		this();
		for (int i = 0; i < m.length; i++)
			addFrame(new Frame("frame" + i, m[i]));
		this.animations.add(new Animation(this, 0, m.length, 15, "untitled"));
	}

	public Model(Frame[] frames)
	{
		this();
		for (int i = 0; i < frames.length; i++)
			addFrame(frames[i]);
		this.animations = Animation.buildAnimationsHeuristic(this, frames, 15);
	}

	public void addFrame(Frame f)
	{
		this.frames.add(f);
	}

	public Frame getFrame(int ix)
	{
		return frames.get(ix);
	}

	public int getFrameCount()
	{
		return frames.size();
	}

	public void addAnimation(Animation a)
	{
		this.animations.add(a);
	}

	public Animation getAnimation(int ix)
	{
		return animations.get(ix);
	}

	public int getAnimationCount()
	{
		return animations.size();
	}
}
