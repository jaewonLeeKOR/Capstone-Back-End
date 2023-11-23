package com.inha.capstone.util;

import java.util.Optional;

public class LogUtil {
  public enum HttpRequestMethod {
    GET, POST, PUT, PATCH, DELETE, HEAD;

    @Override
    public String toString() {
      return super.name();
    }
  }
  public static String getRequestLog(HttpRequestMethod httpRequestMethod, String endpointPath, Object requestObject) {
    return "REQUESTED " + httpRequestMethod + " " + endpointPath + (requestObject != null ? " - request:" + requestObject.toString() : "");
  }
  public static String getResponseLog(HttpRequestMethod httpRequestMethod, String endpointPath, Object requestObject, Object reponseObject) {
    return "RESPONSED " + httpRequestMethod + " " + endpointPath + (requestObject != null ? " - request:" + requestObject.toString() : "") + (reponseObject != null ? " - response:" + reponseObject.toString() : "");
  }
}
