package me.drownek.example.hook;

import org.bukkit.OfflinePlayer;

public class VaultHookFallback implements VaultHook {

    @Override
    public boolean hasEnoughMoney(OfflinePlayer player, double amount) {
        return false;
    }

    @Override
    public double getMoney(OfflinePlayer player) {
        return 0.0;
    }

    @Override
    public void withdrawMoney(OfflinePlayer player, double amount) {
    }

    @Override
    public void depositMoney(OfflinePlayer player, double amount) {
    }
}
