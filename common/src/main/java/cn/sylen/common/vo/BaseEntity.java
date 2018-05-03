package cn.sylen.common.vo;

import com.wordnik.swagger.annotations.ApiModelProperty;

import cn.sylen.common.util.DateTimeUtil;
import cn.sylen.dao.annotation.SqlField;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Sylen
 * @create 2018-04-09
 **/
public abstract class BaseEntity implements Serializable {
	private static final long serialVersionUID = -2455172166170402359L;
	
	@ApiModelProperty("创建时间 ")
	@SqlField("date_create")
    private Date dateCreate;

	@ApiModelProperty("创建者id")
	@SqlField("creator_id")
    private Integer creatorId;
	
	@ApiModelProperty("更新时间")
	@SqlField("date_update")
    private Date dateUpdate;

	@ApiModelProperty("更新者id")
	@SqlField("updator_id")
    private Integer updatorId;

	@ApiModelProperty("删除时间")
	@SqlField("date_delete")
    private Date dateDelete;
	
	@ApiModelProperty("逻辑删除（0：否，1：是）")
	@SqlField("deleted")
    private Boolean deleted;
    
	@ApiModelProperty("创建时间 yyyy-MM-dd HH:mm:ss")
	private String dateCreateStr;

	@ApiModelProperty("更新时间 yyyy-MM-dd HH:mm:ss")
	private String dateUpdateStr;
	
	@ApiModelProperty("删除时间 yyyy-MM-dd HH:mm:ss")
	private String dateDeleteStr;

	public BaseEntity() {
	}

	public Date getDateCreate() {
		return this.dateCreate;
	}

	public void setDateCreate(Date dateCreate) {
		this.dateCreate = dateCreate;
	}

	public Date getDateUpdate() {
		return this.dateUpdate;
	}

	public void setDateUpdate(Date dateUpdate) {
		this.dateUpdate = dateUpdate;
	}

	public Date getDateDelete() {
		return this.dateDelete;
	}

	public void setDateDelete(Date dateDelete) {
		this.dateDelete = dateDelete;
	}

	public Integer getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Integer creatorId) {
		this.creatorId = creatorId;
	}

	public Integer getUpdatorId() {
		return updatorId;
	}

	public void setUpdatorId(Integer updatorId) {
		this.updatorId = updatorId;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
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
