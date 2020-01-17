package com.android.chapter24;

import java.nio.ByteBuffer;

public class Texture
{
	// 图像数据
    ByteBuffer imageData;
    // 图像中每个像素的颜色深度
    int bpp;	
    // 宽度和高度
    int width;											
    int height;		
    // 贴图ID
    int[] texID = new int[1];		
    // 图像数据的类型（rgb 或者 argb）
    int type;
}

