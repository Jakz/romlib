package com.github.jakz.romlib.data.cataloguers;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.Rom;

@FunctionalInterface
public interface GameCataloguer extends GameSetTransformer
{
  public void catalogue(Game game);
  public default void catalogue(Rom rom) { }
  
  public default void done() { }
}
