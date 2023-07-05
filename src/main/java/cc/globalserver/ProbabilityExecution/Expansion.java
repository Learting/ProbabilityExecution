package cc.globalserver.ProbabilityExecution;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import cc.globalserver.ProbabilityExecution.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Expansion extends PlaceholderExpansion {
    private final Main plugin;

    public Expansion(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public String getAuthor(){
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getIdentifier(){
        return "probabilityexecution";
    }

    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier){
        if(identifier.equals("remaining_times")){
            int times = plugin.playerData.getInt(player.getName(), -1);
            return times == -1 ? null : String.valueOf(times);
        }
        return null;
    }
}

