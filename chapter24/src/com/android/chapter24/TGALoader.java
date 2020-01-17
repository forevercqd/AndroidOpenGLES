package com.android.chapter24;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javax.microedition.khronos.opengles.GL10;

public class TGALoader
{
	// δѹ����TGAͷ
	private static final ByteBuffer	uTGAcompare;
	// ѹ����TGAͷ
	private static final ByteBuffer	cTGAcompare;

	static
	{
		//������Ҫ����һ���ļ�ͷ�����������ܹ����߳���ʲô���͵��ļ�ͷ������Ч��ͼ���ϡ�
		//�����δѹ����TGAͼ��ǰ12�ֽڽ�����0 0 2 0 0 0 0 0 0 0 0 0��
		//�����RLEѹ���ģ�����0 0 10 0 0 0 0 0 0 0 0 0��������ֵ�������Ǽ�����ڶ�ȡ���ļ��Ƿ���Ч��
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

	// װ��һ��TGA�ļ�
	public static void loadTGA(Texture texture, InputStream file) throws IOException
	{
		ByteBuffer header = BufferUtil.newByteBuffer(12);
		ReadableByteChannel in = Channels.newChannel(file);
		readBuffer(in, header);

		// ����ļ�ͷ����δѹ�����ļ�ͷ��ʽ
		if (uTGAcompare.equals(header))
		{
			// ��ȡδѹ����TGA�ļ�
			loadUncompressedTGA(texture, in);
		}
		// ����ļ�ͷ����ѹ�����ļ�ͷ��ʽ
		else if (cTGAcompare.equals(header))
		{
			// ��ȡѹ����TGA��ʽ
			loadCompressedTGA(texture, in);
		}
		else
		{
			// ����
			in.close();
			throw new IOException("TGA file be type 2 or type 10 ");

		}
	}

	// ��ȡһ�����ݰ�����������
	private static void readBuffer(ReadableByteChannel in, ByteBuffer buffer) throws IOException
	{
		while (buffer.hasRemaining())
		{
			in.read(buffer);
		}
		buffer.flip();
	}

	// ��ȡδѹ����TGA�ļ�
	private static void loadUncompressedTGA(Texture texture, ReadableByteChannel in) throws IOException
	{
		TGA tga = new TGA();
		// ��ȡ�ļ�ͷ
		readBuffer(in, tga.header);
		// ����TGA�ļ��Ŀ�� (highbyte*256+lowbyte)
		texture.width = (unsignedByteToInt(tga.header.get(1)) << 8) + unsignedByteToInt(tga.header
				.get(0));
		// ����TGA�ļ��ĸ߶� (highbyte*256+lowbyte)
		texture.height = (unsignedByteToInt(tga.header.get(3)) << 8) + unsignedByteToInt(tga.header
				.get(2));
		// ����BPP
		texture.bpp = unsignedByteToInt(tga.header.get(4));
		// ����Width�����ؽṹ��ȥ
		tga.width = texture.width;
		// ����Height�����ؽṹ��ȥ
		tga.height = texture.height;
		// ����Bpp�����ؽṹ��ȥ
		tga.bpp = texture.bpp;

		// ȷ�����е���Ϣ������Ч��
		// ������Ҫȷ�ϸ߶ȺͿ������Ϊ1�����أ�����bpp��24��32��
		if ((texture.width <= 0) || (texture.height <= 0) || ((texture.bpp != 24) && (texture.bpp != 32))) { throw new IOException("Invalid texture information"); }
		// �����24 bitͼ��
		if (texture.bpp == 24)
			texture.type = GL10.GL_RGB; // ��������ΪRGB
		else
			texture.type = GL10.GL_RGBA;// ��������ΪRGBA

		// ����BPP
		tga.bytesPerPixel = (tga.bpp / 8);
		// ����洢ͼ��������ڴ�(����������С)
		tga.imageSize = (tga.bytesPerPixel * tga.width * tga.height);
		// �����ڴ棨����ָ����С�Ļ�������
		texture.imageData = BufferUtil.newByteBuffer(tga.imageSize);

		// �Զ�ȡ����ͼ������
		readBuffer(in, texture.imageData);

		// ͨ��ѭ��ȡ��ͼ������
		//TGA�ļ�����OpenGL����˳��ķ�ʽ�洢ͼ��������Ǳ��뽫��ʽ��BGR��RGB��
		//Ϊ�˴ﵽ��һ�㣬���ǽ���ÿ�����صĵ�һ���͵������ֽڵ����ݡ�
		for (int cswap = 0; cswap < tga.imageSize; cswap += tga.bytesPerPixel)
		{
			byte temp = texture.imageData.get(cswap);
			texture.imageData.put(cswap, texture.imageData.get(cswap + 2));
			texture.imageData.put(cswap + 2, temp);
		}
	}

	// ��ȡѹ����TGA�ļ�
	private static void loadCompressedTGA(Texture texture, ReadableByteChannel fTGA) throws IOException
	{
		TGA tga = new TGA();
		readBuffer(fTGA, tga.header);
		// ����TGA�ļ��Ŀ�� (highbyte*256+lowbyte)
		texture.width = (unsignedByteToInt(tga.header.get(1)) << 8) + unsignedByteToInt(tga.header
				.get(0));
		// ����TGA�ļ��ĸ߶� (highbyte*256+lowbyte)
		texture.height = (unsignedByteToInt(tga.header.get(3)) << 8) + unsignedByteToInt(tga.header
				.get(2));
		// ����BPP
		texture.bpp = unsignedByteToInt(tga.header.get(4));
		// ����Width�����ؽṹ��ȥ
		tga.width = texture.width;
		// ����Height�����ؽṹ��ȥ
		tga.height = texture.height;
		// ����Bpp�����ؽṹ��ȥ
		tga.bpp = texture.bpp;

		// ȷ�����е���Ϣ������Ч��
		if ((texture.width <= 0) || (texture.height <= 0) || ((texture.bpp != 24) && (texture.bpp != 32))) { throw new IOException("Invalid texture information"); }

		// �����24 bitͼ��
		if (texture.bpp == 24)
			texture.type = GL10.GL_RGB;
		else
			texture.type = GL10.GL_RGBA;

		// ����BPP
		tga.bytesPerPixel = (tga.bpp / 8);
		// ����ͼ���С
		tga.imageSize = (tga.bytesPerPixel * tga.width * tga.height);
		// ���뻺����
		texture.imageData = BufferUtil.newByteBuffer(tga.imageSize);
		
		texture.imageData.position(0);
		texture.imageData.limit(texture.imageData.capacity());

		// ��������
		int pixelcount = tga.height * tga.width;
		int currentpixel = 0;
		int currentbyte = 0;
		// ���뻺����
		ByteBuffer colorbuffer = BufferUtil.newByteBuffer(tga.bytesPerPixel);

		do
		{
			//������������һ���������洢���顱ͷ��
			//��ͷָʾ�������Ķ���RLE����RAW�����ĳ����Ƕ��١�
			//���һ�ֽ�ͷС�ڵ���127��������һ��RAWͷ��
			//ͷ��ֵ����ɫ�����Ǹ����������Ǵ�������ͷ�ֽ�֮ǰ�������ȶ�ȡ�����ҿ������ڴ��С�
			//�������ǽ����ǵõ���ֵ��1��Ȼ���ȡ�������ز��ҽ����ǿ�����ImageData�У��������Ǵ���δѹ����ͼ��һ����
			//���ͷ����127����ô������һ������ֵ���Ҫ�ظ��Ĵ�����
			//Ҫ��ȡʵ���ظ������������ǽ�����ȥ127�Գ�ȥ1bit�ĵ�ͷ��ʾ����
			//Ȼ�����Ƕ�ȡ��һ�����ز����������������������������ڴ��С�
			// �洢Id��ֵ�ı���
			int chunkheader;
			try
			{
				// ���Զ�ȡ���ͷ
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
			// �����RAW��
			if (chunkheader < 128)
			{
				// ����ֵ��1�Ի�ȡRAW���ص�����
				chunkheader++;
				// ��ȡRAW����ɫֵ
				for (short counter = 0; counter < chunkheader; counter++)
				{
					readBuffer(fTGA, colorbuffer);
					//�������ݸ�ʽ������BGR��תΪRGB����BGRAת��ΪRGBA��
					//�������ȡ����ÿ���صı����������������������������ӵ�ǰ���ֽں͵�ǰ�����ؼ�������
					// д���ڴ�(R\G\B)
					texture.imageData.put(currentbyte, colorbuffer.get(2));
					texture.imageData.put(currentbyte + 1, colorbuffer.get(1));
					texture.imageData.put(currentbyte + 2, colorbuffer.get(0));
					// �����32λͼ��
					if (tga.bytesPerPixel == 4)
					{
						// �������ĸ��ֽ�(A)
						texture.imageData.put(currentbyte + 3, colorbuffer.get(3));
					}
					// ����ÿ���ص��ֽ��������ֽڼ�����
					currentbyte += tga.bytesPerPixel;
					// ���ؼ�������1
					currentpixel++;
					// �������������
					if (currentpixel > pixelcount) { throw new IOException("Too many pixels read"); }
				}
			}
			// �����RLEͷ
			else
			{
				// ��������RLE�εġ��顱ͷ���������ǽ�chunkheader��ȥ127���õ���ȡ��һ����ɫ�ظ��Ĵ�����
				// ��ȥ127���ID Bit��Rid
				chunkheader -= 127;
				// ��ȡ��һ�����أ���ɫֵ��
				readBuffer(fTGA, colorbuffer);

				for (short counter = 0; counter < chunkheader; counter++)
				{
					// �����ֽ����ݣ�R\G\B��
					//�������ݸ�ʽ������BGR��תΪRGB����BGRAת��ΪRGBA��
					//�������ȡ����ÿ���صı����������������������������ӵ�ǰ���ֽں͵�ǰ�����ؼ�������
					texture.imageData.put(currentbyte, colorbuffer.get(2));
					texture.imageData.put(currentbyte + 1, colorbuffer.get(1));
					texture.imageData.put(currentbyte + 2, colorbuffer.get(0));

					// �����32λͼ��
					if (tga.bytesPerPixel == 4)
					{
						// �������ĸ��ֽ�(A)
						texture.imageData.put(currentbyte + 3, colorbuffer.get(3));
					}
					// �����ֽڼ�����
					currentbyte += tga.bytesPerPixel;
					// �����ֽڼ�����
					currentpixel++;
					// �ж�
					if (currentpixel > pixelcount) { throw new IOException("Too many pixels read"); }
				}
			}
		} while (currentpixel < pixelcount); // �Ƿ��и��������Ҫ��ȡ����ʼѭ��ֱ�����
	}

	// ��������ת��
	private static int unsignedByteToInt(byte b)
	{
		return (int) b & 0xFF;
	}

}
