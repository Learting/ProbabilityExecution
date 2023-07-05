package cc.globalserver.ProbabilityExecution;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import me.clip.placeholderapi.PlaceholderAPI;

import java.util.Collections;
import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

public class Main extends JavaPlugin implements CommandExecutor {

    private FileConfiguration config;
    public FileConfiguration playerData;
    private FileConfiguration messages;

    @Override
    public void onEnable() {
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            new Expansion(this).register();
        }
        saveDefaultConfig();
        createConfig("playerdata.yml");
        createConfig("messages.yml");
        super.onEnable();
        this.getCommand("pe").setExecutor(this);
        this.getCommand("peadd").setExecutor(this);
        this.getCommand("peremove").setExecutor(this);
        this.getCommand("peset").setExecutor(this);
        this.getCommand("peshow").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        switch (command.getName().toLowerCase()) {
            case "pe":
                executeCommand(sender, args);
                break;

            case "peadd":
                modifyTimes(sender, args, "add");
                break;

            case "peremove":
                modifyTimes(sender, args, "remove");
                break;

            case "peset":
                modifyTimes(sender, args, "set");
                break;

            case "peshow":
                showTimes(sender, args);
                break;

            default:
                return false;
        }
        return true;
    }

    public void createConfig(String fileName){
        File file = new File(getDataFolder(), fileName);

        if(!file.exists()){
            saveResource(fileName, false);
        }

        if (fileName.equals("playerdata.yml")){
            playerData = YamlConfiguration.loadConfiguration(file);
        }else if(fileName.equals("messages.yml")){
            messages = YamlConfiguration.loadConfiguration(file);
        }
    }

    private void executeCommand(CommandSender sender, String[] args) {
        if (args.length != 1) return;

        String pID = args[0];
        Player player = Bukkit.getPlayer(pID);
        int times = this.playerData.getInt(pID);

        if (player == null) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.messages.getString("playerNotFound")));
            return;
        }

        if (times > 0) {
            Set<String> commandsSet = this.getConfig().getKeys(false);
            List<String> listOfCommands = new ArrayList<>(commandsSet);
            Collections.shuffle(listOfCommands);
            double[] cumulativeProbabilities = new double[listOfCommands.size()];
            Random rand = new Random();
            double r = rand.nextDouble();

            double cumulativeProbability = 0.0; // Probability wie Weights
            for (int i = 0; i < listOfCommands.size(); i++) {
                cumulativeProbability += this.getConfig().getDouble(listOfCommands.get(i));
                cumulativeProbabilities[i] = cumulativeProbability;
            }

            for (int i = 0; i < cumulativeProbabilities.length; i++) {
                if (r <= cumulativeProbabilities[i]/cumulativeProbability) {
                    String cmd = listOfCommands.get(i).replace("\"","").replace("%s", pID);

                    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) { // Check PlaceHolderAPI
                        cmd = PlaceholderAPI.setPlaceholders(player, cmd);
                    }

                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                    this.playerData.set(pID, times - 1);
                    this.saveFile("playerdata.yml", playerData);
                    return;
                }
            }
        }

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.messages.getString("noRemainingTimes")));
    }

    private void modifyTimes(CommandSender sender, String[] args, String operation) {
        if (args.length != 2) return;
        String pID = args[0];
        int times = Integer.parseInt(args[1]);

        switch (operation) {
            case "add":
                this.playerData.set(pID, this.playerData.getInt(pID) + times);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.messages.getString("added")) + times);
                break;

            case "remove":
                int currentTimes = this.playerData.getInt(pID);
                if (currentTimes > 0 && currentTimes >= times) {
                    this.playerData.set(pID, currentTimes - times);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.messages.getString("removed")) + times);
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.messages.getString("noRemainingTimes")));
                }
                break;

            case "set":
                this.playerData.set(pID, times);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.messages.getString("set")) + times);
                break;
        }
        this.saveFile("playerdata.yml", playerData);
    }

    private void showTimes(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("/peshow <ID>");
        } else {
            String playerId = args[0];
            int times = playerData.getInt(playerId, -1);
            if (times == -1) {
                sender.sendMessage("Player not found");
            } else {
                sender.sendMessage("Remaining times for " + playerId + ": " + times);
            }
        }
    }

    public void saveFile(String fileName, FileConfiguration fileConfiguration) {
        try {
            fileConfiguration.save(new File(getDataFolder(), fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

