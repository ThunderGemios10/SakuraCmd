/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands.player
 * Created: 2013/01/12 17:49:00
 */
package net.syamn.sakuracmd.commands.player;

import net.syamn.sakuracmd.SCHelper;
import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.sakuracmd.manager.Worlds;
import net.syamn.sakuracmd.permission.Perms;
import net.syamn.sakuracmd.player.PlayerManager;
import net.syamn.sakuracmd.player.Power;
import net.syamn.sakuracmd.player.SakuraPlayer;
import net.syamn.sakuracmd.utils.plugin.SakuraCmdUtil;
import net.syamn.sakuracmd.worker.FlymodeWorker;
import net.syamn.utils.LogUtil;
import net.syamn.utils.Util;
import net.syamn.utils.economy.EconomyUtil;
import net.syamn.utils.exception.CommandException;
import net.syamn.utils.queue.ConfirmQueue;
import net.syamn.utils.queue.Queueable;
import net.syamn.utils.queue.QueuedCommand;

/**
 * FlymodeCommand (FlymodeCommand.java)
 * @author syam(syamn)
 */
public class FlymodeCommand extends BaseCommand implements Queueable{
    public FlymodeCommand(){
        bePlayer = true;
        name = "flymode";
        perm = Perms.FLYMODE;
        argLength = 0;
        usage = "<- buy flymode";
    }
    
    private double getCost(){
        return 5000.0D; //TODO configuable
    }
    
    private int getDuration(){
        return 2; //TODO configuable
    }

    public void execute() throws CommandException{
        final SakuraPlayer sp = PlayerManager.getPlayer(player);
        if (sp.hasPower(Power.FLY)){
            throw new CommandException("&cあなたは飛行モード(fly)が有効です");
        }
        if (sp.hasPower(Power.FLYMODE)){
            throw new CommandException("&cあなたは既に飛行権限を購入しています");
        }
        
        ConfirmQueue.getInstance().addQueue(sender, this, null, 15);
        Util.message(sender, "&6飛行権限を購入しようとしています！");
        Util.message(sender, "&6現在の価格は &a" + getDuration() + "分間 " + getCost() + " Coin &6です");
        Util.message(sender, "&6本当に購入しますか？ &a/confirm&6 コマンドで続行します。");
    }
    
    @Override
    public void executeQueue(QueuedCommand queued){
        if (!Worlds.isFlyAllowed(player.getWorld().getName())){
            Util.message(sender, "&cこのワールドでは飛行が許可されていません！");
            return;
        }
        if (player.getLocation().getY() > 257 || player.getLocation().getY() < 0){
            Util.message(sender, "&cあなたの座標からこのコマンドは使えません！");
            return;
        }
        
        // pay cost
        if (!SCHelper.getInstance().isEnableEcon()){
            Util.message(sender, "&c経済システムが動作していないため使えません！");
            return;
        }
        
        final SakuraPlayer sp = PlayerManager.getPlayer(player);
        
        double cost = getCost();
        boolean paid = EconomyUtil.takeMoney(player, cost);
        if (!paid){
            Util.message(sender, "&cお金が足りません！ " + cost + "Coin必要です！");
            return;
        }
        
        int minute = getDuration();
        
        final FlymodeWorker worker = FlymodeWorker.getInstance();
        worker.enableFlymode(sp, minute);
        
        Util.message(player, "&a飛行モードが " + minute + "分間 有効になりました！");
        LogUtil.info(player.getName() + " is bought flying mode: " + minute + " minutes for " + cost + " coins");
        SakuraCmdUtil.sendlog(player, sp.getName() + " &aが飛行権限を購入しました");
    }
}