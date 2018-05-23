package com.github.jakz.romlib.data.cataloguers;

import com.github.jakz.romlib.data.set.GameList;

@FunctionalInterface
public interface GameAggregator
{
  public GameList aggregate(GameList list);
}
