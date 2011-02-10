package redecouverte.boatboost;

import java.io.File;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.config.Configuration;

public class BoatBoost extends JavaPlugin {

    private static final Logger logger = Logger.getLogger("Minecraft");
    private EVehicleListener mVehicleListener;
    private Configuration config;
    private BoatManager boatMgr;

    public BoatBoost(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {

        super(pluginLoader, instance, desc, folder, plugin, cLoader);

       // folder.mkdirs();

    }

    public void onEnable() {
        try {

            this.boatMgr = new BoatManager();

            PluginManager pm = getServer().getPluginManager();

            mVehicleListener = new EVehicleListener(this);
            pm.registerEvent(Type.VEHICLE_UPDATE, mVehicleListener, Priority.Normal, this);
            pm.registerEvent(Type.VEHICLE_DAMAGE, mVehicleListener, Priority.Normal, this);
            pm.registerEvent(Type.VEHICLE_COLLISION_BLOCK, mVehicleListener, Priority.Normal, this);
            pm.registerEvent(Type.VEHICLE_COLLISION_ENTITY, mVehicleListener, Priority.Normal, this);

            PluginDescriptionFile pdfFile = this.getDescription();
            logger.log(Level.INFO, pdfFile.getName() + " version " + pdfFile.getVersion() + " enabled.");
        } catch (Exception e) {
            logger.log(Level.WARNING, "BoatBoost error: " + e.getMessage() + e.getStackTrace().toString());
            e.printStackTrace();
            return;
        }
    }

    public void onDisable() {
        try {
            PluginDescriptionFile pdfFile = this.getDescription();
            logger.log(Level.INFO, pdfFile.getName() + " version " + pdfFile.getVersion() + " disabled.");
        } catch (Exception e) {
            logger.log(Level.WARNING, "BoatBoost : error: " + e.getMessage() + e.getStackTrace().toString());
            e.printStackTrace();
            return;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        try {
            String cmd = command.getName().toLowerCase();


            return false;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public BoatManager getBoatMgr()
    {
        return this.boatMgr;
    }


 
}
