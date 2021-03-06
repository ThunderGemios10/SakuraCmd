/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands.items
 * Created: 2012/12/29 6:11:48
 */
package net.syamn.sakuracmd.commands.items;

import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.sakuracmd.permission.Perms;
import net.syamn.sakuracmd.player.PlayerManager;
import net.syamn.utils.ItemUtil;
import net.syamn.utils.Util;
import net.syamn.utils.exception.CommandException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * RepairAllCommand (RepairAllCommand.java)
 * @author syam(syamn)
 */
public class RepairAllCommand extends BaseCommand{
    public RepairAllCommand(){
        bePlayer = false;
        name = "repairall";
        perm = Perms.REPAIRALL;
        argLength = 0;
        usage = "[player] <- repair your all items";
    }

    @Override
    public void execute() throws CommandException{
        if (args.size() == 0 && !isPlayer){
            throw new CommandException("&cプレイヤー名を指定してください！");
        }

        final Player target = (args.size() > 0) ? Bukkit.getPlayer(args.get(0)) : player;
        if (target == null || !target.isOnline()){
            throw new CommandException("&cプレイヤーが見つかりません！");
        }

        for (final ItemStack item : target.getInventory().getContents()){
            if (item != null && ItemUtil.repairable(item.getTypeId())){ // TODO add repairable check
                item.setDurability((short) 0);
            }
        }
        for (final ItemStack item : target.getInventory().getArmorContents()){
            if (item != null){
                item.setDurability((short) 0);
            }
        }

        if (!sender.equals(target)){
            Util.message(sender, "&a" + PlayerManager.getPlayer(target).getName() + " &aの全アイテムが修復されました");
        }
        Util.message(target, "&aあなたの全アイテムが修復されました");
    }
}