package com.yomplex.simple.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

public class TestsModel implements Serializable {

    public String testPlayedDate;
    public String timeTakenToComplete;
    public String testStatus;
    public String correctAnswers;
    public String totalQuestions;
    public String userId;
    public String useremail;
    public String testname;
    public String phonenumber;
    @ServerTimestamp
    public Date createddate;

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getTestPlayedDate() {
        return testPlayedDate;
    }

    public void setTestPlayedDate(String testPlayedDate) {
        this.testPlayedDate = testPlayedDate;
    }

    public String getTimeTakenToComplete() {
        return timeTakenToComplete;
    }

    public void setTimeTakenToComplete(String timeTakenToComplete) {
        this.timeTakenToComplete = timeTakenToComplete;
    }

    public String getTestStatus() {
        return testStatus;
    }

    public void setTestStatus(String testStatus) {
        this.testStatus = testStatus;
    }

    public String getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(String correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public String getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(String totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }

    public String getTestname() {
        return testname;
    }

    public void setTestname(String testname) {
        this.testname = testname;
    }

    public Date getCreateddate() {
        return createddate;
    }

    public void setCreateddate(Date createddate) {
        this.createddate = createddate;
    }
}
