package com.github.jakz.romlib.data.game.attributes;

public interface Attribute
{
  String prettyValue(Object value);
  Class<?> getType();
  String getCaption();
  String getIdent();
}