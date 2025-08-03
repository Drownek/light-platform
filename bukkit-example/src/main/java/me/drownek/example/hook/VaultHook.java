package me.drownek.example.hook;

import org.bukkit.OfflinePlayer;

public interface VaultHook {

    boolean hasEnoughMoney(OfflinePlayer player, double amount);

    double getMoney(OfflinePlayer player);

    void withdrawMoney(OfflinePlayer player, double amount);

    void depositMoney(OfflinePlayer player, double amount);
}
