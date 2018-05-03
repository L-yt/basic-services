package cn.sylen.common.vo;

import java.io.Serializable;
import java.util.Date;

import com.wordnik.swagger.annotations.ApiModelProperty;

import cn.sylen.common.util.DateTimeUtil;
import cn.sylen.dao.annotation.SqlField;
import cn.sylen.dao.annotation.SqlPrimaryKey;
import cn.sylen.dao.annotation.SqlTable;

/**
 * 小程序用户基本信息实体类
 */
@SqlTable("applets_user")
public class AppletsUserEntity extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 6941371489775397156L;

	@ApiModelProperty("ID")
    @SqlPrimaryKey
    private Integer userId;
	
	@ApiModelProperty("头像地址")
    @SqlField("avatar_url")
    private String avatarUrl;
	
	@ApiModelProperty("昵称")
    @SqlField("nick_name")
    private String nickName;
	
	@ApiModelProperty("语言类型")
    @SqlField("language")
    private String language;
	
	@ApiModelProperty("所在城市")
    @SqlField("city")
    private String city;
	
	@ApiModelProperty("所在国家")
    @SqlField("country")
    private String country;
	
	@ApiModelProperty("所在省")
    @SqlField("province")
    private String province;
	
	@ApiModelProperty("性别（男：1，女：2）")
    @SqlField("gender")
    private Integer gender;
	
	@ApiModelProperty("登录次数")
    @SqlField("login_times")
    private Integer loginTimes;
	
	@ApiModelProperty("最后登录时间")
    @SqlField("last_login_time")
    private Date lastLoginTime;
	
	@ApiModelProperty("最后登录时间yyyy-MM-dd HH:mm:ss")
    private String lastLoginTimeStr;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public Integer getLoginTimes() {
		return loginTimes;
	}

	public void setLoginTimes(Integer loginTimes) {
		this.loginTimes = loginTimes;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getLastLoginTimeStr() {
		if (this.lastLoginTime != null) {
			return DateTimeUtil.formatDate(this.lastLoginTime);
		}
		return null;
	}

	public AppletsUserEntity() {
		super();
	}
	
}
