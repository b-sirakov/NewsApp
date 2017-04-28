package com.example.nikoleta.newsapp.string_holders;


public class StringHolder {

    private String section;
    private String businessURL;
    private String healthURL;
    private String politicsURL;
    private String sportsURL;
    private String technologiesURL;

    public StringHolder( ) {
    }

    public void setSection(String section) {
        this.section = section;
    }

    public void setBusinessURL(String businessURL) {
        this.businessURL = businessURL;
    }

    public void setHealthURL(String healthURL) {
        this.healthURL = healthURL;
    }

    public void setPoliticsURL(String politicsURL) {
        this.politicsURL = politicsURL;
    }

    public void setSportsURL(String sportsURL) {
        this.sportsURL = sportsURL;
    }

    public void setTechnologiesURL(String technologiesURL) {
        this.technologiesURL = technologiesURL;
    }

    public String getSection() {
        return section;
    }

    public String getBusinessURL() {
        return businessURL;
    }

    public String getHealthURL() {
        return healthURL;
    }

    public String getPoliticsURL() {
        return politicsURL;
    }

    public String getSportsURL() {
        return sportsURL;
    }

    public String getTechnologiesURL() {
        return technologiesURL;
    }
}
