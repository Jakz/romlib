package com.github.jakz.romlib.data.cataloguers.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.jakz.romlib.data.cataloguers.CloneSetCreator;
import com.github.jakz.romlib.data.cataloguers.GameCataloguer;
import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameClone;
import com.github.jakz.romlib.data.set.CloneSet;
import com.github.jakz.romlib.data.set.GameList;

public class GoodOldDaysFixer implements GameCataloguer, CloneSetCreator
{
  @Override
  public void catalogue(Game game)
  {
    /* switch title with description */
    String title = game.getTitle();
    String desc = game.getDescription();
    
    game.setTitle(desc);
    game.setDescription(title);
  }

  private String normalizedTitle(Game g)
  {
    int i = g.getTitle().indexOf("[");
    return g.getTitle().substring(0, i).trim();
  }
  
  @Override
  public CloneSet generate(GameList set)
  {
    /* truncate to [ and aggregate by name */
    Map<String, List<Game>> mapping = set.stream().collect(Collectors.groupingBy(g -> normalizedTitle(g)));
    GameClone[] clones = mapping.values().stream().map(v -> new GameClone(normalizedTitle(v.get(0)), v)).toArray(i -> new GameClone[i]);

    return new CloneSet(clones);
  }

}
