package com.github.jakz.romlib.parsers;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameClone;
import com.github.jakz.romlib.data.game.Location;
import com.github.jakz.romlib.data.set.CloneSet;
import com.github.jakz.romlib.data.set.GameMap;
import com.pixbits.lib.io.xml.XMLEmbeddedDTD;
import com.pixbits.lib.io.xml.XMLHandler;
import com.pixbits.lib.io.xml.XMLParser;
import com.pixbits.lib.lang.Pair;
import com.pixbits.lib.log.Log;
import com.pixbits.lib.log.Logger;

public class XMDBHandler extends XMLHandler<CloneSet>
{
  private final static Map<String, Location> zoneMap = new HashMap<>();
  
  static
  {
    zoneMap.put("J", Location.JAPAN);
    zoneMap.put("U", Location.USA);
    zoneMap.put("E", Location.EUROPE);
    zoneMap.put("S", Location.SPAIN);
    zoneMap.put("G", Location.GERMANY);
    zoneMap.put("F", Location.FRANCE);
    zoneMap.put("I", Location.ITALY);
    zoneMap.put("A", Location.AUSTRALIA);
    zoneMap.put("Ne", Location.NETHERLANDS);
    zoneMap.put("Da", Location.DENMARK);
    zoneMap.put("Sw", Location.SWEDEN);
    zoneMap.put("K", Location.KOREA);
    zoneMap.put("Cn", Location.CHINA);
    zoneMap.put("Ca", Location.CANADA);
    zoneMap.put("Br", Location.BRASIL);
    zoneMap.put("As", Location.ASIA);
    zoneMap.put("Th", Location.TAIWAN);
    zoneMap.put("No", Location.NORWAY);
    zoneMap.put("Hr", Location.CROATIA);
    zoneMap.put("Pr", Location.PORTUGAL);
    zoneMap.put("Ru", Location.RUSSIA);
    zoneMap.put("Pl", Location.POLAND);
    zoneMap.put("Gr", Location.GREECE);
  }
  
  
  private final static Logger logger = Log.getLogger(XMDBHandler.class);
  
  
  
  
  List<GameClone> clones;
  GameMap list;
  List<Game> clone;
  Map<String, String> attributes;
  List<Pair<Location,String>> biases;
  
  public XMDBHandler(GameMap list)
  {
    this.list = list;
  }
  
  @Override protected void init()
  {
    
  }
  
  @Override protected void end(String ns, String name)
  {
    if (name.equals("zoned"))
    {
      /* if there is only a bias zone then we expect the name to be the same for the only clone */
      if (biases.size() == 1)
      {
        Pair<Location, String> entry = biases.get(0);
        
        if (clone.size() != 1)
        {
          logger.w("Clone %s with a single bias zone which doesn't have a single game specified", entry.second);
          return;
        }
        else if (!clone.get(0).getTitle().equals(entry.second))
        {
          logger.w("Single bias zone requires that the clone has exactly the same name (%s != %s)", entry.second, clone.get(0).getTitle());
          return;
        }
        
        clones.add(new GameClone(clone.get(0), entry.first, entry.second));
      }
      else
      {
        String[] zones = new String[Location.values().length];
        
        for (Pair<Location, String> entry : biases)
          zones[entry.first.ordinal()] = entry.second;

        clones.add(new GameClone(clone, zones));
      }
    }
  }
  
  @Override protected void start(String ns, String name, Attributes attr) throws SAXException
  {
    if (name.equals("set"))
    {
      attributes = new HashMap<>();

      if (attr.getValue("name") != null)
        attributes.put("name", attr.getValue("name"));
      if (attr.getValue("version") != null)
        attributes.put("version", attr.getValue("version"));
    }
    else if (name.equals("parents"))
    {
      clones = new ArrayList<>();
    }
    else if (name.equals("zoned"))
    {
      clone = new ArrayList<>();
      biases = new ArrayList<>();
    }
    else if (name.equals("bias"))
    {
      Location zone = zoneMap.get(attrString("zone"));
      
      if (zone == null)
        throw new NoSuchElementException("zone found in zoned rom '"+attrString("name")+"' is not valid: "+attrString("zone"));
      
      biases.add(new Pair<>(zone, attrString("name")));
    }
    else if (name.equals("clone"))
    {
      Game game = list.get(attrString("name"));

      if (game == null)
      {
        logger.w("Game clone '"+attrString("name")+"' is not present in corresponding game set");
        return;
      }
              
      clone.add(game);
    }
  }

  @Override public CloneSet get()
  {
    return new CloneSet(clones.toArray(new GameClone[clones.size()]), attributes);
  }
  
  public static CloneSet loadCloneSet(GameMap list, Path path) throws IOException, SAXException
  {
    final String dtdName = "GoodMerge.dtd";
    final String packageName = MethodHandles.lookup().lookupClass().getPackage().getName().replaceAll("\\.", "/");
    
    XMLEmbeddedDTD resolver = new XMLEmbeddedDTD(dtdName, packageName + "/" + dtdName);    
    XMDBHandler xparser = new XMDBHandler(list);
    XMLParser<CloneSet> xmdbParser = new XMLParser<>(xparser, resolver);

    CloneSet cloneSet = xmdbParser.load(path);
    return cloneSet;
  }
}
