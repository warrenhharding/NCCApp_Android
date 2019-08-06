package com.salzburg101.nccapp;

public class StructureForRetrieveAlertPreferences {

    private String mTopic, mTopicCode, mTopicDescription;
    private Boolean mTopicOptional;

    public StructureForRetrieveAlertPreferences(String topic, Boolean topicOptional, String topicCode, String topicDescription) {
        mTopic = topic;
        mTopicOptional = topicOptional;
        mTopicCode = topicCode;
        mTopicDescription = topicDescription;
    }


    public String getTopic() {
        return mTopic;
    }

    public Boolean getTopicOptional() {
        return mTopicOptional;
    }

    public String getTopicDescription() {
        return mTopicDescription;
    }

    public String getTopicCode() {
        return mTopicCode;
    }

}
