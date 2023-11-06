package com.inha.capstone.repository;

import com.inha.capstone.domain.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final EntityManager em;

    public User fineOne(Long userId) { return em.find(User.class, userId); }
    public void save(User user) { em.persist(user); }

    public Optional<User> findById(String id) {
        return em.createQuery(
                "SELECT u FROM Users u " +
                        "WHERE u.id =: id ",
                User.class)
                .setParameter("id",id)
                .getResultList()
                .stream().findAny();
    }


}
