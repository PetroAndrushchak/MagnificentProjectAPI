package com.petroandrushchak.repository.mongo;


import com.petroandrushchak.entity.mongo.FutEaDbPlayer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.StringOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;


import java.util.List;

@Slf4j
@Component
public class FUTPlayersRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    public List<FutEaDbPlayer> findAll() {
        return mongoTemplate.findAll(FutEaDbPlayer.class);
    }

    public List<FutEaDbPlayer> findByPlayerId(Long playerId) {
        return mongoTemplate.find(Query.query(Criteria.where("id").is(playerId)), FutEaDbPlayer.class);
    }

    public List<FutEaDbPlayer> findByNickname(String nickName) {
        return mongoTemplate.find(Query.query(Criteria.where("c").is(nickName)), FutEaDbPlayer.class);
    }

    public List<FutEaDbPlayer> findByFullName(String fullName) {

        Aggregation aggregation = newAggregation(
                project("firstName", "lastName")
                        .andExpression("concat(f, ' ', l)").as("fullName"),
                match(Criteria.where("fullName").is(fullName)));

        AggregationResults<FutEaDbPlayer> result = mongoTemplate.aggregate(aggregation, FutEaDbPlayer.class, FutEaDbPlayer.class);

        return result.getMappedResults().stream()
                .map(FutEaDbPlayer::get_id)
                .map(mongoDbId -> mongoTemplate.findById(mongoDbId, FutEaDbPlayer.class))
                .toList();
    }


}
