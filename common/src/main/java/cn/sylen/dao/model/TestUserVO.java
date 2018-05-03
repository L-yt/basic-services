package cn.sylen.dao.model;

import cn.sylen.common.vo.BaseEntity;
import cn.sylen.dao.annotation.SqlTable;

@SqlTable("user")
public class TestUserVO extends BaseEntity {
    private static final long serialVersionUID = -6505184666114522948L;
    public int cid;
    private String phone;
    private String name;
    public int getCid() {
        return cid;
    }
    public void setCid(int id) {
        this.cid = id;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return "TestUserVO [cid=" + cid + ", phone=" + phone + ", name=" + name
                + "]";
    }

}
