package com.android.chapter24;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javax.microedition.khronos.opengles.GL10;

public class TGALoader
{
	// 未压缩的TGA头
	private static final ByteBuffer	uTGAcompare;
	// 压缩的TGA头
	private static final ByteBuffer	cTGAcompare;

	static
	{
		//我们需要定义一对文件头，那样我们能够告诉程序什么类型的文件头处于有效的图像上。
		//如果是未压缩的TGA图像，前12字节将会是0 0 2 0 0 0 0 0 0 0 0 0，
		//如果是RLE压缩的，则是0 0 10 0 0 0 0 0 0 0 0 0。这两个值允许我们检查正在读取的文件是否有效。
		byte[] uncompressedTgaHeader = new byte[]{0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		byte[] compressedTgaHeader = new byte[]{0, 0, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0};

		// Uncompressed TGA Header
		uTGAcompare = BufferUtil.newByteBuffer(uncompressedTgaHeader.length);
		uTGAcompare.put(uncompressedTgaHeader);
		uTGAcompare.flip();

		// Compressed TGA Header
		cTGAcompare = BufferUtil.newByteBuffer(compressedTgaHeader.length);
		cTGAcompare.put(compressedTgaHeader);
		cTGAcompare.flip();
	}

	// 装载一个TGA文件
	public static void loadTGA(Texture texture, InputStream file) throws IOException
	{
		ByteBuffer header = BufferUtil.newByteBuffer(12);
		ReadableByteChannel in = Channels.newChannel(file);
		readBuffer(in, header);

		// 如果文件头附合未压缩的文件头格式
		if (uTGAcompare.equals(header))
		{
			// 读取未压缩的TGA文件
			loadUncompressedTGA(texture, in);
		}
		// 如果文件头附合压缩的文件头格式
		else if (cTGAcompare.equals(header))
		{
			// 读取压缩的TGA格式
			loadCompressedTGA(texture, in);
		}
		else
		{
			// 错误
			in.close();
			throw new IOException("TGA file be type 2 or type 10 ");

		}
	}

	// 读取一个数据包到缓冲区中
	private static void readBuffer(ReadableByteChannel in, ByteBuffer buffer) throws IOException
	{
		while (buffer.hasRemaining())
		{
			in.read(buffer);
		}
		buffer.flip();
	}

	// 读取未压缩的TGA文件
	private static void loadUncompressedTGA(Texture texture, ReadableByteChannel in) throws IOException
	{
		TGA tga = new TGA();
		// 读取文件头
		readBuffer(in, tga.header);
		// 计算TGA文件的宽度 (highbyte*256+lowbyte)
		texture.width = (unsignedByteToInt(tga.header.get(1)) << 8) + unsignedByteToInt(tga.header
				.get(0));
		// 计算TGA文件的高度 (highbyte*256+lowbyte)
		texture.height = (unsignedByteToInt(tga.header.get(3)) << 8) + unsignedByteToInt(tga.header
				.get(2));
		// 计算BPP
		texture.bpp = unsignedByteToInt(tga.header.get(4));
		// 拷贝Width到本地结构中去
		tga.width = texture.width;
		// 拷贝Height到本地结构中去
		tga.height = texture.height;
		// 拷贝Bpp到本地结构中去
		tga.bpp = texture.bpp;

		// 确认所有的信息都是有效的
		// 我们需要确认高度和宽度至少为1个像素，并且bpp是24或32。
		if ((texture.width <= 0) || (texture.height <= 0) || ((texture.bpp != 24) && (texture.bpp != 32))) { throw new IOException("Invalid texture information"); }
		// 如果是24 bit图像
		if (texture.bpp == 24)
			texture.type = GL10.GL_RGB; // 设置类型为RGB
		else
			texture.type = GL10.GL_RGBA;// 设置类型为RGBA

		// 计算BPP
		tga.bytesPerPixel = (tga.bpp / 8);
		// 计算存储图像所需的内存(即缓冲区大小)
		tga.imageSize = (tga.bytesPerPixel * tga.width * tga.height);
		// 分配内存（申请指定大小的缓冲区）
		texture.imageData = BufferUtil.newByteBuffer(tga.imageSize);

		// 试读取所有图像数据
		readBuffer(in, texture.imageData);

		// 通过循环取得图像数据
		//TGA文件用逆OpenGL需求顺序的方式存储图像，因此我们必须将格式从BGR到RGB。
		//为了达到这一点，我们交换每个像素的第一个和第三个字节的内容。
		for (int cswap = 0; cswap < tga.imageSize; cswap += tga.bytesPerPixel)
		{
			byte temp = texture.imageData.get(cswap);
			texture.imageData.put(cswap, texture.imageData.get(cswap + 2));
			texture.imageData.put(cswap + 2, temp);
		}
	}

	// 读取压缩的TGA文件
	private static void loadCompressedTGA(Texture texture, ReadableByteChannel fTGA) throws IOException
	{
		TGA tga = new TGA();
		readBuffer(fTGA, tga.header);
		// 计算TGA文件的宽度 (highbyte*256+lowbyte)
		texture.width = (unsignedByteToInt(tga.header.get(1)) << 8) + unsignedByteToInt(tga.header
				.get(0));
		// 计算TGA文件的高度 (highbyte*256+lowbyte)
		texture.height = (unsignedByteToInt(tga.header.get(3)) << 8) + unsignedByteToInt(tga.header
				.get(2));
		// 计算BPP
		texture.bpp = unsignedByteToInt(tga.header.get(4));
		// 拷贝Width到本地结构中去
		tga.width = texture.width;
		// 拷贝Height到本地结构中去
		tga.height = texture.height;
		// 拷贝Bpp到本地结构中去
		tga.bpp = texture.bpp;

		// 确认所有的信息都是有效的
		if ((texture.width <= 0) || (texture.height <= 0) || ((texture.bpp != 24) && (texture.bpp != 32))) { throw new IOException("Invalid texture information"); }

		// 如果是24 bit图像
		if (texture.bpp == 24)
			texture.type = GL10.GL_RGB;
		else
			texture.type = GL10.GL_RGBA;

		// 计算BPP
		tga.bytesPerPixel = (tga.bpp / 8);
		// 计算图像大小
		tga.imageSize = (tga.bytesPerPixel * tga.width * tga.height);
		// 申请缓冲区
		texture.imageData = BufferUtil.newByteBuffer(tga.imageSize);
		
		texture.imageData.position(0);
		texture.imageData.limit(texture.imageData.capacity());

		// 计算像数
		int pixelcount = tga.height * tga.width;
		int currentpixel = 0;
		int currentbyte = 0;
		// 申请缓冲区
		ByteBuffer colorbuffer = BufferUtil.newByteBuffer(tga.bytesPerPixel);

		do
		{
			//首先我们声明一个变量来存储“块”头。
			//块头指示接下来的段是RLE还是RAW，它的长度是多少。
			//如果一字节头小于等于127，则它是一个RAW头。
			//头的值是颜色数，是负数，在我们处理其它头字节之前，我们先读取它并且拷贝到内存中。
			//这样我们将我们得到的值加1，然后读取大量像素并且将它们拷贝到ImageData中，就像我们处理未压缩型图像一样。
			//如果头大于127，那么它是下一个像素值随后将要重复的次数。
			//要获取实际重复的数量，我们将它减去127以除去1bit的的头标示符。
			//然后我们读取下一个像素并且依照上述次数连续拷贝它到内存中。
			// 存储Id块值的变量
			int chunkheader;
			try
			{
				// 尝试读取块的头
				ByteBuffer chunkHeaderBuffer = ByteBuffer.allocate(1);
				fTGA.read(chunkHeaderBuffer);
				chunkHeaderBuffer.flip();
				chunkheader = unsignedByteToInt(chunkHeaderBuffer.get());
			}
			catch (IOException e)
			{
				throw new IOException("Could not read RLE header"); // Display
																	// Error
			}
			// 如果是RAW块
			if (chunkheader < 128)
			{
				// 变量值加1以获取RAW像素的总数
				chunkheader++;
				// 读取RAW的颜色值
				for (short counter = 0; counter < chunkheader; counter++)
				{
					readBuffer(fTGA, colorbuffer);
					//下面数据格式将会由BGR翻转为RGB或由BGRA转换为RGBA，
					//具体情况取决于每像素的比特数。当我们完成任务后我们增加当前的字节和当前的像素计数器。
					// 写入内存(R\G\B)
					texture.imageData.put(currentbyte, colorbuffer.get(2));
					texture.imageData.put(currentbyte + 1, colorbuffer.get(1));
					texture.imageData.put(currentbyte + 2, colorbuffer.get(0));
					// 如果是32位图像
					if (tga.bytesPerPixel == 4)
					{
						// 拷贝第四个字节(A)
						texture.imageData.put(currentbyte + 3, colorbuffer.get(3));
					}
					// 依据每像素的字节数增加字节计数器
					currentbyte += tga.bytesPerPixel;
					// 像素计数器加1
					currentpixel++;
					// 如果像素数超界
					if (currentpixel > pixelcount) { throw new IOException("Too many pixels read"); }
				}
			}
			// 如果是RLE头
			else
			{
				// 处理描述RLE段的“块”头。首先我们将chunkheader减去127来得到获取下一个颜色重复的次数。
				// 减去127获得ID Bit的Rid
				chunkheader -= 127;
				// 读取下一个像素（颜色值）
				readBuffer(fTGA, colorbuffer);

				for (short counter = 0; counter < chunkheader; counter++)
				{
					// 拷贝字节数据（R\G\B）
					//下面数据格式将会由BGR翻转为RGB或由BGRA转换为RGBA，
					//具体情况取决于每像素的比特数。当我们完成任务后我们增加当前的字节和当前的像素计数器。
					texture.imageData.put(currentbyte, colorbuffer.get(2));
					texture.imageData.put(currentbyte + 1, colorbuffer.get(1));
					texture.imageData.put(currentbyte + 2, colorbuffer.get(0));

					// 如果是32位图像
					if (tga.bytesPerPixel == 4)
					{
						// 拷贝第四个字节(A)
						texture.imageData.put(currentbyte + 3, colorbuffer.get(3));
					}
					// 增加字节计数器
					currentbyte += tga.bytesPerPixel;
					// 增加字节计数器
					currentpixel++;
					// 判断
					if (currentpixel > pixelcount) { throw new IOException("Too many pixels read"); }
				}
			}
		} while (currentpixel < pixelcount); // 是否有更多的像素要读取？开始循环直到最后
	}

	// 数据类型转换
	private static int unsignedByteToInt(byte b)
	{
		return (int) b & 0xFF;
	}

}
