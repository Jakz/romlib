package com.github.jakz.romlib.data.game;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class GameClone implements Iterable<Game>, Drawable
{  
  private final Game[] games;
  private final String[] names;
  private final LocationSet location;
  private final LanguageSet languages;
  private long size;
  int ordinal;
  
  private int foundCount;
  private GameStatus status;
  
  public GameClone(Game game, String name)
  {
    this.games = new Game[] { game };
    this.names = name != null ? new String[] { name } : null;
    this.location = new LocationSet();
    this.location.add(game.getLocation());
    this.languages = new LanguageSet(game.getLanguages());
    this.status = GameStatus.MISSING;
    this.size = game.getSizeInBytes();
  }
  
  public GameClone(Game game)
  {
    this(game, null);
  }
  
  public GameClone(Game game, Location location, String name)
  {
    this.games = new Game[] { game };
    // TODO: maybe better management without the need of the whole array
    this.names = new String[Location.values().length];
    this.names[location.ordinal()] = name;
    this.location = new LocationSet(location);
    this.languages = new LanguageSet(game.getLanguages());
    this.status = GameStatus.MISSING;
    this.size = game.getSizeInBytes();

  }
  
  public GameClone(Collection<Game> games, String[] names)
  {
    if (games.isEmpty()) throw new IllegalArgumentException("a GameClone can't be empty");
    this.games = games.toArray(new Game[games.size()]);
    this.names = names;
    this.location = new LocationSet();
    this.languages = new LanguageSet();
    this.status = GameStatus.MISSING;
    
    //TODO: maybe this could be always be deferred? to save some performance
    updateInfo();
  }

  public GameClone(Collection<Game> games)
  {
    this(games, null);
  }
  
  public GameClone(String name, Collection<Game> games)
  {
    this(games, new String[] { name });
  }
  
  public void updateInfo()
  {
    this.location.clear();
    this.languages.clear();
    this.size = 0l;
    
    for (Game game : games)
    {
      location.add(game.getLocation());
      languages.add(game.getLanguages());
      size += game.getSizeInBytes();      
    }
    
  }
  
  public String getTitleForBias(BiasSet bias, boolean acceptFallback)
  {
    if (names != null)
    {
      for (Location location : bias.getLocations())
        if (names[location.ordinal()] != null)
          return names[location.ordinal()];
      
      /* TODO: should check for a contained bias, eg: Germany is contained in Europe */
      
      return games[0].getTitle();
    }
    else
    {
      Game game = getBestMatchForBias(bias, acceptFallback);
      return game != null ? game.getTitle() : null;
    }
  }

  public Game getBestMatchForBias(BiasSet bias, boolean acceptFallback)
  {    
    for (Location location : bias.getLocations())
    {
      Optional<Game> game = Arrays.stream(games).filter(g -> g.getLocation().is(location)).findAny();
      if (game.isPresent())
        return game.get();
    }

    if (acceptFallback)
      return games[0];
    else
      return null;
  }
  
  public void setStatus(GameStatus status) { this.status = status; }
  
  public void updateStatus()
  {
    boolean hasMissing = false, hasFound = false, hasUnorganized = false;
    foundCount = 0;
    
    for (Game game : games)
    {
      GameStatus status = game.getStatus();
      hasMissing = hasMissing || status == GameStatus.MISSING || status == GameStatus.INCOMPLETE;
      hasFound = hasFound || status == GameStatus.FOUND || status == GameStatus.UNORGANIZED;
      hasUnorganized = hasUnorganized || status == GameStatus.UNORGANIZED;
      
      if (status == GameStatus.FOUND || status == GameStatus.UNORGANIZED)
        ++foundCount;
    }
    
    if (hasMissing)
    {
      if (hasFound || hasUnorganized)
        status = GameStatus.INCOMPLETE;
      else
        status = GameStatus.MISSING;
    }
    else if (hasFound)
    {
      if (hasUnorganized)
        status = GameStatus.UNORGANIZED;
      else
        status = GameStatus.FOUND;
    }
  }
 
  public Game get(int index) { return games[index]; }
  public int size() { return games.length; }
  
  public Iterator<Game> iterator() { return Arrays.asList(games).iterator(); }
  public Stream<Game> stream() { return Arrays.stream(games); }
  public List<Game> list() { return Arrays.asList(games); } 
  
  @Override public String getDrawableCaption() 
  { 
    String base = names != null && names.length > 0 ? names[0] : games[0].getNormalizedTitle();
    return base + " (" + foundCount + "/" + games.length + ")";
  } 
  @Override public LanguageSet getDrawableLanguages() { return languages; }
  @Override public LocationSet getDrawableLocation() { return location; }
  @Override public boolean getDrawableFavourite() { return false; }
  @Override public GameStatus getDrawableStatus() { return status; }
  @Override public long getDrawableSize() { return size; }
  @Override public int getDrawableOrdinal() { return ordinal; }
}
