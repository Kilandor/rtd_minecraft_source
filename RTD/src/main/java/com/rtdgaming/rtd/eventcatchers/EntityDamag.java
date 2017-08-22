package com.rtdgaming.rtd.eventcatchers;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.*;

import com.kilandor.chat.Chat;

import com.rtdgaming.rtd.RTD;
import com.rtdgaming.rtd.Roller;
import com.rtdgaming.rtd.rolls.*;

public class EntityDamag extends EntityListener
{
	@SuppressWarnings("unused")
	private final RTD plugin;
	private Chat chat;

	public EntityDamag(RTD instance)
	{
		plugin = instance;
		chat = instance.getChat();
	}

	@Override
	public void onEntityDamage(EntityDamageEvent event)
	{
		if(event.getEntity() instanceof Player)
		{
			Player p = (Player) event.getEntity();

			switch(event.getCause())
			{
				case FALL:
					//Fall immunity check
					if(Roller.getRoller().isActiveRoll(p, _RollsEnum.FALL_IMMUNITY))
					{
						if(event.getDamage() >= 13)
							chat.playerMsg(p, RTD.CHATTITLE, FallImmunity.activateText, false);
						event.setCancelled(true);
					}
					break;

				case DROWNING:
					//Water breathing check
					if(Roller.getRoller().isActiveRoll(p, _RollsEnum.WATER_BREATHING))
						event.setCancelled(true);
					break;

				case FIRE:
				case FIRE_TICK:
				case LAVA:
					//Fire immunity check
					if(Roller.getRoller().isActiveRoll(p, _RollsEnum.FIRE_IMMUNITY)) {
						p.setFireTicks(0);
						event.setCancelled(true);
					}
					break;
			}
		}
	}

	/* TODO: Fire Immunity is on the back burner */
	@Override
	public void onEntityCombust(EntityCombustEvent event)
	{
		if(event.getEntity() instanceof Player)
		{
			Player p = (Player) event.getEntity();

			if(Roller.getRoller().isActiveRoll(p, _RollsEnum.FIRE_IMMUNITY))
				event.setCancelled(true);
		}
	}
}
