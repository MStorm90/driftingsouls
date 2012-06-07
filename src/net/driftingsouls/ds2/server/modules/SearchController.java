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
package net.driftingsouls.ds2.server.modules;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import net.driftingsouls.ds2.server.bases.Base;
import net.driftingsouls.ds2.server.entities.User;
import net.driftingsouls.ds2.server.framework.Common;
import net.driftingsouls.ds2.server.framework.Configuration;
import net.driftingsouls.ds2.server.framework.Context;
import net.driftingsouls.ds2.server.framework.pipeline.Module;
import net.driftingsouls.ds2.server.framework.pipeline.generators.Action;
import net.driftingsouls.ds2.server.framework.pipeline.generators.ActionType;
import net.driftingsouls.ds2.server.framework.pipeline.generators.TemplateGenerator;
import net.driftingsouls.ds2.server.framework.templates.TemplateEngine;
import net.driftingsouls.ds2.server.ships.Ship;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Zeigt alle Objekte an, welche zu einem Suchbegriff passen.
 * @author Christopher Jung
 *
 * @urlparam String search Der Suchbegriff
 */
@Module(name="search")
public class SearchController extends TemplateGenerator {
	private static final int MAX_OBJECTS = 25;
	
	/**
	 * Konstruktor.
	 * @param context Der zu verwendende Kontext
	 */
	public SearchController(Context context) {
		super(context);

		setTemplate("search.html");
		
		addBodyParameter("style", "background-image: url('"+Configuration.getSetting("URL")+"data/interface/border/border_background.gif')");
		setDisableDebugOutput(true);
		setDisablePageMenu(true);
		
		parameterString("search");
	}
	
	@Override
	protected boolean validateAndPrepare(String action) {
		return true;	
	}

	/**
	 * AJAX-Suche mit JSON-Antwort.
	 * @throws IOException
	 */
	@Action(ActionType.AJAX)
	public void searchAction() throws IOException {
		org.hibernate.Session db = getDB();
		JSONObject json = new JSONObject();
		
		parameterString("only");
		parameterNumber("max");
		final String only = getString("only");
		final String search = getString("search");
		int max = getInteger("max");
		if( max <= 0 || max > MAX_OBJECTS ) {
			max = MAX_OBJECTS;
		}
		
		if( search.length() < 1 ) {
			getResponse().getWriter().append(json.toString());
			return;
		}
		
		int count = 0;
		
		if( only.isEmpty() || "bases".equals(only) ) {
			JSONArray baseListObj = new JSONArray();
			
			List<?> baseList = findBases(db, search, max-count);
			for( Iterator<?> iter=baseList.iterator(); iter.hasNext(); ) {
				Base base = (Base)iter.next();
				JSONObject baseObj = new JSONObject();
				
				baseObj.accumulate("id", base.getId());
				baseObj.accumulate("name", Common._plaintitle(base.getName()));
				baseObj.accumulate("location", base.getLocation().displayCoordinates(false));
				
				baseListObj.add(baseObj);
				
				count++;
			}
			
			json.accumulate("bases", baseListObj);
		}
		
		if( only.isEmpty() || "ships".equals(only) ) {
			if( count < max ) {
				JSONArray shipListObj = new JSONArray();
				
				List<?> shipList = findShips(db, search, max-count);
				for( Iterator<?> iter=shipList.iterator(); iter.hasNext(); ) {
					Ship ship = (Ship)iter.next();
					
					JSONObject shipObj = new JSONObject();
					
					shipObj.accumulate("id", ship.getId());
					shipObj.accumulate("name", Common._plaintitle(ship.getName()));
					shipObj.accumulate("location", ship.getLocation().displayCoordinates(false));
					
					JSONObject typeObj = new JSONObject();
					typeObj.accumulate("name", ship.getTypeData().getNickname());
					typeObj.accumulate("picture", ship.getTypeData().getNickname());
					
					shipObj.accumulate("type", typeObj);

					shipListObj.add(shipObj);
					
					count++;
				}
				
				json.accumulate("ships", shipListObj);
			}
		}
		
		if( only.isEmpty() || "users".equals(only) ) {
			if( count < max ) {
				JSONArray userListObj = new JSONArray();
			
				List<?> userList = findUsers(db, search, max-count);
				for( Iterator<?> iter=userList.iterator(); iter.hasNext(); ) {
					User auser = (User)iter.next();
					
					JSONObject userObj = new JSONObject();
					userObj.accumulate("id", auser.getId());
					userObj.accumulate("name", Common._title(auser.getName()));
					userObj.accumulate("plainname", auser.getPlainname());
					
					userListObj.add(userObj);
					
					count++;
				}
				
				json.accumulate("users", userListObj);
			}
		}
		
		getResponse().getWriter().append(json.toString());
	}
	
