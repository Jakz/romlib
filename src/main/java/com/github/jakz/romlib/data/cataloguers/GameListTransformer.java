package com.github.jakz.romlib.data.cataloguers;

import com.github.jakz.romlib.data.set.GameList;

@FunctionalInterface
public interface GameListTransformer
{
  public GameList transform(GameList list);
}
