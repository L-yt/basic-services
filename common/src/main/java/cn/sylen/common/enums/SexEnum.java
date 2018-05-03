package cn.sylen.common.enums;

/**
 * 性别枚举类
 * 
 * @author Sylen 2018-4-9
 */
public enum SexEnum {
	MALE(1, "男"),
	FEMALE(2, "女");

	private int code;
	private String desc;

	SexEnum(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public int getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public static SexEnum findByCode(Integer code) {
		for (SexEnum sexEnum : SexEnum.values()) {
			if (sexEnum.getCode() == code) {
				return sexEnum;
			}
		}
		return null;
	}

	public static String findDescByCode(Integer code) {
		for (SexEnum sexEnum : SexEnum.values()) {
			if (sexEnum.getCode() == code) {
				return sexEnum.getDesc();
			}
		}
		return "未知";
	}

}
