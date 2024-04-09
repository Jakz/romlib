package com.github.jakz.romlib.data.set;

public class GameSetUUID
{
  private final String value;
  
  public GameSetUUID(String value)
  {
    this.value = value;
  }
  
  @Override public int hashCode() { return value.hashCode(); }
  @Override public boolean equals(Object other)
  {
    return other instanceof GameSetUUID && 
        value.equals(((GameSetUUID)other).value); 
  }
  
  public String asPath() { return value; }
}
