package com.github.jakz.romlib.data.game;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class GameClone implements Iterable<Game>, Drawable
{  
  private final Game[] games;
  private final String[] names;
  private final LocationSet location;
  private final LanguageSet languages;
  private final long size;
  int ordinal;
  
  private int foundCount;
  private GameStatus status;
  
  public GameClone(Game game)
  {
    this.games = new Game[] { game };
    this.names = null;
    this.location = new LocationSet();
    this.location.add(game.getLocation());
    this.languages = new LanguageSet(game.getLanguages());
    this.status = GameStatus.MISSING;
    this.size = game.getSizeInBytes();
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
    games.stream().map(Game::getLocation).forEach(location::add);
    this.languages = new LanguageSet();
    games.stream().map(Game::getLanguages).forEach(languages::add);
    this.status = GameStatus.MISSING;
    this.size = games.stream().mapToLong(Game::getSizeInBytes).sum();
  }

  public GameClone(Collection<Game> games)
  {
    this(games, null);
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
      hasFound = hasFound || status == GameStatus.FOUND;
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
  
  @Override public String getDrawableCaption() { return games[0].getNormalizedTitle() + " (" + foundCount + "/" + games.length + ")"; } //TODO: better management
  @Override public LanguageSet getDrawableLanguages() { return languages; }
  @Override public LocationSet getDrawableLocation() { return location; }
  @Override public boolean getDrawableFavourite() { return false; }
  @Override public GameStatus getDrawableStatus() { return status; }
  @Override public long getDrawableSize() { return size; }
  @Override public int getDrawableOrdinal() { return ordinal; }
}
