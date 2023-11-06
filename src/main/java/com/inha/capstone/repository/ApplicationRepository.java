package com.inha.capstone.repository;

import com.inha.capstone.domain.Application;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ApplicationRepository {

    private final EntityManager em;

    public Application fineOne(Long applicationId) { return em.find(Application.class, applicationId); }
    public void save(Application application) { em.persist(application); }
}
