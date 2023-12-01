package com.inha.capstone.util;

import com.inha.capstone.config.BaseException;
import com.inha.capstone.config.BaseResponseStatus;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class MakeRealService {
  // open AI 키
  @Value("${env.gpt.key}")
  String gptKey;
  @Value("${env.url.makereal-sanvit}")
  String makeRealSanvitUrl;
  public String makeRealConverter(String uiImageUrl)  throws IOException{
    OkHttpClient client = new OkHttpClient.Builder().callTimeout(1, TimeUnit.HOURS).readTimeout(1, TimeUnit.HOURS).connectTimeout(1, TimeUnit.HOURS).build();

    String json = "{\"openai_key\":\"" + gptKey + "\",\"image_url\":\"" + uiImageUrl + "\"}";

    MediaType mediaType = MediaType.parse("application/json");
    RequestBody requestBody = RequestBody.create(mediaType,
        json);

    Request request = new Request.Builder()
        .url("https://makereal.sanvit.workers.dev")
        .post(requestBody)
        .addHeader("content-type", "application/json")
        .build();

    ResponseBody responseBody = null;
    try {
      Response response = client.newCall(request).execute();
      responseBody = response.body();
    }
    catch (IOException e) {
      log.error("make real converter error");
      log.error(e.getMessage());
      e.printStackTrace();
      throw new BaseException(BaseResponseStatus.MAKE_REAL_CONVERTER_ERROR);
    }
    return responseBody.string();
  }
}
