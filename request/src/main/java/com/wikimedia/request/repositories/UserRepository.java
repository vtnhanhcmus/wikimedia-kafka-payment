package com.wikimedia.request.repositories;

import com.wikimedia.request.entity.FakeUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<FakeUser, Long> {
    @Query("SELECT fu FROM FakeUser fu ORDER BY RANDOM() LIMIT 1")
    FakeUser findRandomUser();

}