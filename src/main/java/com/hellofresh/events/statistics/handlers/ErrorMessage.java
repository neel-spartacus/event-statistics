package com.hellofresh.events.statistics.handlers;

import java.util.ArrayList;
import java.util.List;

public class ErrorMessage {

    String errorMessage;
    List<String> errorNotifications = new ArrayList<String>();

    public ErrorMessage() {
    }

    public ErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ErrorMessage(List<String> errorNotifications) {
        this.errorNotifications = errorNotifications;
    }

    public void addErrorNotifications(String notification) {
        errorNotifications.add(notification);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<String> getErrorNotifications() {
        return errorNotifications;
    }

    public void setErrorNotifications(List<String> errorNotifications) {
        this.errorNotifications = errorNotifications;
    }
}
