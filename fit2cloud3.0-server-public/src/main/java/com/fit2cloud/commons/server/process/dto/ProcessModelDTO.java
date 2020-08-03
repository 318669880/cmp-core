package com.fit2cloud.commons.server.process.dto;

import java.util.List;

public class ProcessModelDTO {
    private List<ActivityDTO> activities;

    public List<ActivityDTO> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityDTO> activities) {
        this.activities = activities;
    }
}
