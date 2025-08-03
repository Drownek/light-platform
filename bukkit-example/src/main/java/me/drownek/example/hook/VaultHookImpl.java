package me.drownek.example.hook;

import eu.okaeri.injector.annotation.Inject;
import me.drownek.platform.core.dependency.HookInitializationException;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class VaultHookImpl implements VaultHook {

    private final Economy economy;

    @Inject
    public VaultHookImpl(JavaPlugin plugin) {
        RegisteredServiceProvider<Economy> registration = 
            plugin.getServer().getServicesManager().getRegistration(Economy.class);

        if (registration == null) {
            throw new HookInitializationException("Vault found but no economy provider available");
        }

        this.economy = registration.getProvider();
    }

    @Override
    public boolean hasEnoughMoney(OfflinePlayer player, double amount) {
        return economy.has(player, amount);
    }

    @Override
    public double getMoney(OfflinePlayer player) {
        return economy.getBalance(player);
    }

    @Override
    public void withdrawMoney(OfflinePlayer player, double amount) {
        economy.withdrawPlayer(player, amount);
    }

    @Override
    public void depositMoney(OfflinePlayer player, double amount) {
        economy.depositPlayer(player, amount);
    }
}
