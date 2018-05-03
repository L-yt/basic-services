package cn.sylen.common.dto;

import java.util.Date;

import com.wordnik.swagger.annotations.ApiModelProperty;

import cn.sylen.common.util.DateTimeUtil;


/**
 * 用户信息
 * @author Sylen
 * 2018-4-11
 */
public class UserDTO extends BaseDTO {

	@ApiModelProperty("用户ID")
    private Integer userId;
	
	@ApiModelProperty("账号")
    private String account;
	
	@ApiModelProperty("手机号码")
    private String phone;
	
	@ApiModelProperty("密码")
    private String password;
	
	@ApiModelProperty("真实姓名")
    private String nickname;
	
	@ApiModelProperty("性别")
    private String sex;
	
	@ApiModelProperty("登录次数")
    private Integer loginTimes;
	
	@ApiModelProperty("最后登录时间")
    private Date lastLoginTime;
	
	@ApiModelProperty("最后登录时间yyyy-MM-dd HH:mm:ss")
    private String lastLoginTimeStr;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
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

}
