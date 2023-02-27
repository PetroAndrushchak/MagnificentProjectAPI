package com.petroandrushchak.mapper.ui.api;

import com.petroandrushchak.exceptions.ItemMappingException;
import com.petroandrushchak.model.fut.*;
import com.petroandrushchak.view.request.PlayerItemRequestBody;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PlayerItemMapper {

    PlayerItemMapper INSTANCE = Mappers.getMapper(PlayerItemMapper.class);

    default PlayerItem playerItemRequestToView(PlayerItemRequestBody playerItemRequestBody) {

        var playerItem = new PlayerItem();

        //Player Name, Player Rating
        var isPlayerNamePresent = playerItemRequestBody.isPlayerNamePresent();
        var isPlayerRatingPresent = playerItemRequestBody.isPlayerRatingPresent();
        var isPlayerIDPresent = playerItemRequestBody.isPlayerIdPresent();
        var isPlayerQualityPresent = playerItemRequestBody.isQualityPresent();
        var isPlayerRarityPresent = playerItemRequestBody.isRarityPresent();
        var isPlayerPositionPresent = playerItemRequestBody.isPositionPresent();
        var isPlayerChemistryStylePresent = playerItemRequestBody.isChemistryStylePresent();

        if (isPlayerIDPresent) playerItem.setId(playerItemRequestBody.getPlayerId());

        if (isPlayerNamePresent && isPlayerRatingPresent) {
            playerItem.setName(playerItemRequestBody.getPlayerName());
            playerItem.setRating(playerItemRequestBody.getPlayerRating());
        }

        if (isPlayerNamePresent && !isPlayerRatingPresent)
            throw new ItemMappingException("Player Rating", "Player rating is not present");
        if (!isPlayerNamePresent && isPlayerRatingPresent)
            throw new ItemMappingException("Player Name", "Player Name is not present");

        if (isPlayerQualityPresent) playerItem.setLevel(Quality.fromApiKey(playerItemRequestBody.getQuality()));
        if (isPlayerRarityPresent) playerItem.setRarity(Rarity.fromApiKey(playerItemRequestBody.getRarity()));
        if (isPlayerPositionPresent) playerItem.setPosition(Position.fromApiKey(playerItemRequestBody.getPosition()));
        if (isPlayerChemistryStylePresent) playerItem.setChemistryStyle(ChemistryStyle.fromApiKey(playerItemRequestBody.getChemistryStyle()));


        //TODO Other fields


        return playerItem;
    }


}
