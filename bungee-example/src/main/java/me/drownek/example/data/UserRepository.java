package me.drownek.example.data;

import eu.okaeri.persistence.Persistence;
import eu.okaeri.persistence.repository.DocumentRepository;
import eu.okaeri.persistence.repository.annotation.DocumentCollection;
import me.drownek.platform.core.annotation.DependsOn;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

@DependsOn(name = "persistence", type = Persistence.class)
@DocumentCollection(path = "users", keyLength = 36)
public interface UserRepository extends DocumentRepository<UUID, User> {

    default User getByPlayer(ProxiedPlayer player) {
        return findOrCreateByPath(player.getUniqueId());
    }
}
