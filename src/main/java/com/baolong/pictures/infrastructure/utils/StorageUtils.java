package com.baolong.pictures.infrastructure.utils;

/**
 * 存储大小工具类
 *
 * @author Baolong 2025年03月09 14:35
 * @version 1.0
 * @since 1.8
 */
public class StorageUtils {
	// 定义常量
	private static final long BYTES_IN_KB = 1024;
	private static final long BYTES_IN_MB = BYTES_IN_KB * 1024;
	private static final long BYTES_IN_GB = BYTES_IN_MB * 1024;
	private static final long BYTES_IN_TB = BYTES_IN_GB * 1024;

	/**
	 * 自动格式化存储大小为合适的单位（如TB、GB、MB、KB、Bytes），并保留一位小数。
	 *
	 * @param bytes 字节数
	 * @return 格式化的字符串，例如 "1.0 MB" 或 "1023 Bytes"
	 */
	public static String format_1f(long bytes) {
		if (bytes < 0) {
			throw new IllegalArgumentException("Bytes cannot be negative.");
		}

		String[] units = {"TB", "GB", "MB", "KB", "Bytes"};
		long[] factors = {BYTES_IN_TB, BYTES_IN_GB, BYTES_IN_MB, BYTES_IN_KB, 1};

		for (int i = 0; i < units.length; i++) {
			long factor = factors[i];
			if (bytes >= factor) {
				double value = (double) bytes / factor;
				if (units[i].equals("Bytes")) {
					return String.format("%d Bytes", bytes);
				} else {
					return String.format("%.1f %s", value, units[i]);
				}
			}
		}

		return "0"; // 处理bytes为0的情况
	}

	/**
	 * 自动格式化存储大小为合适的单位（如TB、GB、MB、KB、Bytes），并保留有效一位小数。
	 *
	 * @param bytes 字节数
	 * @return 格式化的字符串，例如 "1.0 MB" 或 "1023 Bytes"
	 */
	public static String format(long bytes) {
		if (bytes < 0) {
			throw new IllegalArgumentException("Bytes cannot be negative.");
		}

		String[] units = {"TB", "GB", "MB", "KB", "Bytes"};
		long[] factors = {BYTES_IN_TB, BYTES_IN_GB, BYTES_IN_MB, BYTES_IN_KB, 1};

		for (int i = 0; i < units.length; i++) {
			long factor = factors[i];
			if (bytes >= factor) {
				double value = (double) bytes / factor;
				if (units[i].equals("Bytes")) {
					return String.format("%d Bytes", bytes);
				} else {
					// 判断是否为整数，若整数则去掉小数位
					if (value == (long) value) {
						return String.format("%d %s", (long) value, units[i]);
					} else {
						return String.format("%.1f %s", value, units[i]);
					}
				}
			}
		}

		return "0";
	}

	/**
	 * 将字节转换为千字节 (KB)
	 *
	 * @param bytes 字节数
	 * @return 千字节数
	 */
	public static double bytesToKB(long bytes) {
		return (double) bytes / BYTES_IN_KB;
	}

	/**
	 * 将字节转换为兆字节 (MB)
	 *
	 * @param bytes 字节数
	 * @return 兆字节数
	 */
	public static double bytesToMB(long bytes) {
		return (double) bytes / BYTES_IN_MB;
	}

	/**
	 * 将字节转换为吉字节 (GB)
	 *
	 * @param bytes 字节数
	 * @return 吉字节数
	 */
	public static double bytesToGB(long bytes) {
		return (double) bytes / BYTES_IN_GB;
	}

	/**
	 * 将字节转换为太字节 (TB)
	 *
	 * @param bytes 字节数
	 * @return 太字节数
	 */
	public static double bytesToTB(long bytes) {
		return (double) bytes / BYTES_IN_TB;
	}

	/**
	 * 将千字节 (KB) 转换为字节
	 *
	 * @param kb 千字节数
	 * @return 字节数
	 */
	public static long kbToBytes(double kb) {
		return (long) (kb * BYTES_IN_KB);
	}

	/**
	 * 将兆字节 (MB) 转换为字节
	 *
	 * @param mb 兆字节数
	 * @return 字节数
	 */
	public static long mbToBytes(double mb) {
		return (long) (mb * BYTES_IN_MB);
	}

	/**
	 * 将吉字节 (GB) 转换为字节
	 *
	 * @param gb 吉字节数
	 * @return 字节数
	 */
	public static long gbToBytes(double gb) {
		return (long) (gb * BYTES_IN_GB);
	}

	/**
	 * 将太字节 (TB) 转换为字节
	 *
	 * @param tb 太字节数
	 * @return 字节数
	 */
	public static long tbToBytes(double tb) {
		return (long) (tb * BYTES_IN_TB);
	}

	public static void main(String[] args) {
		long bytes = 1048576; // 1MB

		System.out.println(bytes + " bytes = " + bytesToKB(bytes) + " KB"); // 1024.0 KB
		System.out.println(bytes + " bytes = " + bytesToMB(bytes) + " MB"); // 1.0 MB
		System.out.println(bytes + " bytes = " + bytesToGB(bytes) + " GB"); // 9.765625E-4 GB
		System.out.println(bytes + " bytes = " + bytesToTB(bytes) + " TB"); // 9.5367431640625E-7 TB

		double mb = 1.0;
		System.out.println(mb + " MB = " + mbToBytes(mb) + " bytes"); // 1048576 bytes

		System.out.println("格式化结果: " + format_1f(bytes)); // 1.0 MB
		System.out.println("格式化结果: " + format_1f(1500));  // 1.5 KB
		System.out.println("格式化结果: " + format_1f(500));   // 500 Bytes
		System.out.println("格式化结果: " + format_1f(1073741824L)); // 1.0 GB

		System.out.println("格式化结果: " + format(bytes)); // 1.0 MB
		System.out.println("格式化结果: " + format(1500));  // 1.5 KB
		System.out.println("格式化结果: " + format(500));   // 500 Bytes
		System.out.println("格式化结果: " + format(1073741824L)); // 1.0 GB
	}
}
