package redecouverte.boatboost;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleEvent;
import org.bukkit.event.vehicle.VehicleListener;



public class EVehicleListener extends VehicleListener {

    private static final Logger logger = Logger.getLogger("Minecraft");
    private BoatBoost parent;
    private BoatManager boatMgr;

    public EVehicleListener(BoatBoost parent) {
        this.parent = parent;
        this.boatMgr = parent.getBoatMgr();
    }


    @Override
    public void onVehicleUpdate(VehicleEvent event) {

        try {
            if(!(event.getVehicle() instanceof Boat))
            {
                return;
            }

            Boat bukkitBoat = (Boat)event.getVehicle();
            BoostBoat boat = this.boatMgr.getBoostBoat(bukkitBoat);
            Block bu = this.boatMgr.getBlockUnderneath(bukkitBoat.getLocation());

            if(bu.getType() != Material.OBSIDIAN && !boat.isOnGround() &&(!boat.isMoving() || boat.equalsBoatLastLocation(bukkitBoat.getLocation())))
            {
                return;
            }

            boat.onPassingBlock(bu);
     

        } catch (Exception e) {
            logger.log(Level.WARNING, "BoatBoost: error: " + e.getMessage());
            e.printStackTrace();
            return;
        }
    }
 
    @Override
    public void onVehicleDamage(VehicleDamageEvent  event) {

        try {
            if(event.isCancelled())
            {
                return;
            }

            if(!(event.getVehicle() instanceof Boat))
            {
                return;
            }

            Boat bukkitBoat = (Boat)event.getVehicle();
            BoostBoat boat = this.boatMgr.getBoostBoat(bukkitBoat);

            if(boat.damageDestroyesBoat(event.getDamage()))
            {
                this.boatMgr.delBoostBoat(bukkitBoat);
            }

            if(boat.isInLava())
            {
                event.setCancelled(true);
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, "BoatBoost: error: " + e.getMessage());
            e.printStackTrace();
            return;
        }        
    }

    @Override
    public void onVehicleBlockCollision(VehicleBlockCollisionEvent event)
    {
         try {

            if(!(event.getVehicle() instanceof Boat))
            {
                return;
            }

            Boat bukkitBoat = (Boat)event.getVehicle();
            BoostBoat boat = this.boatMgr.getBoostBoat(bukkitBoat);

             if (boat.foundWood() && event.getBlock().getType() == Material.WOOD) {
                 boat.setVelocity(0D, 0D, 0D);
             } 
     
        } catch (Exception e) {
            logger.log(Level.WARNING, "BoatBoost: error: " + e.getMessage());
            e.printStackTrace();
            return;
        }
    }

    @Override
    public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {
        try {

            if (!(event.getVehicle() instanceof Boat)) {
                return;
            }

            Boat bukkitBoat = (Boat) event.getVehicle();
            BoostBoat boat = this.boatMgr.getBoostBoat(bukkitBoat);

            Entity e = event.getEntity();

            if (!(e instanceof LivingEntity)) {

                if (e instanceof Boat) {
                    Boat bukkitBoat2 = (Boat) e;
                    BoostBoat boat2 = this.boatMgr.getBoostBoat(bukkitBoat2);

                    if (boat.foundWood()) {
                        boat.setVelocity(0D, 0D, 0D);
                    }
                    if (boat2.foundWood()) {
                        boat2.setVelocity(0D, 0D, 0D);
                    }

                    event.setCancelled(true);
                    event.setCollisionCancelled(true);
                    event.setPickupCancelled(true);
                }

                return;
            }

            if (e instanceof Player) {
                return;
            }

            if (!boat.isMoving()) {
                return;
            }

            LivingEntity le = (LivingEntity) e;
            le.setHealth(0);

            event.setCancelled(true);
            event.setCollisionCancelled(true);
            event.setPickupCancelled(true);


        } catch (Exception e) {
            logger.log(Level.WARNING, "BoatBoost: error: " + e.getMessage());
            e.printStackTrace();
            return;
        }
    }
}
