package com.github.jakz.romlib.data.cataloguers.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.jakz.romlib.data.cataloguers.CloneSetCreator;
import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameClone;
import com.github.jakz.romlib.data.set.CloneSet;
import com.github.jakz.romlib.data.set.GameList;

public class NormalizedTitleCloneSetCreator implements CloneSetCreator
{
  @Override
  public CloneSet generate(GameList set)
  {
    Map<String, List<Game>> byTitle = set.stream().collect(Collectors.groupingBy(Game::getNormalizedTitle));    
    GameClone[] clones = byTitle.values().stream().map(v -> new GameClone(v)).toArray(i -> new GameClone[i]);
    
    return new CloneSet(clones);
  }

}
