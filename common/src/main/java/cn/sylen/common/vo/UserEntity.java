package cn.sylen.common.vo;

import java.io.Serializable;
import java.util.Date;

import com.wordnik.swagger.annotations.ApiModelProperty;
import cn.sylen.dao.annotation.SqlPrimaryKey;
import cn.sylen.common.util.DateTimeUtil;
import cn.sylen.dao.annotation.SqlField;
import cn.sylen.dao.annotation.SqlTable;

/**
 * 用户基本信息实体类
 */
@SqlTable("user")
public class UserEntity extends BaseEntity implements Serializable {
	private static final long serialVersionUID = -7384611446807862502L;

	@ApiModelProperty("ID")
    @SqlPrimaryKey
    private Integer userId;
	
	@ApiModelProperty("账号")
    @SqlField("account")
    private String account;
	
	@ApiModelProperty("手机号码")
    @SqlField("phone")
    private String phone;
	
	@ApiModelProperty("密码")
    @SqlField("password")
    private String password;
	
	@ApiModelProperty("真实姓名")
    @SqlField("nickname")
    private String nickname;
	
	@ApiModelProperty("性别")
    @SqlField("sex")
    private Integer sex;
	
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

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
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

	public UserEntity() {
		super();
	}
	
	
}
