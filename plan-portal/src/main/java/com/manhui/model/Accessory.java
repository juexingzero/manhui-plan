package com.manhui.model;

import java.util.Date;

/**
 * @ClassName: Accessory
 * @description: 附件文件实体类
 * @author:	HeJiayan
 * @date Create in 下午2:12:43 2017年12月05日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
public class Accessory {
    private Integer id;               //附件编号

    private String model;             //附件所属模块              cooperation(计划协作模块)

    private Integer modelId;          //关联ID

    private String name;              //名称

    private String suffix;            //后缀名

    private String size;              //附件大小      单位KB

    private String path;              //路径

    private Date upTime;              //上传时间

    private Integer status;           //状态          0.有效附件        1.无效附件

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model == null ? null : model.trim();
    }

    public Integer getModelId() {
        return modelId;
    }

    public void setModelId(Integer modelId) {
        this.modelId = modelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix == null ? null : suffix.trim();
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size == null ? null : size.trim();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path == null ? null : path.trim();
    }

    public Date getUpTime() {
        return upTime;
    }

    public void setUpTime(Date upTime) {
        this.upTime = upTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}