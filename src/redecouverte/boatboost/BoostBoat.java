package redecouverte.boatboost;

import java.util.logging.Logger;
import net.minecraft.server.EntityBoat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftBoat;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Boat;

import org.bukkit.util.Vector;

public class BoostBoat {

    private Boat boat;
    private Location lastLocation;
    private static final Logger logger = Logger.getLogger("Minecraft");
    private long lastWoodSearchTick;
    private boolean foundWood;
    private static double maxSpeed = 0.4D;

    public BoostBoat(Boat boat) {
        this.boat = boat;
        this.lastLocation = boat.getLocation().clone();
        this.lastWoodSearchTick = 0;
        this.foundWood = false;
    }

    public boolean equalsBoatLastLocation(Location l) {

        if (l.getBlockX() != this.lastLocation.getBlockX()) {
            return false;
        }
        if (l.getBlockY() != this.lastLocation.getBlockY()) {
            return false;
        }
        if (l.getBlockZ() != this.lastLocation.getBlockZ()) {
            return false;
        }

        if (l.getWorld().getId() != this.lastLocation.getWorld().getId()) {
            return false;
        }

        return true;

    }

    public int getId() {
        return this.boat.getEntityId();
    }

    public boolean isMoving() {
        Vector vel = this.boat.getVelocity();
        return vel.getX() != 0D || vel.getY() != 0D || vel.getZ() != 0D;
    }

    public static double forceRange(double a) {
        if (Math.abs(a) >= maxSpeed) {
            if (a < 0D) {
                a = -maxSpeed;
            } else {
                a = maxSpeed;
            }
        }
        return a;
    }

    public void setVelocity(double x, double y, double z) {
        Vector newVel = new Vector(forceRange(x), y, forceRange(z));
        this.boat.setVelocity(newVel);
    }

    public void setVelocityX(double x) {
        Vector newVel = new Vector(forceRange(x), this.boat.getVelocity().getY(), this.boat.getVelocity().getZ());
        this.boat.setVelocity(newVel);
    }

    public void setVelocityY(double y) {
        Vector newVel = new Vector(this.boat.getVelocity().getX(), y, this.boat.getVelocity().getZ());
        this.boat.setVelocity(newVel);
    }

    public void setVelocityZ(double z) {
        Vector newVel = new Vector(this.boat.getVelocity().getX(), this.boat.getVelocity().getY(), forceRange(z));
        this.boat.setVelocity(newVel);
    }

    public Vector getVelocity() {
        return this.boat.getVelocity().clone();
    }

    public double getVelocityX() {
        return this.boat.getVelocity().getX();
    }

    public double getVelocityY() {
        return this.boat.getVelocity().getY();
    }

    public double getVelocityZ() {
        return this.boat.getVelocity().getZ();
    }

    public boolean foundWood()
    {
        return this.foundWood;
    }

    public boolean damageDestroyesBoat(int damage) {
        CraftBoat cb = (CraftBoat) this.boat;
        EntityBoat eb = (EntityBoat) cb.getHandle();

        int damageTaken = eb.a;

        if (damageTaken + damage * 10 > 40) {
            return true;
        }

        return false;
    }

    public void addForce(double pitch, double yaw, double power) {

        double x = Math.cos(pitch) * Math.cos(yaw) * power;
        double z = Math.cos(pitch) * Math.sin(yaw) * power;
        double y = Math.sin(pitch) * power;

        this.setVelocity(this.getVelocityX() + x, this.getVelocityY() + y, this.getVelocityZ() + z);
    }

    public void findWood()
    {
        int curX =  this.boat.getLocation().getBlockX();
        int curY =  this.boat.getLocation().getBlockY();
        int curZ =  this.boat.getLocation().getBlockZ();

        for (int x = curX - 5; x <= curX + 5; x++) {
            for (int y = curY; y <= curY + 1; y++) {
                for (int z = curZ - 5; z <= curZ + 5; z++) {
                    if(y < 1 || y > 126)
                    {
                        continue;
                    }

                    Block b = this.boat.getWorld().getBlockAt(x, y, z);

                    if(b.getType() == Material.WOOD)
                    {
                      int deltaX = Math.abs(curX - x);
                      int deltaZ = Math.abs(curZ - z);
                      if(deltaX < 2 && deltaZ < 2)
                      {
                          this.foundWood = true;
                          this.setVelocity(0D, 0D, 0D);
                          return;
                      }
                      this.foundWood = true;
                      this.navigateTo(x, y , z);
                      return;
                    }
                }
            }
        }
    }

    public boolean isInLava()
    {
        Location l = this.boat.getLocation();
        Block b = this.boat.getWorld().getBlockAt(l.getBlockX(), l.getBlockY() - 1, l.getBlockZ());

        if(b.getType() == Material.LAVA || b.getType() == Material.STATIONARY_LAVA)
        {
            return true;
        }

        return false;
    }


