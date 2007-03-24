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

import java.util.List;

import net.driftingsouls.ds2.server.ContextCommon;
import net.driftingsouls.ds2.server.battles.Battle;
import net.driftingsouls.ds2.server.framework.Common;
import net.driftingsouls.ds2.server.framework.Context;
import net.driftingsouls.ds2.server.framework.ContextMap;
import net.driftingsouls.ds2.server.framework.db.Database;
import net.driftingsouls.ds2.server.framework.db.SQLResultRow;
import net.driftingsouls.ds2.server.ships.ShipTypes;

/**
 * Laesst ein Schiff in die zweite Reihe fliegen
 * @author Christopher Jung
 *
 */
public class KSSecondRowAction extends BasicKSAction {
	/**
	 * Konstruktor
	 *
	 */
	public KSSecondRowAction() {
		this.requireOwnShipReady(true);
		this.requireAP(1);
	}
	
	@Override
	public int validate(Battle battle) {
		Context context = ContextMap.getContext();
		Database db = context.getDatabase();
		SQLResultRow ownShip = battle.getOwnShip();
		
		if( (ownShip.getInt("action") & Battle.BS_SECONDROW) != 0 || (ownShip.getInt("action") & Battle.BS_DESTROYED) != 0 ||
			( ownShip.getInt("engine") == 0 ) || ownShip.getString("docked").length() > 0 || (ownShip.getInt("action") & Battle.BS_FLUCHT) != 0 ||
			( ownShip.getInt("action") & Battle.BS_JOIN ) != 0 ) {
			return RESULT_ERROR;
		}
		
		if( (ownShip.getInt("action") & Battle.BS_SECONDROW_BLOCKED) != 0 ) {
			return RESULT_ERROR;
		}
		
		if( (battle.getOwnSide() == 0) && battle.hasFlag(Battle.FLAG_DROP_SECONDROW_0) ) {
			return RESULT_ERROR;
		}
		
		if( (battle.getOwnSide() == 1) && battle.hasFlag(Battle.FLAG_DROP_SECONDROW_1) ) {
			return RESULT_ERROR;
		}
		
		if( (battle.getOwnSide() == 0) && battle.hasFlag(Battle.FLAG_BLOCK_SECONDROW_0) ) {
			return RESULT_ERROR;
		}
		
		if( (battle.getOwnSide() == 1) && battle.hasFlag(Battle.FLAG_BLOCK_SECONDROW_1) ) {
			return RESULT_ERROR;
		}
		
		if( ownShip.getBoolean("battleAction") ) {
			return RESULT_ERROR;	
		}
	
		int curr_engines = db.first("SELECT engine FROM battles_ships WHERE shipid=",ownShip.getInt("id")).getInt("engine");
		
		if( curr_engines <= 0 ) {
			return RESULT_ERROR;
		} 
		
		SQLResultRow ownShipType = ShipTypes.getShipType( ownShip );
		
		if( !ShipTypes.hasShipTypeFlag(ownShipType, ShipTypes.SF_SECONDROW) ) {
			return RESULT_ERROR;
		}
		
		if( ownShipType.getInt("cost") == 0 ) {
			return RESULT_ERROR;
		}
		 
		if( (ownShipType.getInt("crew") > 0) && (ownShip.getInt("crew") == 0) ) {
			return RESULT_ERROR;
		}
		
		boolean gotone = false;
		if( ShipTypes.hasShipTypeFlag(ownShipType, ShipTypes.SF_DROHNE) ) {
			List<SQLResultRow> ownShips = battle.getOwnShips();
			for( int i=0; i < ownShips.size(); i++ ) {
				SQLResultRow aship = ownShips.get(i);
				SQLResultRow ashiptype = ShipTypes.getShipType(aship);
				if( ShipTypes.hasShipTypeFlag(ashiptype, ShipTypes.SF_DROHNEN_CONTROLLER) ) {
					gotone = true;
					break;	
				}
			}
		}
		else {
			gotone = true;	
		}
		
		if( !gotone ) {
			return RESULT_ERROR;
		}
		
		return RESULT_OK;
	}

	@Override
	public int execute(Battle battle) {
		int result = super.execute(battle);
		if( result != RESULT_OK ) {
			return result;
		}
		
		if( this.validate(battle) != RESULT_OK ) {
			battle.logme("Die Aktion kann nicht ausgef&uuml;hrt werden");
			return RESULT_ERROR;
		}
		
		Context context = ContextMap.getContext();
		Database db = context.getDatabase();
		SQLResultRow ownShip = battle.getOwnShip();
		
		battle.logenemy("<action side=\""+battle.getOwnSide()+"\" time=\""+Common.time()+"\" tick=\""+context.get(ContextCommon.class).getTick()+"\"><![CDATA[\n");
		battle.setPoints(battle.getOwnSide(), battle.getPoints(battle.getOwnSide())-1);
		battle.save(false);
		
		battle.logme( ownShip.getString("name")+" fliegt in die zweite Reihe\n" );
		battle.logenemy( Battle.log_shiplink(ownShip)+" fliegt in die zweite Reihe\n" );
		
		int action = ownShip.getInt("action");
		
		action |= Battle.BS_SECONDROW;
		action |= Battle.BS_SECONDROW_BLOCKED;
		action |= Battle.BS_BLOCK_WEAPONS;
		
		db.update("UPDATE battles_ships SET action=",action," WHERE shipid=",ownShip.getInt("id"));
		ownShip.put("action", action);

		int remove = 1;
		List<SQLResultRow> ownShips = battle.getOwnShips();
		for( int i=0; i < ownShips.size(); i++ ) {
			SQLResultRow s = ownShips.get(i);
			
			if( s.getString("docked").equals(Integer.toString(ownShip.getInt("id"))) ) {
				s.put("action", s.getInt("action") | Battle.BS_SECONDROW | Battle.BS_SECONDROW_BLOCKED);
				
				db.update("UPDATE battles_ships SET action="+s.getInt("action")," WHERE shipid=",s.getInt("id"));
							
				remove++;
			}
		}
		
		if( remove > 1 ) {
			battle.logme( (remove-1)+" an "+ownShip.getString("name")+" gedockte Schiffe fliegen in die zweite Reihe\n" );
		}
		
		battle.logenemy("]]></action>\n");
	
		return RESULT_OK;
	}
}
