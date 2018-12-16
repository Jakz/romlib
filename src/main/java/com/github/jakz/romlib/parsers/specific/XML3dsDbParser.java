package com.github.jakz.romlib.parsers.specific;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.Language;
import com.github.jakz.romlib.data.game.Location;
import com.github.jakz.romlib.data.game.RomSize;
import com.github.jakz.romlib.data.game.attributes.Attribute;
import com.github.jakz.romlib.data.game.attributes.GameAttribute;
import com.github.jakz.romlib.data.set.DataSupplier;
import com.github.jakz.romlib.data.set.DataSupplier.Data;
import com.github.jakz.romlib.data.set.GameList;
import com.pixbits.lib.io.xml.XMLHandler;

public class XML3dsDbParser extends XMLHandler<DataSupplier.Data>
{ 
  public static enum ReleaseType
  {
    ESHOP,
    GAME,
    DEMO,
    _3DSWARE
  };
  
  public static final Attribute AttributeTitleID = Attribute.of("titleid", String.class, Object::toString);
  public static final Attribute AttributeReleaseName = Attribute.of("release-name", String.class, Object::toString);
  public static final Attribute AttributeReleaseType = Attribute.of("release-type", ReleaseType.class, Object::toString);
  public static final Attribute AttributeCardSlot = Attribute.of("card-slot", String.class, Object::toString);

  List<Game> games;
  Game game;
  
  
  @Override
  protected void init()
  {
    games = new ArrayList<>();
    game = null;
  }
  
  Location locationFor(String name)
  {
    switch (name) {
      case "EUR": return Location.EUROPE;
      case "JPN": return Location.JAPAN;
      case "USA": return Location.USA;
      case "FRA": return Location.FRANCE;
      case "ITA": return Location.ITALY;
      case "KOR": return Location.KOREA;
      case "TWN": return Location.TAIWAN;
      case "CHN": return Location.CHINA;
      case "GER": return Location.GERMANY;
      case "SPA": return Location.SPAIN;
      case "NLD": return Location.NETHERLANDS;
      case "UKV": return Location.UNITED_KINGDOM;
      case "WLD": return Location.WORLD;
      case "RUS": return Location.RUSSIA;

      default: throw new IllegalArgumentException("Unknown region "+name);
    }
  }
  
  Language[] languagesFor(String langs)
  {
    String[] tokens = langs.split(",");
    List<Language> languages = new ArrayList<>();
    
    for (String l : tokens)
    {
      Language lang = Language.for639_1(l);
      if (lang == null)
        throw new IllegalArgumentException("Unknown language: "+l);
      languages.add(lang);
    }
    
    return languages.toArray(new Language[languages.size()]);
  }
  
  ReleaseType releaseTypeFor(String type)
  {
    if (type.equals("1"))
      return ReleaseType.GAME;
    else if (type.equals("2"))
      return ReleaseType.DEMO;
    else if (type.equals("3"))
      return ReleaseType._3DSWARE;
    else if (type.equals("4"))
      return ReleaseType.ESHOP;
    else
      throw new IllegalArgumentException("Unknown release type: "+type+" for "+game);
  }

  @Override
  protected void start(String ns, String name, Attributes attr) throws SAXException
  {
    switch (name)
    {
      case "release":
      {
        if (game != null)
          throw new IllegalArgumentException("Game wasn't null on new game");
        game = new Game();
        break;
      }
    }
  }

  @Override
  protected void end(String ns, String name) throws SAXException
  {
    switch (name)
    {
      case "id": game.setAttribute(GameAttribute.NUMBER, asInt()); break;
      case "name": game.setTitle(asString()); break;
      case "region": game.getLocation().add(locationFor(asString())); break;
      case "languages": game.getLanguages().add(languagesFor(asString())); break;
      case "group": game.setAttribute(GameAttribute.GROUP, asString()); break;
      case "serial": game.setAttribute(GameAttribute.SERIAL, asString()); break;
      case "titleid": game.setAttribute(AttributeTitleID, asString()); break;
      case "releasename": game.setAttribute(AttributeReleaseName, asString()); break;
      case "type": game.setAttribute(AttributeReleaseType, releaseTypeFor(asString())); break;
      case "card":
        String value = asString();
        if (value.equals("1") || value.equals("2"))
          game.setAttribute(AttributeCardSlot, value);
        else
          throw new IllegalArgumentException("Unknown card slot: "+value);
        
        break;
      case "release":
        games.add(game);
        game = null;
        break;
    }
  }

  @Override
  public Data get()
  {
    return new Data(new GameList(games, new RomSize.NullSet()));
  }

}
