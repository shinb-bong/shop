package com.shop.entity.item;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.entity.item.domain.Item;
import com.shop.entity.item.domain.ItemRepository;
import com.shop.entity.item.domain.ItemSellStatus;
import com.shop.entity.item.domain.QItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Transactional
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    EntityManager em;

    @Test
    @DisplayName("상품 저장 테스트")
    public void createItemTest(){
        Item savedItem = getItem();

        System.out.println("savedItem = " + savedItem.toString());
    }



    @Test
    @DisplayName("Querydsl 조회 테스트1")
    public void queryDslTest(){
        this.getItem();
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QItem qItem = QItem.item;
        List<Item> itemList = queryFactory.select(qItem)
                .from(qItem)
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
                .where(qItem.itemDetail.like("%" + "상세" + "%"))
                .orderBy(qItem.price.desc())
                .fetch();

        for (Item item : itemList) {
            System.out.println(item.toString());
        }

    }

    @Test
    @DisplayName("상품 QueryDsl 조회 테스트2")
    public void queryDslTest2(){
        this.getItem2();

        // where 절 담는 것
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QItem item = QItem.item;
        String itemDetail = "상세";
        int price = 10003;
        String itemSellStat = "SELL";

        //where 설정
        booleanBuilder.and(item.itemDetail.like("%"+ itemDetail + "%"));
        booleanBuilder.and(item.price.gt(price));
        booleanBuilder.and(item.itemSellStatus.eq(ItemSellStatus.SELL));

        Pageable pageable = PageRequest.of(0, 5);
        Page<Item> itemPagingResult = itemRepository.findAll(booleanBuilder, pageable);

        System.out.println("total elements : " + itemPagingResult.getTotalElements());

        List<Item> resultItemList = itemPagingResult.getContent();
        for (Item resultItem : resultItemList) {
            System.out.println(resultItem.toString());
        }

    }

    private Item getItem() {
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("상세 설명입니다. 잘봐주세요");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        Item savedItem = itemRepository.save(item);
        return savedItem;
    }

    private void getItem2() {
        for (int i = 0; i <= 5; i++) {
            Item item = new Item();
            item.setItemNm("테스트 상품"+i);
            item.setPrice(10000 + i);
            item.setItemDetail("상세 설명입니다. 잘봐주세요");
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            Item savedItem = itemRepository.save(item);
        }

        for (int i = 6; i <= 10; i++) {
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("상세 설명입니다. 잘봐주세요");
            item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            Item savedItem = itemRepository.save(item);
        }

    }
}