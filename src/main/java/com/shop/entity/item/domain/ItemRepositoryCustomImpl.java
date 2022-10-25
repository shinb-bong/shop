package com.shop.entity.item.domain;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.entity.item.dto.ItemSearchDto;
import com.shop.main.MainItemDto;
import com.shop.main.QMainItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{

    private JPAQueryFactory queryFactory;

    // DI를 통해 직접 Enitity매니저 주입

    public ItemRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    // 상품 등록일에 대한 조회 조건 BooleanExpression
    private BooleanExpression reDtsAfter(String searchDateType){
        LocalDateTime dateTime = LocalDateTime.now();
        if(StringUtils.equals("all", searchDateType) || searchDateType == null){
            return null;
        } else if (StringUtils.equals("1d",searchDateType)) {
            dateTime = dateTime.minusDays(1);
        }
        else if (StringUtils.equals("1w",searchDateType)) {
            dateTime = dateTime.minusWeeks(1);
        }
        else if (StringUtils.equals("1m",searchDateType)) {
            dateTime = dateTime.minusMonths(1);
        }
        else if (StringUtils.equals("6m",searchDateType)) {
            dateTime = dateTime.minusMonths(6);
        }

        return QItem.item.regTime.after(dateTime);
    }

    // 상품 상태 대한 조회 조건 BooleanExpression
    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus){
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
    }

    // 상품명 또는 상품 등록자 아이디 조건
    private BooleanExpression searchByLike(String searchBy, String searchQuery){
        if (StringUtils.equals("itemNm",searchBy)){
            return QItem.item.itemNm.like("%" + searchQuery +"%" );
        } else if (StringUtils.equals("createdBy",searchBy)) {
            return QItem.item.createdBy.like("%" + searchQuery +"%"); //여기 알아볼것
        }
        return null;
    }

    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QueryResults<Item> results = queryFactory
                .selectFrom(QItem.item)
                .where(reDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();//2번의 select 문

        // 조회 대상 리스트 결과
        List<Item> content = results.getResults();

        // 조회 대상 리스트 개수
        long total = results.getTotal();

        return new PageImpl<>(content,pageable,total);

    }

    /**
     * Main 페이지
     */
    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        QueryResults<MainItemDto> result = queryFactory
                .select(
                        new QMainItemDto(
                                item.id,
                                item.itemNm,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price
                        )
                ).from(itemImg)
                .join(itemImg.item, item) // 외래키 맵핑 위치 주의 item 테이블과의 조인
                .where(itemImg.repimgYn.eq("Y")) // 대표 사진만 조회
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<MainItemDto> content = result.getResults();
        long total = result.getTotal();
        return new PageImpl<>(content,pageable,total);
    }

    private BooleanExpression itemNmLike(String searchQuery) {
        return StringUtils.isEmpty(searchQuery) ? null:
                QItem.item.itemNm.like("%" + searchQuery +"%");
    }
}
