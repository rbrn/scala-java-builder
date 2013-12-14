package com.javabuilder;

import java.util.Date;

public class TestMock {
    private long entityAuditId;
    private String entityName;
    private long entityId;

    private Date changedDate;


    public long getEntityAuditId() {
        return entityAuditId;
    }

    public void setEntityAuditId(long entityAuditId) {
        this.entityAuditId = entityAuditId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public Date getChangedDate() {
        return changedDate;
    }

    public void setChangedDate(Date changedDate) {
        this.changedDate = changedDate;
    }
}
