package com.github.jakz.romlib.data.game;

import java.util.Arrays;

public class Edition
{
  private String value;
  
  public Edition() { value = ""; }
  public Edition(String value) { this.value = value; }
  
  public String value() { return value; }
  
  @Override public int hashCode() { return value.hashCode(); }
  @Override public boolean equals(Object o) { return (o instanceof Edition) && ((Edition)o).value.equals(value); }
  
  public class Set
  {
    private final java.util.Set<Edition> editions;
    
    public Set() { editions = new java.util.HashSet<>(); }
    public Set(Edition... edition)
    {
      this.editions = new java.util.HashSet<>(Arrays.asList(edition));
    }
    
    public void add(Edition edition) { editions.add(edition); }
    public java.util.Set<Edition> values() { return editions; }
  }
  
  public static class Expansion extends Edition { private Expansion(String value) { super(value); } }
  
  public static Expansion ofExpansion(String value) { return new Expansion(value); }
}
