package cn.sylen.common.vo;

import java.io.Serializable;

import com.wordnik.swagger.annotations.ApiModelProperty;

import cn.sylen.dao.annotation.SqlField;
import cn.sylen.dao.annotation.SqlPrimaryKey;
import cn.sylen.dao.annotation.SqlTable;

/**
 * 小程序随机分支实体类
 */
@SqlTable("random_branches")
public class RandomBranchesEntity extends BaseEntity implements Serializable {
	private static final long serialVersionUID = -5933002739229561528L;

	@ApiModelProperty("ID")
    @SqlPrimaryKey
    private Integer branchesId;
	
	@ApiModelProperty("级别")
    @SqlField("step")
    private Integer step;
	
	@ApiModelProperty("描述")
    @SqlField("description")
    private String description;


	public Integer getBranchesId() {
		return branchesId;
	}

	public void setBranchesId(Integer branchesId) {
		this.branchesId = branchesId;
	}

	public Integer getStep() {
		return step;
	}

	public void setStep(Integer step) {
		this.step = step;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
