package com.manhui.model.jurisdictionManager;

/**
 * @ClassName: JurisdictionManager
 * @description: 权限管理实体类
 * @author:	Jiangxiaosong	
 * @date Create in 上午11:12:43 2017年12月21日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
public class JurisdictionManager {
    private Integer id;

    private String createPlanMin;

    private String decomposePlanMin;

    private String adjustPlanMin;

    private String acceptancePlanMax;

    private String taPlanMax;

    private String approvalPlanMaxSection1;

    private String approvalPlanMaxSection2;

    private String noAssess;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCreatePlanMin() {
        return createPlanMin;
    }

    public void setCreatePlanMin(String createPlanMin) {
        this.createPlanMin = createPlanMin == null ? null : createPlanMin.trim();
    }

    public String getDecomposePlanMin() {
        return decomposePlanMin;
    }

    public void setDecomposePlanMin(String decomposePlanMin) {
        this.decomposePlanMin = decomposePlanMin == null ? null : decomposePlanMin.trim();
    }

    public String getAdjustPlanMin() {
        return adjustPlanMin;
    }

    public void setAdjustPlanMin(String adjustPlanMin) {
        this.adjustPlanMin = adjustPlanMin == null ? null : adjustPlanMin.trim();
    }

    public String getAcceptancePlanMax() {
        return acceptancePlanMax;
    }

    public void setAcceptancePlanMax(String acceptancePlanMax) {
        this.acceptancePlanMax = acceptancePlanMax == null ? null : acceptancePlanMax.trim();
    }

    public String getTaPlanMax() {
        return taPlanMax;
    }

    public void setTaPlanMax(String taPlanMax) {
        this.taPlanMax = taPlanMax == null ? null : taPlanMax.trim();
    }

    public String getApprovalPlanMaxSection1() {
        return approvalPlanMaxSection1;
    }

    public void setApprovalPlanMaxSection1(String approvalPlanMaxSection1) {
        this.approvalPlanMaxSection1 = approvalPlanMaxSection1 == null ? null : approvalPlanMaxSection1.trim();
    }

    public String getApprovalPlanMaxSection2() {
        return approvalPlanMaxSection2;
    }

    public void setApprovalPlanMaxSection2(String approvalPlanMaxSection2) {
        this.approvalPlanMaxSection2 = approvalPlanMaxSection2 == null ? null : approvalPlanMaxSection2.trim();
    }

    public String getNoAssess() {
        return noAssess;
    }

    public void setNoAssess(String noAssess) {
        this.noAssess = noAssess == null ? null : noAssess.trim();
    }
}