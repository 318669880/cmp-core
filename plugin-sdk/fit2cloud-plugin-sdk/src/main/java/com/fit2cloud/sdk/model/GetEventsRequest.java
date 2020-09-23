package com.fit2cloud.sdk.model;

public class GetEventsRequest extends Request{
    private String[] eventLevels;
    private long startTimeInMills;
    private long endTimeInMills;

    public String[] getEventLevels() {
        return eventLevels;
    }

    public void setEventLevels(String[] eventLevels) {
        this.eventLevels = eventLevels;
    }

    public long getStartTimeInMills() {
        return startTimeInMills;
    }

    public void setStartTimeInMills(long startTimeInMills) {
        this.startTimeInMills = startTimeInMills;
    }

    public long getEndTimeInMills() {
        return endTimeInMills;
    }

    public void setEndTimeInMills(long endTimeInMills) {
        this.endTimeInMills = endTimeInMills;
    }
}
