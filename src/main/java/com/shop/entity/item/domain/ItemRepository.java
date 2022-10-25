package com.shop.entity.item.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface ItemRepository extends
        JpaRepository<Item,Long>, QuerydslPredicateExecutor,ItemRepositoryCustom {

    List<Item> findByItemNm(String itemNm);
}
