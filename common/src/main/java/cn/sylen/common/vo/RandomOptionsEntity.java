package cn.sylen.common.vo;

import java.io.Serializable;

import com.wordnik.swagger.annotations.ApiModelProperty;

import cn.sylen.dao.annotation.SqlField;
import cn.sylen.dao.annotation.SqlPrimaryKey;
import cn.sylen.dao.annotation.SqlTable;

/**
 * 小程序随机选项实体类
 */
@SqlTable("random_options")
public class RandomOptionsEntity extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 5419797039438570886L;

	@ApiModelProperty("ID")
    @SqlPrimaryKey
    private Integer optionsId;
	
	@ApiModelProperty("分支ID")
	@SqlField("branches_id")
    private Integer branchesId;
	
	@ApiModelProperty("是否结束")
    @SqlField("is_end")
    private Boolean isEnd;
	
	@ApiModelProperty("选项描述")
    @SqlField("description")
    private String description;
	
	@ApiModelProperty("选择后描述")
    @SqlField("result")
    private String result;

	public Integer getOptionsId() {
		return optionsId;
	}

	public void setOptionsId(Integer optionsId) {
		this.optionsId = optionsId;
	}

	public Integer getBranchesId() {
		return branchesId;
	}

	public void setBranchesId(Integer branchesId) {
		this.branchesId = branchesId;
	}

	public Boolean getIsEnd() {
		return isEnd;
	}

	public void setIsEnd(Boolean isEnd) {
		this.isEnd = isEnd;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	
}
