package edu.oregonstate.mist.catalogapitest.core

import com.fasterxml.jackson.annotation.JsonProperty

class ErrorPOJO {
    String errorMessage
    Integer errorCode

    public ErrorPOJO() {
        // Jackson deserialization
    }
}
/*
    public ErrorPOJO(String errorMessage, Integer errorCode) {
        this.errorMessage = errorMessage
        this.errorCode = errorCode
    }

    @JsonProperty
    public String getErrorMessage() {
        return errorMessage
    }
    @JsonProperty
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage
    }

    @JsonProperty
    public Integer getErrorCode() {
        return errorCode
    }

    @JsonProperty
    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode
    }

}
*/