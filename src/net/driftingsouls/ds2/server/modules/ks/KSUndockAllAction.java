/*
 *	Drifting Souls 2
 *	Copyright (c) 2006 Christopher Jung
 *
 *	This library is free software; you can redistribute it and/or
 *	modify it under the terms of the GNU Lesser General Public
 *	License as published by the Free Software Foundation; either
 *	version 2.1 of the License, or (at your option) any later version.
 *
 *	This library is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *	Lesser General Public License for more details.
 *
 *	You should have received a copy of the GNU Lesser General Public
 *	License along with this library; if not, write to the Free Software
 *	Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.driftingsouls.ds2.server.modules.ks;

import net.driftingsouls.ds2.server.ContextCommon;
import net.driftingsouls.ds2.server.battles.Battle;
import net.driftingsouls.ds2.server.battles.BattleShip;
import net.driftingsouls.ds2.server.framework.Common;
import net.driftingsouls.ds2.server.framework.Context;
import net.driftingsouls.ds2.server.framework.ContextMap;
import net.driftingsouls.ds2.server.framework.templates.TemplateEngine;
import net.driftingsouls.ds2.server.ships.Ship;
import net.driftingsouls.ds2.server.ships.ShipTypeData;
import net.driftingsouls.ds2.server.ships.ShipTypeFlag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Dockt alle Schiffe vom gerade ausgewaehlten Schiff ab.
 * @author Christopher Jung
 *
 */
public class KSUndockAllAction extends BasicKSAction {
	/**
	 * Konstruktor.
	 *
	 */
	public KSUndockAllAction() {
	}

    /**
     * Prueft, ob das Schiff gestartet werden soll oder nicht.
     * @param ship Das Schiff
     * @param shiptype Der Schiffstyp
     * @return <code>true</code>, wenn das Schiff gestartet werden soll
     */
    protected boolean validateShipExt( BattleShip ship, ShipTypeData shiptype ) {
        // Extension Point
        return true;
    }

	@Override
	public Result validate(Battle battle) {
		BattleShip ownShip = battle.getOwnShip();
		org.hibernate.Session db = ContextMap.getContext().getDB();

		boolean dock = db.createQuery("from Ship where docked in (:docked,:landed)")
			.setString("landed", "l "+ownShip.getId())
			.setString("docked", Integer.toString(ownShip.getId()))
			.iterate().hasNext();

		if( dock ) {
			return Result.OK;
		}
		return Result.ERROR;
	}

	@Override
	public Result execute(TemplateEngine t, Battle battle) throws IOException {
		Result result = super.execute(t, battle);
		if( result != Result.OK ) {
			return result;
		}

		if( this.validate(battle) != Result.OK ) {
			battle.logme( "Validation failed\n" );
			return Result.ERROR;
		}

		Context context = ContextMap.getContext();
		org.hibernate.Session db = context.getDB();
		BattleShip ownShip = battle.getOwnShip();

		battle.logenemy("<action side=\""+battle.getOwnSide()+"\" time=\""+Common.time()+"\" tick=\""+context.get(ContextCommon.class).getTick()+"\"><![CDATA[\n");

		ownShip.getShip().setBattleAction(true);

        List<Ship> startList = new ArrayList<>();
        List<Ship> undockList = new ArrayList<>();

        for(BattleShip aship : battle.getOwnShips())
        {
            if(!validateShipExt(aship, aship.getTypeData()))
            {
                continue;
            }

            if(aship.getShip().getBaseShip() != null && aship.getShip().getBaseShip().getId() == ownShip.getShip().getId())
            {
                if(aship.getShip().isLanded())
                {
                    startList.add(aship.getShip());
                }
                else
                {
                    undockList.add(aship.getShip());
                }
            }
        }

        Ship[] startArray = new Ship[startList.size()];
        Ship[] undockArray = new Ship[undockList.size()];
        startList.toArray(startArray);
        undockList.toArray(undockArray);

        ownShip.getShip().start(startArray);
        ownShip.getShip().undock(undockArray);

		List<BattleShip> ownShips = battle.getOwnShips();
		for (BattleShip s : ownShips)
		{
			if ((s.getAction() & Battle.BS_SECONDROW) != 0 && !s.getTypeData().hasFlag(ShipTypeFlag.SECONDROW))
			{
				s.setAction((s.getAction() ^ Battle.BS_SECONDROW) | Battle.BS_SECONDROW_BLOCKED);
			}
		}

		battle.logme((startList.size()+undockList.size())+" Schiffe wurden abgedockt");
		battle.logenemy((startList.size()+undockList.size())+" Schiffe wurden von der "+Battle.log_shiplink(ownShip.getShip())+" abgedockt\n");

		battle.logenemy("]]></action>\n");

		ownShip.getShip().recalculateShipStatus();

		return Result.OK;
	}
}
