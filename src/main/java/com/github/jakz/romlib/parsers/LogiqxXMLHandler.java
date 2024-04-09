package com.github.jakz.romlib.parsers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.github.jakz.romlib.data.game.Date;
import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameRef;
import com.github.jakz.romlib.data.game.Rom;
import com.github.jakz.romlib.data.game.RomSize;
import com.github.jakz.romlib.data.game.attributes.GameAttribute;
import com.github.jakz.romlib.data.set.GameList;
import com.pixbits.lib.io.xml.XMLHandler;
import com.pixbits.lib.io.xml.XMLParser;

public class LogiqxXMLHandler extends XMLHandler<LogiqxXMLHandler.Data>
{
  public static class Data
  {
    public Map<String, String> setAttributes;
    public Map<GameRef, GameRef> childToParentCloneMap;
    public Map<GameRef, Game> gameRefMapping;
    public GameList list;
    
    public boolean hasClones() { return !childToParentCloneMap.isEmpty(); }
  }
 
  private enum Status
  {
    NOWHERE,
    HEADER,
  }
  
  Supplier<Game> gameFactory = () -> new Game();
  
  Status status;
  
  RomSize.Set sizeSet = new RomSize.NullSet();
  
  private Map<String, String> attributes;
  Map<GameRef, GameRef> childToParentCloneMap;
  public Map<GameRef, Game> gameRefMapping;

  List<Game> games;
  
  Game game;
  String gameName;
  String gameDescription;
  String gameComment;
  Date gameYear;
  String gameIdentifier;
  
  List<Rom> roms;
  String romName;
  long size;
  long crc;
  byte[] md5;
  byte[] sha1;
  
  public void initSizeSet(boolean finite)
  {
    sizeSet = finite ? new RomSize.RealSet() : new RomSize.NullSet();

  }
  
  public void setGameFactory(Supplier<Game> gameFactory)
  {
    this.gameFactory = gameFactory;
  }
  
  @Override
  protected void init()
  {
    status = Status.NOWHERE;
    games = new ArrayList<>();
    roms = new ArrayList<>();
    attributes = new HashMap<>();
    childToParentCloneMap = new HashMap<>();
    gameRefMapping = new HashMap<>();
  }

  @Override
  protected void start(String ns, String name, Attributes attr) throws SAXException
  {
    if (name.equals("header"))
      status = Status.HEADER;
    else if (status != Status.HEADER)
    {
      switch (name)
      {
        case "game":
        {
          gameName = attrString("name");
         
          /* if cloneof is present prepare it */
          if (hasAttr("cloneof"))
            childToParentCloneMap.put(GameRef.ofName(gameName), GameRef.ofName(attrString("cloneof")));
          
          /* if game has id (used by no-intro for parent-clone management */
          if (hasAttr("id"))
          {
            gameIdentifier = attrString("id");
            
            if (hasAttr("cloneofid"))
              childToParentCloneMap.put(GameRef.ofIdentifier(gameIdentifier), GameRef.ofIdentifier(attrString("cloneofid")));
          }
            
        }
        case "rom":
        {
          romName = attrString("name");
          size = longAttributeOrDefault("size", -1);
          crc = longHexAttributeOrDefault("crc", -1L);
          md5 = hexByteArray("md5");
          sha1 = hexByteArray("sha1");
          break;
        }
      }
    }
    else
    {
      /* header info */
    }
  }

  @Override
  protected void end(String ns, String name) throws SAXException
  {
    if (status == Status.HEADER)
    {
      switch (name)
      {
        case "header": status = Status.NOWHERE; break;

        default: 
          attributes.put(name, asString());
          break;
      }
    }
    else
    {
      switch (name)
      {
        case "description": gameDescription = asString(); break;
        case "comment": gameComment = asString(); break;
        case "year": gameYear = Date.ofYear(asInt()); break;
        case "game":
        {
          game = gameFactory.get();
          
          game.setRom(roms.toArray(new Rom[roms.size()]));
          
          if (gameName != null)
            game.setTitle(gameName);
          
          if (gameDescription != null)
            game.setDescription(gameDescription);
          
          if (gameComment != null)
            game.setComment(gameComment);
          
          if (gameYear != null)
            game.setAttribute(GameAttribute.RELEASE_DATE, gameYear);
          
          if (gameIdentifier != null)
          {
            game.setAttribute(GameAttribute.IDENTIFIER, gameIdentifier);
            gameRefMapping.put(GameRef.ofIdentifier(gameIdentifier), game);
          }
          
          gameRefMapping.put(GameRef.ofName(gameName), game);
          
          games.add(game); 
          
          game = null;
    
          gameDescription = null;
          gameName = null;
          gameComment = null;
          gameYear = null;
          gameIdentifier = null;
          
          roms.clear();;
          break;
        }
        case "rom": roms.add(new Rom(romName, sizeSet.forBytes(size), crc, md5, sha1)); break;
        case "datafile": 
        {
          //Provider provider = new Provider(name, description, version, "", author);
          data = new Data();
          data.setAttributes = attributes;
          data.childToParentCloneMap = childToParentCloneMap;
          data.gameRefMapping = gameRefMapping;
          data.list = new GameList(games.toArray(new Game[games.size()]), sizeSet);
          break;
        }
      }
    }
  }

  private Data data;
  @Override public Data get() { return data; }
  
  public static Data load(Path path) throws IOException, SAXException
  {
    LogiqxXMLHandler xparser = new LogiqxXMLHandler();
    XMLParser<Data> parser = new XMLParser<>(xparser);
    parser.load(path);
    return xparser.get();
  }
}
