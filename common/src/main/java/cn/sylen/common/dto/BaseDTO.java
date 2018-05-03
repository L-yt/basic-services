package cn.sylen.common.dto;

import java.util.Date;

import com.wordnik.swagger.annotations.ApiModelProperty;

import cn.sylen.common.enums.DeleteEnum;
import cn.sylen.common.util.DateTimeUtil;

public class BaseDTO {

	@ApiModelProperty("创建时间 ")
    private Date dateCreate;

	@ApiModelProperty("创建者id")
    private Integer creatorId;
	
	@ApiModelProperty("创建者")
    private String creator;
	
	@ApiModelProperty("更新时间")
    private Date dateUpdate;

	@ApiModelProperty("更新者id")
    private Integer updatorId;
	
	@ApiModelProperty("更新者")
    private String updator;

	@ApiModelProperty("删除时间")
    private Date dateDelete;
	
	@ApiModelProperty("逻辑删除（启用中/已停用）")
    private String deleted;
    
	@ApiModelProperty("创建时间 yyyy-MM-dd HH:mm:ss")
	private String dateCreateStr;

	@ApiModelProperty("更新时间 yyyy-MM-dd HH:mm:ss")
	private String dateUpdateStr;
	
	@ApiModelProperty("删除时间 yyyy-MM-dd HH:mm:ss")
	private String dateDeleteStr;

	public Date getDateCreate() {
		return dateCreate;
	}

	public void setDateCreate(Date dateCreate) {
		this.dateCreate = dateCreate;
	}

	public Integer getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Integer creatorId) {
		this.creatorId = creatorId;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Date getDateUpdate() {
		return dateUpdate;
	}

	public void setDateUpdate(Date dateUpdate) {
		this.dateUpdate = dateUpdate;
	}

	public Integer getUpdatorId() {
		return updatorId;
	}

	public void setUpdatorId(Integer updatorId) {
		this.updatorId = updatorId;
	}

	public String getUpdator() {
		return updator;
	}

	public void setUpdator(String updator) {
		this.updator = updator;
	}

	public Date getDateDelete() {
		return dateDelete;
	}

	public void setDateDelete(Date dateDelete) {
		this.dateDelete = dateDelete;
	}

	public String getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean isDeleted) {
		this.deleted = DeleteEnum.findDescByCode(isDeleted);
	}

	public String getDateCreateStr() {
		if (this.dateCreate != null) {
			return DateTimeUtil.formatDate(this.dateCreate);
		}
		return null;
	}

	public String getDateUpdateStr() {
		if (this.dateUpdate != null) {
			return DateTimeUtil.formatDate(this.dateUpdate);
		}
		return null;
	}

	public String getDateDeleteStr() {
		if (this.dateDelete != null) {
			return DateTimeUtil.formatDate(this.dateDelete);
		}
		return null;
	}

	
}
