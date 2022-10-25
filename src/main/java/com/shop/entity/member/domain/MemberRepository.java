package com.shop.entity.member.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long> {

    // 이메일로 회원가입 중복 여부 확인
    Member findByEmail(String email);
}
