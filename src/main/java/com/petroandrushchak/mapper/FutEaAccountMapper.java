package com.petroandrushchak.mapper;

import com.petroandrushchak.entity.FutAccountEntity;
import com.petroandrushchak.entity.MailEntity;
import com.petroandrushchak.view.FutEaAccountView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper
public interface FutEaAccountMapper {

    FutEaAccountMapper INSTANCE = Mappers.getMapper(FutEaAccountMapper.class);

    default FutEaAccountView entityAccountToModel(FutAccountEntity futAccountEntity) {
        return FutEaAccountView.anFutEaAccount()
                                  .withId(futAccountEntity.getId())
                                  .withUsername(futAccountEntity.getUsername())

                                  .withEaEmailEmail(futAccountEntity.getEaEmail().getEmailAddress())
                                  .withEaEmailPassword(futAccountEntity.getEaEmail().getEmailPassword())

                                  .withEaLogin(futAccountEntity.getEaLogin())
                                  .withEaPassword(futAccountEntity.getEaPassword())

                                  .build();
    }

    default FutAccountEntity accountModelToEntity(FutEaAccountView futEaAccountUiModel) {
        var mail = new MailEntity();
        mail.setEmailAddress(futEaAccountUiModel.getEaEmailEmail());
        mail.setEmailPassword(futEaAccountUiModel.getEaEmailPassword());

        var entity = new FutAccountEntity();
        entity.setId(futEaAccountUiModel.getId());
        entity.setUsername(futEaAccountUiModel.getUsername());

        entity.setEaLogin(futEaAccountUiModel.getEaLogin());
        entity.setEaPassword(futEaAccountUiModel.getEaPassword());
        entity.setEaEmail(mail);
        return entity;
    }


}
