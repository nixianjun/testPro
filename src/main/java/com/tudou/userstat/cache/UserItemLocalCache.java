package com.tudou.userstat.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.tudou.userstat.tool.FileMapper;
import com.tudou.userstat.tool.PropertiesAccessor;

/**
 * 用户视频列表的本地缓存,用于缓存部分用户视频列表特别大的用户
 * 
 * @author clin
 * 
 */
public class UserItemLocalCache {
	private static final Logger logger = Logger
			.getLogger(UserItemLocalCache.class);

	public static final String CACHE_USERITEM_FILE = "cache.useritem.file";

	public static final String CACHE_USERITEM_SIZE = "cache.useritem.size";

	private static Map<Integer, List<Integer>> userItems = new ConcurrentHashMap<Integer, List<Integer>>();

	static {
		try {
			// 从本地文件加载数据
			String file = PropertiesAccessor.getProperty(CACHE_USERITEM_FILE);

			if (file == null) {
				logger.error("user item local cache file is empty");
			}

			long startTime = System.currentTimeMillis();

			try {
				// 加载用户视频数据
				byte[] bs = FileMapper.readFile(file);

				ConcurrentHashMap<Integer, List<Integer>> itemMaps = readFromByte(bs);

				if (itemMaps != null) {
					userItems = itemMaps;

					logger.info("load from file " + userItems.size());
				}
			} catch (Exception e) {
				logger.error("", e);
			}

			long endTime = System.currentTimeMillis();

			logger.info("load local cache item finish " + (endTime - startTime));
		} catch (Exception e) {
			logger.info("user item local cache init error", e);
		}
	}

	public static void remove(int uid) {
		userItems.remove(uid);
		save();
	}

	public static void put(int uid, List<Integer> itemIds) {
		userItems.put(uid, itemIds);
		save();
	}

	public static List<Integer> get(int uid) {
		return userItems.get(uid);
	}

	public static Map<Integer, List<Integer>> getAll() {
		return userItems;
	}

	/**
	 * 将useritems的数据转化为byte数组
	 * 
	 * @return
	 */
	public static byte[] toByteArray() {
		Map<Integer, List<Integer>> tmp = userItems;
		Iterator<Entry<Integer, List<Integer>>> it = tmp.entrySet().iterator();

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		int count = tmp.size();

		writeInt(out, count);

		while (it.hasNext()) {
			Entry<Integer, List<Integer>> e = it.next();
			writeInt(out, e.getKey());

			List<Integer> list = e.getValue();
			int size = 0;

			if (list != null) {
				size = list.size();
			}

			writeInt(out, size);

			for (Integer itemId : list) {
				writeInt(out, itemId);
			}
		}

		return out.toByteArray();
	}

	public static ConcurrentHashMap<Integer, List<Integer>> readFromByte(
			byte[] b) {
		if (b == null || b.length == 0) {
			return null;
		}

		ConcurrentHashMap<Integer, List<Integer>> result = new ConcurrentHashMap<Integer, List<Integer>>();

		ByteArrayInputStream in = new ByteArrayInputStream(b);

		int count = readInt(in);

		for (int i = 0; i < count; i++) {
			int uid = readInt(in);

			int size = readInt(in);

			ArrayList<Integer> itemIds = new ArrayList<Integer>();

			for (int j = 0; j < size; j++) {
				int itemId = readInt(in);
				itemIds.add(itemId);
			}

			result.put(uid, itemIds);
		}

		return result;
	}

	public static void main(String[] args) {
		List<Integer> list = new ArrayList<Integer>();
		list.add(1);
		list.add(2);

		userItems.put(1, list);

		list = new ArrayList<Integer>();
		list.add(1);
		list.add(2);

		userItems.put(3, list);

		byte[] b = toByteArray();
		System.out.println(b.length);

		ConcurrentHashMap<Integer, List<Integer>> result = readFromByte(b);
		System.out.println(result);
	}

	public static int readInt(ByteArrayInputStream in) {
		int b1 = in.read();
		int b2 = in.read();
		int b3 = in.read();
		int b4 = in.read();

		return (int) (b1 << 24 | b2 << 16 | b3 << 8 | b4);
	}

	public static void writeInt(ByteArrayOutputStream out, int i) {
		out.write((byte) (i >> 24));
		out.write((byte) (i >> 16));
		out.write((byte) (i >> 8));
		out.write((byte) (i));
	}

	public static synchronized void save() {
		try {
			// 从本地文件加载数据
			long step1 = System.currentTimeMillis();

			byte[] bs = toByteArray();

			long step2 = System.currentTimeMillis();

			String file = PropertiesAccessor.getProperty(CACHE_USERITEM_FILE);

			if (file == null) {
				logger.error("user item local cache file is empty");
			}

			long step3 = 0;

			try {
				FileMapper.writeFile(file, bs);
				step3 = System.currentTimeMillis();
			} catch (Exception e) {
				logger.error("", e);
			}

			long step4 = System.currentTimeMillis();

			logger.info("save -- local cache item finish " + (step2 - step1)
					+ " " + (step3 - step2) + " " + (step4 - step3) + " total "
					+ (step4 - step1));
		} catch (Exception e) {
			logger.error("save error item local cache", e);
		}
	}
}
