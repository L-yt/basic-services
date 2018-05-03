package cn.sylen.common.dto;

import java.util.List;

import com.wordnik.swagger.annotations.ApiModelProperty;

import cn.sylen.common.vo.RandomOptionsEntity;

/**
 * 分支信息
 * @author Sylen
 * 2018-4-18
 */
public class BranchesDTO extends BaseDTO {

	@ApiModelProperty("分支ID")
    private Integer branchesId;
	
	@ApiModelProperty("级别")
    private Integer step;
	
	@ApiModelProperty("分支描述")
    private String description;
	
	@ApiModelProperty("选项")
    private List<RandomOptionsEntity> options;

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

	public List<RandomOptionsEntity> getOptions() {
		return options;
	}

	public void setOptions(List<RandomOptionsEntity> options) {
		this.options = options;
	}
	
	
}
