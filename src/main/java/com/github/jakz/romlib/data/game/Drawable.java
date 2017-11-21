package com.github.jakz.romlib.data.game;

public interface Drawable
{
  public LocationSet getDrawableLocation();
  public LanguageSet getDrawableLanguages();
  public GameStatus getDrawableStatus();
  public boolean getDrawableFavourite();
  public String getDrawableCaption();
  public long getDrawableSize();
  public int getDrawableOrdinal();
}
