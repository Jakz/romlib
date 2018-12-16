package com.github.jakz.romlib.data.game.attributes;

import java.util.function.Function;

public interface Attribute
{
  String prettyValue(Object value);
  Class<?> getType();
  String getCaption();
  String getIdent();
  
  public static Attribute of(String ident, Class<?> type, Function<Object, String> printer) { return of(ident, ident, type, printer); }
  public static Attribute of(String ident, String caption, Class<?> type, Function<Object, String> printer)
  {
    return new Attribute()
        {
          @Override public String prettyValue(Object value) { return printer.apply(value); }
          @Override public Class<?> getType() { return type; }
          @Override public String getCaption() { return caption; }
          @Override public String getIdent() { return ident; }
        };
  }
}