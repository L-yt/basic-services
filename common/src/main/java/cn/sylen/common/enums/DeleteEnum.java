package cn.sylen.common.enums;

/**
 * 删除枚举类
 * 
 * @author Sylen 2018-4-11
 */
public enum DeleteEnum {
	ENABLE(false, "启用中"),
	DISABLE(true, "已停用");

	private Boolean code;
	private String desc;

	DeleteEnum(Boolean code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public Boolean getCode() {
		return code;
	}

	public void setCode(Boolean code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public static String getDescByCode(Boolean code) {
		for (DeleteEnum deleteEnum : DeleteEnum.values()) {
			if (deleteEnum.getCode() == code) {
				return deleteEnum.getDesc();
			}
		}
		return "";
	}

	public static DeleteEnum findByCode(Boolean code) {
		for (DeleteEnum deleteEnum : DeleteEnum.values()) {
			if (deleteEnum.getCode() == code) {
				return deleteEnum;
			}
		}
		return null;
	}

	public static String findDescByCode(Boolean code) {
		for (DeleteEnum deleteEnum : DeleteEnum.values()) {
			if (deleteEnum.getCode() == code) {
				return deleteEnum.getDesc();
			}
		}
		return "";
	}
}
