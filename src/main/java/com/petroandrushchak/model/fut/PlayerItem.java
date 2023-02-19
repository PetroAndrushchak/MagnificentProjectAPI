package com.petroandrushchak.model.fut;

import lombok.*;
import lombok.experimental.SuperBuilder;

//TODO Add League, Nation, Club, Chemistry style

@Data
@NoArgsConstructor
public class PlayerItem extends Item {

    private String name;
    private String rating;
    private ItemLevel quality;
    private PlayerPosition position;

    @Override
    public String toString() {
        return "Player{ " + name + "  " + rating + '}';
    }
}
