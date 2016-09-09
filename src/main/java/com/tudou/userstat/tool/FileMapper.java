package com.tudou.userstat.tool;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.tudou.userstat.cache.UserItemLocalCache;

public class FileMapper {
	private static final Logger logger = Logger.getLogger(FileMapper.class);

	private RandomAccessFile rf;
	private MappedByteBuffer buffer;
	private FileChannel channel;

	public FileMapper(String fileName, long size) throws Exception {
		File file = new File(fileName);

		rf = new RandomAccessFile(file, "rw");
		channel = rf.getChannel();
		buffer = channel.map(MapMode.READ_WRITE, 0, size);
	}

	/**
	 * 写文件
	 * 
	 * @param file
	 * @param b
	 */
	public static void writeFile(String file, byte[] b) {
		FileOutputStream out = null;

		try {
			out = new FileOutputStream(file);
			out.write(b);
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			try {
				out.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 读取文件
	 * 
	 * @param file
	 * @return
	 */
	public static byte[] readFile(String file) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		FileInputStream in = null;

		try {
			// 设置缓存为10m
			byte[] tmp = new byte[10485760];
			in = new FileInputStream(file);

			int readLen = 0;

			while ((readLen = in.read(tmp)) > 0) {
				// 将读取到的数据写入bytearray流
				if (readLen < tmp.length) {
					out.write(tmp, 0, readLen);
				} else {
					out.write(tmp);
				}
			}

		} catch (Exception e) {
			logger.error("", e);
		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
		}

		return out.toByteArray();
	}

	public void writeWithBytes(byte[] bytes) {
		buffer.position(0);
		buffer.putInt(bytes.length);
		buffer.put(bytes);
		buffer.force();
	}

	public void writeWithObject(Object obj) {
		byte[] bs = JSON.toJSONBytes(obj);
		buffer.position(0);
		buffer.putInt(bs.length);
		buffer.put(bs);
		buffer.flip();
	}

	public int getSize() {
		int position = buffer.position();

		buffer.position(0);
		int size = buffer.getInt();
		buffer.position(position);

		return size;
	}

	public byte[] readBytes() {
		int size = buffer.getInt();

		byte[] bs = new byte[size];

		for (int i = 0; i < size; i++) {
			bs[i] = buffer.get();
		}

		return bs;
	}

	public <T> T readObject(TypeReference<T> t) {
		byte[] bs = readBytes();

		try {
			return JSON.parseObject(new String(bs, "utf-8"), t);
		} catch (Exception e) {
			logger.error("", e);
		}

		return null;
	}

	public void close() {
		if (this.buffer != null) {
			this.buffer.force();
		}

		if (channel != null) {
			try {
				channel.close();
			} catch (IOException e) {

			}
		}

		if (this.rf != null) {
			try {
				this.rf.close();
			} catch (IOException e) {
			}
		}

		this.buffer = null;
		this.channel = null;
		this.rf = null;
	}

	public static void main(String[] args) throws Throwable {
		List<Integer> value = new ArrayList<Integer>();
		value.add(1);
		value.add(2);

		UserItemLocalCache.put(45, value);

		value = new ArrayList<Integer>();
		value.add(1);
		value.add(2);

		UserItemLocalCache.put(123123123, value);

		value = new ArrayList<Integer>();
		value.add(1);
		value.add(2);

		UserItemLocalCache.put(334123223, value);

		FileMapper fm = new FileMapper("d:\\test.txt", 10000);
		fm.writeWithObject(UserItemLocalCache.getAll());
		fm.close();

		fm = new FileMapper("d:\\test.txt", 10000);
		ConcurrentHashMap<Integer, List<Integer>> result = fm
				.readObject(new TypeReference<ConcurrentHashMap<Integer, List<Integer>>>() {
				});

		System.out.println(result);
	}
}
