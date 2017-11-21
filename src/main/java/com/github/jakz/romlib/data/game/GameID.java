package com.github.jakz.romlib.data.game;

import java.util.Arrays;
import java.util.Objects;

public interface GameID<T>
{
  public static class Numeric implements GameID<Integer>
  {
    private final int value;
    public Numeric(int value) { this.value = value; }
  }
  
  public static class Textual implements GameID<String>
  {
    private final String value;
    public Textual(String value) { this.value = value; }
  }
  
  public static class CRC implements GameID<Long>
  {
    public final long value;
    public CRC(long value) { this.value = value; }
    
    @Override public boolean equals(Object o) { return o instanceof CRC && ((CRC)o).value == value; }
    @Override public int hashCode() { return Long.hashCode(value); }
  }
  
  public static class MultipleCRC implements GameID<long[]>
  {
    public final long[] values;
    public MultipleCRC(long... values) { this.values = values; }
    
    @Override public boolean equals(Object o) { return o instanceof MultipleCRC && Arrays.equals(((MultipleCRC)o).values, values); }
    @Override public int hashCode() { return Arrays.hashCode(values); }
  }
}
