package com.github.jakz.romlib.data.game;

import java.util.Comparator;

public class GameSorter
{
  public static Comparator<? super Game> byCaption()
  {
    return (g1, g2) -> g1.getDrawableCaption().compareToIgnoreCase(g2.getDrawableCaption());
  }
}
