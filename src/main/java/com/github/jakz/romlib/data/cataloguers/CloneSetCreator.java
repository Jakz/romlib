package com.github.jakz.romlib.data.cataloguers;

import com.github.jakz.romlib.data.set.CloneSet;
import com.github.jakz.romlib.data.set.GameList;

@FunctionalInterface
public interface CloneSetCreator extends GameSetTransformer
{
  public CloneSet generate(GameList set);
}