	@Override
	@Action(ActionType.DEFAULT)
	public void defaultAction() {		
		TemplateEngine t = getTemplateEngine();
		org.hibernate.Session db = getDB();

		final String search = getString("search");
		
		if( search.isEmpty() ) {
			return;
		}
		
		if( search.length() < 2 ) {
			t.setVar("objects.termtoshort", 1);
			return;
		}
		
		t.setVar("search", search);
		
		t.setBlock("_SEARCH", "base.listitem", "none");				 			
		t.setBlock("_SEARCH", "ship.listitem", "none");
		t.setBlock("_SEARCH", "user.listitem", "none");

		int count = 0;
		
		/*
			Basen
		*/
		List<?> baseList = findBases(db, search, MAX_OBJECTS-count);
		for( Iterator<?> iter=baseList.iterator(); iter.hasNext(); ) {
			Base base = (Base)iter.next();
			
			t.setVar(	"base.id",		base.getId(),
						"base.name",	Common._plaintitle(base.getName()),
						"base.location",	base.getLocation().displayCoordinates(false));

			t.parse("objects.list", "base.listitem", true);
			
			count++;
		}
		
		if( count < MAX_OBJECTS ) {
			/*
				Schiffe
			*/
			List<?> shipList = findShips(db, search, MAX_OBJECTS-count);
			for( Iterator<?> iter=shipList.iterator(); iter.hasNext(); ) {
				Ship ship = (Ship)iter.next();
				
				t.setVar(	"ship.id",		ship.getId(),
							"ship.name",	Common._plaintitle(ship.getName()),
							"ship.type.name",	ship.getTypeData().getNickname(),
							"ship.type.picture",	ship.getTypeData().getPicture(),
							"ship.location",	ship.getLocation().displayCoordinates(false));
		
				t.parse("objects.list", "ship.listitem", true);
				
				count++;
			}
		}
		
		if( count < MAX_OBJECTS ) {
			/*
				User
			*/
			List<?> userList = findUsers(db, search, MAX_OBJECTS-count);
			for( Iterator<?> iter=userList.iterator(); iter.hasNext(); ) {
				User auser = (User)iter.next();
				
				t.setVar(	"user.id",		auser.getId(),
							"user.name",	Common._title(auser.getName()));
		
				t.parse("objects.list", "user.listitem", true);
				
				count++;
			}
		}
		
		if( count >= MAX_OBJECTS ) {
			t.setVar("objects.tomany", 1);
		}
	}

	private List<?> findUsers(org.hibernate.Session db, final String search, int count)
	{
		List<?> userList = db.createQuery("from User where "+(getUser().getAccessLevel() > 20 ? "" : "locate('hide',flags)=0 and ")+
				" (plainname like :search or id like :searchid)")
			.setString("search", "%"+search+"%")
			.setString("searchid", search+"%")
			.setMaxResults(count)
			.list();
		return userList;
	}

	private List<?> findShips(org.hibernate.Session db, final String search, int count)
	{
		List<?> shipList = db.createQuery("from Ship as s left join fetch s.modules where s.owner= :user and (s.name like :search or s.id like :searchid)")
			.setEntity("user", getUser())
			.setString("search", "%"+search+"%")
			.setString("searchid", search+"%")
			.setMaxResults(count)
			.list();
		return shipList;
	}

	private List<?> findBases(org.hibernate.Session db, final String search, int count)
	{
		List<?> baseList = db.createQuery("from Base where owner= :user and (name like :search or id like :searchid)")
			.setEntity("user", getUser())
			.setString("search", "%"+search+"%")
			.setString("searchid", search+"%")
			.setMaxResults(count)
			.list();
		return baseList;
	}
}
