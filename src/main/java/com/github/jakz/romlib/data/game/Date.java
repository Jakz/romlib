package com.github.jakz.romlib.data.game;

public class Date
{
  private final int year;
  private final int month;
  private final int day;
  
  public Date(int year, int month, int day)
  {
    this.year = year;
    this.month = month;
    this.day = day;
  }
  
  public Date(int year, int month)
  {
    this(year, month, -1);
  }
  
  public Date(int year)
  {
    this(year, -1, -1);
  }
  
  public String toString()
  {
    if (month == -1 && day == -1)
      return Integer.toString(year);
    else if (month != -1 && day == -1)
      return String.format("%d-%02d", year, month);
    else
      return String.format("%d-%02d-%02d", year, month, day);
  }
  
  
  public static Date ofYear(int year) { return new Date(year); }
}
