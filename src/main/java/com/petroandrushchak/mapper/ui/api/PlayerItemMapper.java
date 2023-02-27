package com.petroandrushchak.mapper.ui.api;

import com.petroandrushchak.entity.mongo.FutEaDbPlayer;
import com.petroandrushchak.exceptions.ItemMappingException;
import com.petroandrushchak.model.fut.*;
import com.petroandrushchak.view.request.PlayerItemRequestBody;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Objects;

@Mapper
public interface PlayerItemMapper {

    PlayerItemMapper INSTANCE = Mappers.getMapper(PlayerItemMapper.class);

    default PlayerItem playerItemRequestToView(PlayerItemRequestBody playerItemRequestBody, FutEaDbPlayer futEaDbPlayer) {

        var playerItem = new PlayerItem();

        //Player Name, Player Rating
        var isPlayerNamePresent = playerItemRequestBody.isPlayerNamePresent();
        var isPlayerRatingPresent = playerItemRequestBody.isPlayerRatingPresent();
        var isPlayerIDPresent = playerItemRequestBody.isPlayerIdPresent();
        var isPlayerQualityPresent = playerItemRequestBody.isQualityPresent();
        var isPlayerRarityPresent = playerItemRequestBody.isRarityPresent();
        var isPlayerPositionPresent = playerItemRequestBody.isPositionPresent();
        var isPlayerChemistryStylePresent = playerItemRequestBody.isChemistryStylePresent();

        if (futEaDbPlayer.isNickNamePresent()) {
            playerItem.setPlayerName(futEaDbPlayer.getNickName());
        } else {
            playerItem.setPlayerName(futEaDbPlayer.getFirstName() + " " + futEaDbPlayer.getLastName());
        }

        if (isPlayerIDPresent && Objects.equals(playerItemRequestBody.getPlayerId(), futEaDbPlayer.getPlayerId())) {
            playerItem.setId(playerItemRequestBody.getPlayerId());
        } else if (!isPlayerIDPresent) {
            playerItem.setId(futEaDbPlayer.getPlayerId());
        } else {
            throw new ItemMappingException("Player ID", "Player ID is not correct");
        }

        if (isPlayerRatingPresent && (Objects.equals(playerItemRequestBody.getPlayerRating(), futEaDbPlayer.getRating()))) {
            playerItem.setRating(playerItemRequestBody.getPlayerRating());
        } else if (!isPlayerRatingPresent) {
            playerItem.setRating(futEaDbPlayer.getRating());
        } else {
            throw new ItemMappingException("Player Rating", "Player rating is not correct");
        }


        if (isPlayerQualityPresent) playerItem.setLevel(Quality.fromApiKey(playerItemRequestBody.getQuality()));
        if (isPlayerRarityPresent) playerItem.setRarity(Rarity.fromApiKey(playerItemRequestBody.getRarity()));
        if (isPlayerPositionPresent) playerItem.setPosition(Position.fromApiKey(playerItemRequestBody.getPosition()));
        if (isPlayerChemistryStylePresent)
            playerItem.setChemistryStyle(ChemistryStyle.fromApiKey(playerItemRequestBody.getChemistryStyle()));


        //TODO Other fields


        return playerItem;
    }


}
