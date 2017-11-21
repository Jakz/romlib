package com.github.jakz.romlib.data.set;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameClone;
import com.github.jakz.romlib.data.game.GameStatus;

public class CloneSet implements Iterable<GameClone>
{
  private final Map<String, String> attributes;
  private final GameClone[] clones;
  private final Map<Game, GameClone> cloneMap;
  
  public CloneSet(GameClone[] clones)
  {
    this(clones, Collections.emptyMap());
  }
  
  public CloneSet(GameClone[] clones, Map<String, String> attributes)
  {
    this.attributes = attributes;
    this.clones = clones;
    
    cloneMap = new HashMap<>((int)Arrays.stream(clones).flatMap(GameClone::stream).count());
    
    Arrays.stream(clones).forEach(gc -> {
      gc.stream().forEach(g -> cloneMap.put(g, gc));
    });  
  }
  
  public void resetStatus()
  {
    for (GameClone clone : clones)
      clone.setStatus(GameStatus.MISSING);
  }
  
  public void updateStatus()
  {
    for (GameClone clone : clones) clone.updateStatus();
  }
  
  public GameClone get(Game game) { return cloneMap.get(game); }
  public GameClone get(int index) { return clones[index]; }
  public int size() { return clones.length; }
  
  public Iterator<GameClone> iterator() { return Arrays.asList(clones).iterator(); }
  public Stream<GameClone> stream() { return Arrays.stream(clones); }
  
  public Map<String, String> attributes() { return attributes; }
  
  public boolean isConsistentTo(GameSet set)
  {
    Set<Game> games = set.stream().collect(Collectors.toSet());
    
    boolean sameAmountOfGames = set.gameCount() == cloneMap.size();
    boolean hasOrphanGames = set.stream().anyMatch(game -> !cloneMap.containsKey(game));
    boolean hasMissingGames = cloneMap.keySet().stream().anyMatch(game -> !games.contains(game));
    
    return sameAmountOfGames && !hasOrphanGames && !hasMissingGames;
  }
}
