package com.example.ussd.repository;

import com.example.ussd.domain.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetails,Long> {
    boolean existsByPhoneNumber(String phoneNumber);

    UserDetails findByPhoneNumber(String phoneNumber);
}
