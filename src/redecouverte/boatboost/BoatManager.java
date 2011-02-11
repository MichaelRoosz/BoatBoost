package redecouverte.boatboost;

import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;

public class BoatManager {

    private HashMap<Integer, BoostBoat> boatMap;

    public BoatManager() {

        this.boatMap = new HashMap<Integer, BoostBoat>();
    }

    public void addBoat(BoostBoat boat) {

        BoostBoat b = this.boatMap.get(boat.getId());

        if (b != null) {
            this.boatMap.remove(boat.getId());
        }

        this.boatMap.put(boat.getId(), boat);
    }

    public BoostBoat getBoostBoat(Boat boat) {
        BoostBoat b = this.boatMap.get(boat.getEntityId());

        if (b == null) {
            b = new BoostBoat(boat);
            this.addBoat(b);
        }

        return b;
    }

    public void delBoostBoat(Boat boat) {
        this.boatMap.remove(boat.getEntityId());
    }

    public Block getBlockUnderneath(Location loc) {

        World world = loc.getWorld();
        int y = world.getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ()) - 1;

        Block block;

        int i = 0;
        do {
            block = loc.getWorld().getBlockAt(loc.getBlockX(), y, loc.getBlockZ());
            y--;
            i++;
        } while (y > 0 && i < 11
                && (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER
                || block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA));

        return block;
    }
}
