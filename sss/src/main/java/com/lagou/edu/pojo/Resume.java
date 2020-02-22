package com.lagou.edu.pojo;

import javax.persistence.*;

/**
 * 简历实体类
 */
@Entity
@Table(name = "tb_resume")
public class Resume {
    //主键
    @Id
    /**
     * 生成策略
     * GenerationType.IDENTITY:依靠数据库中主键自增功能
     * GenerationType.SEQUENCE:依靠序列来产生主键      ORACLE
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    @Column(name="name")
    private String name;//姓名
    @Column(name="address")
    private String address;//地址
    @Column(name="phone")
    private String phone;//电话

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Resume{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