    public void navigateTo(double x, double y, double z) {
        double lX = this.boat.getLocation().getX();
        double lZ = this.boat.getLocation().getZ();

        double vx = 0D;
        double vz = 0D;

        if (lX < x) {
            vx = 0.02D;
        } else {
            vx = -0.02D;
        }
        if (lZ < z) {
            vz = 0.02D;
        } else {
            vz = -0.02D;
        }
        this.setVelocity(vx, 0.0D, vz);
    }

    public boolean isBlockUnderneathIndirectlyPowered(Block b)
    {
        int y = b.getY() - 1;

        if (y < 1) {
            return false;
        }

        Block bu = b.getWorld().getBlockAt(b.getX(), b.getY(), b.getZ());

        if(bu == null)
        {
            return false;
        }

        return bu.isBlockIndirectlyPowered();
    }


    public boolean isOnGround() {

        EntityBoat be = (EntityBoat) ((CraftBoat) this.boat).getHandle();
        return be.onGround;
    }

    public void onPassingBlock(Block b) {

        if (!this.boat.isEmpty()) {
            this.foundWood = false;

            CraftEntity ce = (CraftEntity) this.boat.getPassenger();
            Vector pv = ce.getVelocity().clone();

            double px = pv.getX();
            double pz = pv.getZ();

            if (px != 0D || pz != 0D) {

                if (this.isOnGround()) {
                    this.setVelocity(px * 3, this.getVelocityY(), pz * 3);
                }

                double curX = this.getVelocityX();
                double curZ = this.getVelocityZ();

                boolean boostSteering = false;

                if ((px < 0 && curX > 0) || (px > 0 && curX < 0)) {
                    boostSteering = true;
                }
                if (!boostSteering && (pz < 0 && curZ > 0) || (pz > 0 && curZ < 0)) {
                    boostSteering = true;
                }

                if (boostSteering) {
                    curX = curX / 1.5D + px;
                    curZ = curZ / 1.5D + pz;
                    this.setVelocity(curX, this.getVelocityY(), curZ);
                }
            }

        } else if (System.currentTimeMillis() - this.lastWoodSearchTick > 7 * 1000) {
            this.findWood();
            this.lastWoodSearchTick = System.currentTimeMillis();
        }

        this.lastLocation = this.boat.getLocation().clone();

        if (isBlockUnderneathIndirectlyPowered(b)) {

            switch (b.getType()) {
                case OBSIDIAN: {
                    Location l = this.boat.getLocation();
                    this.addForce(l.getPitch(), l.getYaw(), 0.2D);
                    break;
                }
                default: {
                    break;
                }
            }

            return;
        }

        switch (b.getType()) {
            case GOLD_BLOCK: {
                this.boostXZByFactor(8);
                break;
            }
            case GOLD_ORE: {
                this.boostXZByFactor(2);
                break;
            }
            case SOUL_SAND: {
                this.setVelocity(this.getVelocityX() / 8, this.getVelocityY(), this.getVelocityZ() / 8);
                break;
            }
            case WORKBENCH: {
                this.setVelocity(this.getVelocityX() / 2, this.getVelocityY(), this.getVelocityZ() / 2);
                break;
            }
            case OBSIDIAN: {
                this.setVelocity(0D, 0D, 0D);
                break;
            }
            case IRON_BLOCK: {
                this.boat.eject();
            }
            case WOOL: {
                this.setVelocity(this.getVelocityX() * -1, this.getVelocityY() * -1, this.getVelocityZ() * -1);
            }
            case LAPIS_BLOCK: {
                this.setVelocityY(1.0D);
                break;
            }
            case LAPIS_ORE: {
                this.setVelocityY(0.5D);
                break;
            }
            default:
                return;
        }



    }

    public void boostXZByFactor(int factor) {

        double curX = this.getVelocityX();
        double curZ = this.getVelocityZ();

        double newX = curX * factor;
        if (Math.abs(newX) > 0.4D) {
            if (newX < 0) {
                newX = -0.4D;
            } else {
                newX = 0.4D;
            }
            double newZ = 0D;;
            if(curZ != 0D)
            {
                newZ = 0.4D / Math.abs(curX / curZ);
                if(curZ < 0)
                {
                    newZ *= -1;
                }
            }
            this.setVelocity(newX, this.getVelocityY(), newZ);
            return;
        }

        double newZ = curZ * factor;
        if (Math.abs(newZ) > 0.4D) {
            if (newZ < 0) {
                newZ = -0.4D;
            } else {
                newZ = 0.4D;
            }
            newX = 0D;
            if(curX != 0D)
            {
                newX = 0.4D / (curZ / curX);
                if(curX < 0)
                {
                    newX *= -1;
                }
            }
            this.setVelocity(newX, this.getVelocityY(), newZ);
            return;
        }

        this.setVelocity(newX, this.getVelocityY(), newZ);
    }
}
