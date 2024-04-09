package com.github.jakz.romlib.data.game;

import java.util.Objects;

import com.github.jakz.romlib.data.game.attributes.Attribute;
import com.github.jakz.romlib.data.game.attributes.GameAttribute;

public class GameRef
{
  private Attribute attribute;
  private String value;
  
  private GameRef(Attribute attribute, String value)
  {
    this.attribute = attribute;
    this.value = value;
  }
  
  public static GameRef ofName(String name)
  {
    return new GameRef(GameAttribute.TITLE, name);
  }
  
  public static GameRef ofIdentifier(String identifier)
  {
    return new GameRef(GameAttribute.IDENTIFIER, identifier);
  }
  
  public boolean isMatching(Game game)
  {
    String value = game.getAttribute(attribute);
    return value.equals(this.value);
  }
  
  @Override
  public boolean equals(Object other)
  {
    return other instanceof GameRef 
        && ((GameRef)other).value.equals(value)
        && ((GameRef)other).attribute.equals(attribute);
  }
  
  @Override
  public int hashCode()
  {
    return Objects.hash(value, attribute);
  }
}
