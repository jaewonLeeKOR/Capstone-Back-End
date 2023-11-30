package com.inha.capstone.service;


import com.inha.capstone.config.BaseException;
import com.inha.capstone.config.BaseResponseStatus;
import com.inha.capstone.domain.Application;
import com.inha.capstone.domain.User;
import com.inha.capstone.repository.ApplicationRepository;

import com.inha.capstone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final MongoTemplate mongoTemplate;
    private final UserRepository userRepository;
    @Transactional
    public void saveV1(Application application, String UI) throws ParseException {
        // postgresql에 저장
        applicationRepository.save(application);

        // mongoDB에 저장
        JSONParser parser = new JSONParser();
        JSONObject UIJson = (JSONObject) parser.parse(UI);
        UIJson.put("applicationId", application.getApplicationId());
        mongoTemplate.insert(UIJson, "app");
    }
    @Transactional
    public Application save(Application application) throws ParseException {
        return applicationRepository.save(application);
    }

    public JSONObject getApplicationUIV1(Long applicationId){
        Query query = new Query(Criteria.where("applicationId").is(applicationId));
        List<JSONObject> UI = mongoTemplate.find(query, JSONObject.class, "app");

        return UI.get(0);
    }

    public String getApplicationUrl(Long applicationId){
        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_EXIST_APPLICATION));
        return application.getApplicationUrl();
    }

    @Transactional
    public void updateApplicationV1(Long applicationId, String newUi) throws ParseException{
        // 수정 시간 변경
        Application application = applicationRepository.findById(applicationId).get();
        application.setModifiedDate(LocalDateTime.now());

        // 몽고db 업데이트
        Query query = new Query(Criteria.where("applicationId").is(applicationId));
        mongoTemplate.remove(query, "app");

        JSONParser parser = new JSONParser();
        JSONObject UIJson = (JSONObject) parser.parse(newUi);
        UIJson.put("applicationId", applicationId);
        mongoTemplate.insert(UIJson, "app");
    }

    public void checkPermissionForApplication(Principal principal, Long applicationId){
        User user = userRepository.findByUserId(principal.getName()).get();
        Application application = applicationRepository.findById(applicationId).get();

        if(user.getUserId() != application.getUser().getUserId())
            throw new BaseException(BaseResponseStatus.PERMISSION_DENIED);
    }

    public List<Application> findByNameContaining(String keyword) { return applicationRepository.findByTitleContaining(keyword);}

    public Application findById(Long applicationId) {
        Optional<Application> application = applicationRepository.findById(applicationId);
        if(application.isEmpty())
            throw new BaseException(BaseResponseStatus.NOT_EXIST_APPLICATION);

        return application.get();
    }

    public List<Application> findAllApplication(){ return applicationRepository.findAll();}
}
