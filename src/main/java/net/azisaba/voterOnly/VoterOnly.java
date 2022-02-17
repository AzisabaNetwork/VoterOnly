package net.azisaba.voterOnly;

import net.azisaba.voterOnly.listener.CheckJoinListener;
import net.azisaba.voterOnly.listener.VoteListener;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiPredicate;

public class VoterOnly extends JavaPlugin {
    private static final String DEFAULT_DISALLOW_MESSAGE = "&c現在投票者のみが参加可能です。";
    private BiPredicate<UUID, String> permissionChecker = (uuid, permission) -> false;
    private final Set<String> allowedList = new HashSet<>();
    public int freeUntil = 0;
    public String disallowMessage = DEFAULT_DISALLOW_MESSAGE;

    @Override
    public void onEnable() {
        reload();
        try {
            RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
            if (rsp != null) {
                Permission perm = rsp.getProvider();
                permissionChecker = (uuid, permission) -> perm.playerHas(null, Bukkit.getOfflinePlayer(uuid), permission);
            }
        } catch (Throwable ignored) {
            getLogger().warning("Vault is not installed");
        }
        Bukkit.getPluginManager().registerEvents(new VoteListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CheckJoinListener(this), this);
        schedule();
    }

    @Override
    public void onDisable() {
        save();
    }

    public void reload() {
        allowedList.clear();
        allowedList.addAll(getConfig().getStringList("allowedList"));
        freeUntil = Math.max(0, getConfig().getInt("freeUntil", 0));
        disallowMessage = getConfig().getString("disallowMessage", DEFAULT_DISALLOW_MESSAGE);
    }

    public void save() {
        getConfig().set("allowedList", allowedList);
        getConfig().set("freeUntil", freeUntil);
        getConfig().set("disallowMessage", disallowMessage);
        saveConfig();
    }

    public void schedule() {
        LocalDateTime resetOn = LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0, 0));
        if (resetOn.isBefore(LocalDateTime.now())) {
            resetOn = resetOn.plusDays(1);
        }
        long delayInSeconds = resetOn.toEpochSecond(OffsetDateTime.now().getOffset()) - (System.currentTimeMillis() / 1000L);
        Bukkit.getScheduler().runTaskLater(this, () -> {
            allowedList.clear();
            getLogger().info("allowedList was reset at " + LocalDateTime.now());
            Bukkit.getScheduler().runTaskLater(this, this::schedule, 200);
        }, Math.max(0, delayInSeconds * 20));
    }

    public void addToAllowedList(String username) {
        Bukkit.getScheduler().runTask(this, () -> this.allowedList.add(username));
    }

    public boolean isAllowed(String username) {
        return allowedList.contains(username);
    }

    public boolean hasPermission(UUID uuid, String permission) {
        return permissionChecker.test(uuid, permission);
    }
}
