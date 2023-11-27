package com.petroandrushchak.telegram.my.db;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

public class FUT_Accounts {

    static List<FUT_Account> fut_accounts = List.of(
            new FUT_Account("Stivka999", "petrofiffut@gmail.com"),
            new FUT_Account("pasha_lviv", "petroandrushchak@yahoo.com "));

    static public FUT_Account activeFutAccount;

    public static boolean isActiveFutAccountSet() {
        return activeFutAccount != null;
    }

    public static String getActiveFutAccountName() {
        return activeFutAccount.name;
    }

    public static boolean isFutAccountInTheList(String name) {
        return fut_accounts.stream()
                           .anyMatch(fut_account -> fut_account.name.equals(name));
    }

    public static void setActiveFutAccount(String name) {
        fut_accounts.stream()
                    .filter(fut_account -> fut_account.name.equals(name))
                    .findFirst()
                    .ifPresent(fut_account -> activeFutAccount = fut_account);
    }

    @Data
    @AllArgsConstructor
    public static class FUT_Account {

        public String name;
        public String email;


    }
}
