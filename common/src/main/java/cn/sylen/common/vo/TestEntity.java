package cn.sylen.common.vo;

import java.io.Serializable;
import java.util.Date;

import com.wordnik.swagger.annotations.ApiModelProperty;

import cn.sylen.dao.annotation.SqlField;
import cn.sylen.dao.annotation.SqlPrimaryKey;
import cn.sylen.dao.annotation.SqlTable;

@SqlTable("user")
public class TestEntity extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 3176683481272162828L;

	@ApiModelProperty("ID")
    @SqlPrimaryKey
    private Integer userId;
	
	@ApiModelProperty("用户名")
	@SqlField("user_name")
    private String userName;
	
	@ApiModelProperty("密码")
	@SqlField("password")
    private String password;
	
	@ApiModelProperty("登录次数")
	@SqlField("login_times")
    private Integer loginTimes;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getLoginTimes() {
		return loginTimes;
	}

	public void setLoginTimes(Integer loginTimes) {
		this.loginTimes = loginTimes;
	}
	
	
}
