package net.driftingsouls.ds2.server.ships;

import net.driftingsouls.ds2.server.ContextCommon;
import net.driftingsouls.ds2.server.Location;
import net.driftingsouls.ds2.server.cargo.Cargo;
import net.driftingsouls.ds2.server.entities.User;
import net.driftingsouls.ds2.server.framework.Common;
import net.driftingsouls.ds2.server.framework.Context;
import net.driftingsouls.ds2.server.framework.ContextMap;
import net.driftingsouls.ds2.server.werften.ShipWerft;

import org.springframework.lang.NonNull;

public class SchiffHinzufuegenService
{
	public @NonNull Ship erstelle(@NonNull User auser, @NonNull ShipType shiptype, @NonNull Location loc) {
		return erstelle(auser, shiptype, loc, "");
	}

	public @NonNull Ship erstelle(@NonNull User auser, @NonNull ShipType shiptype, @NonNull Location loc, @NonNull String historiendaten) {
		Context context = ContextMap.getContext();
		org.hibernate.Session db = context.getDB();

		String currentTime = Common.getIngameTime(context.get(ContextCommon.class).getTick());
		String history = "Indienststellung am "+currentTime+" durch "+auser.getName()+" ("+auser.getId()+")"+(historiendaten.isEmpty() ? "" : "" + historiendaten);

		Ship ship = new Ship(auser, shiptype, loc.getSystem(), loc.getX(), loc.getY());
		ship.getHistory().addHistory(history);
		ship.setCrew(shiptype.getCrew());
		ship.setHull(shiptype.getHull());
		ship.setCargo(new Cargo());
		ship.setEnergy(shiptype.getEps());
		ship.setName((auser.getSchiffsKlassenNamenGenerator().generiere(shiptype.getShipClass()) + " " + auser.getSchiffsNamenGenerator().generiere(shiptype)).trim());
		ship.setEngine(100);
		ship.setWeapons(100);
		ship.setComm(100);
		ship.setSensors(100);
		ship.setAblativeArmor(shiptype.getAblativeArmor());

		db.save(ship);

		if( shiptype.getWerft() != 0 ) {
			ShipWerft awerft = new ShipWerft(ship);
			db.persist(awerft);
		}

		ship.recalculateShipStatus();

		return ship;
	}
}
