package cn.sylen.common.util;

import java.util.UUID;

public class UUIDUtil {
	/**
	 * 生成10位UUID
	 *
	 * @return
	 */
	public static String getID() {
		UUID uuid = UUID.randomUUID();

		// 改变uuid的生成规则
		return HashUtil.convertToHashStr(uuid.getMostSignificantBits(), 5)
				+ HashUtil.convertToHashStr(uuid.getLeastSignificantBits(), 5);
	}

	/**
	 * 转换目前32位UUID为10位UUID
	 *
	 * @param uuidStr
	 * @return
	 */
	public static String convertID(String uuidStr) {
		UUID uuid = UUID.fromString(uuidStr);
		// 改变uuid的生成规则
		return HashUtil.convertToHashStr(uuid.getMostSignificantBits(), 5)
				+ HashUtil.convertToHashStr(uuid.getLeastSignificantBits(), 5);
	}

	public static void main(String[] args) {
		System.out.println(convertID("d64cfa7e-8d19-4054-82b4-9e5707f55633"));
	}
}
