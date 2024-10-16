package uwu.syndets.altsdetector;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class AltsDetector extends JavaPlugin implements Listener {

    private final Map<String, Set<String>> ipToAccounts = new HashMap<>();
    private FileConfiguration config;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();


        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("AltsPlugin has been enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("AltsPlugin has been disabled.");
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String ip = player.getAddress().getAddress().getHostAddress();
        String playerName = player.getName();

        ipToAccounts.putIfAbsent(ip, new HashSet<>());
        ipToAccounts.get(ip).add(playerName);


        if (config.getBoolean("auto-ban", false)) {
            for (String account : ipToAccounts.get(ip)) {
                if (Bukkit.getBanList(BanList.Type.NAME).isBanned(account)) {
                    player.kickPlayer(ChatColor.RED + "You have logged in with a banned account.");
                    Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), "Linked to banned alt account", null, null);
                    getLogger().info("Auto-banned " + playerName + " for logging in with a banned alt.");
                    break;
                }
            }
        }
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("alts")) {
            if (args.length != 1) {
                sender.sendMessage(ChatColor.RED + "Usage: /alts [playername]");
                return false;
            }

            String targetPlayerName = args[0];
            OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetPlayerName);


            if (targetPlayer == null || !targetPlayer.hasPlayedBefore()) {
                sender.sendMessage(ChatColor.RED + "Player not found or never joined.");
                return true;
            }

            String targetIp = null;
            if (targetPlayer.isOnline()) {
                targetIp = ((Player) targetPlayer).getAddress().getAddress().getHostAddress(); 
            } else {
                for (Map.Entry<String, Set<String>> entry : ipToAccounts.entrySet()) {
                    if (entry.getValue().contains(targetPlayerName)) {
                        targetIp = entry.getKey();
                        break;
                    }
                }
            }

            if (targetIp == null) {
                sender.sendMessage(ChatColor.YELLOW + "No alternate accounts found for this player.");
                return true;
            }


            Set<String> associatedAccounts = ipToAccounts.get(targetIp);

            if (associatedAccounts == null || associatedAccounts.isEmpty()) {
                sender.sendMessage(ChatColor.YELLOW + "No alternate accounts found for this player.");
                return true;
            }

            if (config.getBoolean("hide-ip", true)) {
                sender.sendMessage(ChatColor.AQUA + "Accounts associated with player " + ChatColor.YELLOW + targetPlayerName + ":");
            } else {
                sender.sendMessage(ChatColor.AQUA + "Accounts associated with IP " + ChatColor.YELLOW + targetIp + ":");
            }


            for (String account : associatedAccounts) {
                if (Bukkit.getBanList(BanList.Type.NAME).isBanned(account)) {
                    sender.sendMessage(ChatColor.RED + account);
                } else {
                    sender.sendMessage(ChatColor.GREEN + account);
                }
            }

            return true;
        }
        return false;
    }
}