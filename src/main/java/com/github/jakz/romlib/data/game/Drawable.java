package com.github.jakz.romlib.data.game;

public interface Drawable
{
  public LocationSet getDrawableLocation();
  public GameStatus getDrawableStatus();
  public boolean getDrawableFavourite();
  public String getDrawableCaption();
}
