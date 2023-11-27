package com.petroandrushchak.service;

import com.petroandrushchak.fut.exeptions.NotFoundException;
import com.petroandrushchak.model.fut.PlayerItem;
import com.petroandrushchak.repository.mongo.FUTPlayersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

//TODO remove this class
@Deprecated
@Slf4j
public class FutPlayerService {

    private final FUTPlayersRepository futPlayersRepository;

    public FutPlayerService(FUTPlayersRepository futPlayersRepository) {
        this.futPlayersRepository = futPlayersRepository;
    }

    public PlayerItem getPlayerById(long id) {
        var futPlayers = futPlayersRepository.findByPlayerId(id);

        if (futPlayers.isEmpty()) {
            throw new NotFoundException("Player with id: " + id + " not found in DB");
        } else if (futPlayers.size() > 1) {
            throw new NotFoundException("Player with id: " + id + " found more than 1 in DB");
        }

        var futPlayer = futPlayers.get(0);

        PlayerItem playerItem = new PlayerItem();
        playerItem.setId(futPlayer.getPlayerId());
        playerItem.setPlayerFirstName(futPlayer.getFirstName());
        playerItem.setPlayerLastName(futPlayer.getLastName());
        playerItem.setNickName(futPlayer.getNickName());
        playerItem.setRating(futPlayer.getRating());

        return playerItem;


    }


}
