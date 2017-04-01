/**
 * Copyright (c) 2012-2013, Michael Yang (www.yangfuhai.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xiaomaoqiu.pet.utils.httpCache;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


public class HttpCache {

	IHttpCache m_cache;

	public static HttpCache createMemoryCache( ){
		return new HttpCache();
	}

	public static HttpCache createFileCache(String cachePath ){
		File dir = new File(cachePath);
		File file = new File(dir, "ACache");
		return new HttpCache(file,HttpCache_File.MAX_SIZE,HttpCache_File.MAX_COUNT);
	}

	private HttpCache(){
		m_cache = new HttpCache_Memory();
	}

	private HttpCache(File cacheDir, long max_size, int max_count) {
		if (!cacheDir.exists() && !cacheDir.mkdirs()) {
			throw new RuntimeException("can't make dirs in "
					+ cacheDir.getAbsolutePath());
		}
		m_cache = new HttpCache_File(cacheDir, max_size, max_count);
	}


	public String get(String key) {
		String str = m_cache.get(key);
		if(null==str) return null;
		if(Utils.isDue(str.getBytes()))
			m_cache.remove(key);
		return Utils.clearDateInfo(str);
	}


	public boolean put(String key, String value, int expireTime) {
		String strHasDate = Utils.newStringWithDateInfo(expireTime, value);
		m_cache.put(key,strHasDate);
		return true;
	}


	public boolean remove(String key) {
		return m_cache.remove(key);
	}


	public void clear() {
		m_cache.clear();
	}

	public interface IHttpCache {
		String get(String key);
		boolean put(String key,String value);
		boolean remove(String key);
		void clear();
	}

	public class HttpCache_Memory implements IHttpCache
	{
		Map<String,String>	m_mapCache=new HashMap<String, String>() ;

		@Override
		public String get(String key) {
			return m_mapCache.get(key);
		}

		@Override
		public boolean put(String key, String value) {
			m_mapCache.put(key,value);
			return true;
		}

		@Override
		public boolean remove(String key) {
			return m_mapCache.remove(key)!=null;
		}

		@Override
		public void clear() {
			m_mapCache.clear();
		}

	}

	public class HttpCache_File implements IHttpCache
	{
		private static final int MAX_SIZE = 1000 * 1000 * 50; // 50 mb
		private static final int MAX_COUNT = Integer.MAX_VALUE; // 不限制存放数据的数量

		private final AtomicLong cacheSize;
		private final AtomicInteger cacheCount;
		private final long sizeLimit;
		private final int countLimit;
		private final Map<File, Long> lastUsageDates = Collections
				.synchronizedMap(new HashMap<File, Long>());
		protected File cacheDir;

		private HttpCache_File(File cacheDir, long sizeLimit, int countLimit) {
			this.cacheDir = cacheDir;
			this.sizeLimit = sizeLimit;
			this.countLimit = countLimit;
			cacheSize = new AtomicLong();
			cacheCount = new AtomicInteger();
			calculateCacheSizeAndCacheCount();
		}

		/**
		 * 计算 cacheSize和cacheCount
		 */
		private void calculateCacheSizeAndCacheCount() {
			new Thread(new Runnable() {
				@Override
				public void run() {
					int size = 0;
					int count = 0;
					File[] cachedFiles = cacheDir.listFiles();
					if (cachedFiles != null) {
						for (File cachedFile : cachedFiles) {
							size += calculateSize(cachedFile);
							count += 1;
							lastUsageDates.put(cachedFile,
									cachedFile.lastModified());
						}
						cacheSize.set(size);
						cacheCount.set(count);
					}
				}
			}).start();
		}

		public String get(String key)
		{
			File file = getFile(key);
			if(file == null || !file.exists())
				return null;

			boolean removeFile = false;
			BufferedReader in = null;
			try {
				in = new BufferedReader(new FileReader(file));
				String readString = "";
				String currentLine;
				while ((currentLine = in.readLine()) != null) {
					readString += currentLine;
				}
				return readString;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (removeFile)
					remove(key);
			}
		}

		public boolean put(String key, String value)
		{
			boolean bRet = false;
			File file = newFile(key);
			BufferedWriter out = null;
			try {
				out = new BufferedWriter(new FileWriter(file), 1024);
				out.write(value);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (out != null) {
					try {
						out.flush();
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				updateCache(file);
				bRet = true;
			}
			return bRet;
		}

		private void updateCache(File file) {
			int curCacheCount = cacheCount.get();
			while (curCacheCount + 1 > countLimit) {
				long freedSize = removeNext();
				cacheSize.addAndGet(-freedSize);

				curCacheCount = cacheCount.addAndGet(-1);
			}
			cacheCount.addAndGet(1);

			long valueSize = calculateSize(file);
			long curCacheSize = cacheSize.get();
			while (curCacheSize + valueSize > sizeLimit) {
				long freedSize = removeNext();
				curCacheSize = cacheSize.addAndGet(-freedSize);
			}
			cacheSize.addAndGet(valueSize);

			Long currentTime = System.currentTimeMillis();
			file.setLastModified(currentTime);
			lastUsageDates.put(file, currentTime);
		}

		private File getFile(String key) {
			File file = newFile(key);
			Long currentTime = System.currentTimeMillis();
			file.setLastModified(currentTime);
			lastUsageDates.put(file, currentTime);

			return file;
		}

		private File newFile(String key) {
			return new File(cacheDir, key.hashCode() + "");
		}

		public boolean remove(String key) {
			File image = getFile(key);
			return image.delete();
		}

		public void clear() {
			lastUsageDates.clear();
			cacheSize.set(0);
			File[] files = cacheDir.listFiles();
			if (files != null) {
				for (File f : files) {
					f.delete();
				}
			}
		}

		/**
		 * 移除旧的文件
		 *
		 * @return
		 */
		private long removeNext() {
			if (lastUsageDates.isEmpty()) {
				return 0;
			}

			Long oldestUsage = null;
			File mostLongUsedFile = null;
			Set<Entry<File, Long>> entries = lastUsageDates.entrySet();
			synchronized (lastUsageDates) {
				for (Entry<File, Long> entry : entries) {
					if (mostLongUsedFile == null) {
						mostLongUsedFile = entry.getKey();
						oldestUsage = entry.getValue();
					} else {
						Long lastValueUsage = entry.getValue();
						if (lastValueUsage < oldestUsage) {
							oldestUsage = lastValueUsage;
							mostLongUsedFile = entry.getKey();
						}
					}
				}
			}

			long fileSize = calculateSize(mostLongUsedFile);
			if (mostLongUsedFile.delete()) {
				lastUsageDates.remove(mostLongUsedFile);
			}
			return fileSize;
		}

		private long calculateSize(File file) {
			return file.length();
		}

	}


	/**
	 * 时间计算工具类
	 */
	private static class Utils {

		/**
		 * 判断缓存的String数据是否到期
		 *
		 * @param str
		 * @return true：到期了 false：还没有到期
		 */
		private static boolean isDue(String str) {
			return isDue(str.getBytes());
		}

		/**
		 * 判断缓存的byte数据是否到期
		 *
		 * @param data
		 * @return true：到期了 false：还没有到期
		 */
		private static boolean isDue(byte[] data) {
			String[] strs = getDateInfoFromData(data);
			if (strs != null && strs.length == 2) {
				String saveTimeStr = strs[0];
				while (saveTimeStr.startsWith("0")) {
					saveTimeStr = saveTimeStr
							.substring(1, saveTimeStr.length());
				}
				long saveTime = Long.valueOf(saveTimeStr);
				long deleteAfter = Long.valueOf(strs[1]);
				if (System.currentTimeMillis() > (saveTime + deleteAfter * 1000)) {
					return true;
				}
			}
			return false;
		}

		private static String newStringWithDateInfo(int second, String strInfo) {
			return createDateInfo(second) + strInfo;
		}

		private static String clearDateInfo(String strInfo) {
			if (strInfo != null && hasDateInfo(strInfo.getBytes())) {
				strInfo = strInfo.substring(strInfo.indexOf(mSeparator) + 1,
						strInfo.length());
			}
			return strInfo;
		}

		private static boolean hasDateInfo(byte[] data) {
			return data != null && data.length > 15 && data[13] == '-'
					&& indexOf(data, mSeparator) > 14;
		}

		private static String[] getDateInfoFromData(byte[] data) {
			if (hasDateInfo(data)) {
				String saveDate = new String(copyOfRange(data, 0, 13));
				String deleteAfter = new String(copyOfRange(data, 14,
						indexOf(data, mSeparator)));
				return new String[] { saveDate, deleteAfter };
			}
			return null;
		}

		private static int indexOf(byte[] data, char c) {
			for (int i = 0; i < data.length; i++) {
				if (data[i] == c) {
					return i;
				}
			}
			return -1;
		}

		private static byte[] copyOfRange(byte[] original, int from, int to) {
			int newLength = to - from;
			if (newLength < 0)
				throw new IllegalArgumentException(from + " > " + to);
			byte[] copy = new byte[newLength];
			System.arraycopy(original, from, copy, 0,
					Math.min(original.length - from, newLength));
			return copy;
		}

		private static final char mSeparator = ' ';

		private static String createDateInfo(int second) {
			String currentTime = System.currentTimeMillis() + "";
			while (currentTime.length() < 13) {
				currentTime = "0" + currentTime;
			}
			return currentTime + "-" + second + mSeparator;
		}

	}
}
