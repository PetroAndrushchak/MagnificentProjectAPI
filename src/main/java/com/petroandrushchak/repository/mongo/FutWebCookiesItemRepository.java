package com.petroandrushchak.repository.mongo;

import com.petroandrushchak.entity.mongo.FutWebCookiesItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FutWebCookiesItemRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    public boolean areCookiesExistForEAFutAccount(String emailAddress) {
        log.info("Are cookies exist for EA FUT account with email address: " + emailAddress);
        Query query = new Query(Criteria.where("eaFutEmailAddress").is(emailAddress));
        var result = mongoTemplate.exists(query, FutWebCookiesItem.class);
        log.info("Result: " + result);
        return result;
    }

    public FutWebCookiesItem findItemByEaFutEmailAddress(String emailAddress) {
        log.info("Find cookies item by EA FUT account email address: " + emailAddress);
        Query query = new Query(Criteria.where("eaFutEmailAddress").is(emailAddress));
        return mongoTemplate.findOne(query, FutWebCookiesItem.class);
    }

    public void saveItem(FutWebCookiesItem item) {
        var result = mongoTemplate.update(FutWebCookiesItem.class)
                                  .matching(new Query(Criteria.where("eaFutEmailAddress").is(item.getEaFutEmail())))
                                  .replaceWith(item)
                                  .withOptions(FindAndReplaceOptions.options().upsert())
                                  .as(FutWebCookiesItem.class)
                                  .findAndReplace();

        if (result.isEmpty()) {
            log.info("There was no document with email address: " + item.getEaFutEmail() + " so created new one");
        }
    }

}
