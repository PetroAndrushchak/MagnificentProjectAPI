package com.petroandrushchak.entity.mongo;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document("FUTPlayers")
public class FutEaDbPlayer {

    @Id
    String _id;

    @Field("id")
    Long playerId;

    @Field("f")
    String firstName;

    @Field("l")
    String lastName;

    @Field("c")
    String nickName;

    @Field("r")
    Integer rating;

    public boolean isNickNamePresent() {
        return nickName != null && !nickName.isEmpty();
    }

}
