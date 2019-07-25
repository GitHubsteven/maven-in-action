package com.asa.example.maven.bean;

import com.asa.example.maven.model.DemoModel;

/**
 * @version 1.0.0 COPYRIGHT © 2001 - 2018 VOYAGE ONE GROUP INC. ALL RIGHTS RESERVED.
 * @Author jet.xie
 * @Description:
 * @Date: Created at 16:56 2019/7/25.
 */
public class DemoBean extends DemoModel {
    private String sourceOrderId;

    public String getSourceOrderId() {
        return sourceOrderId;
    }

    public void setSourceOrderId(String sourceOrderId) {
        this.sourceOrderId = sourceOrderId;
    }
}