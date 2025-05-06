package com.github.jakz.romlib.data.game;

public class VersionNumber
{
  private final int major;
  private final String minor;
  private final String suffix;
  
  public VersionNumber(int major, String minor, String suffix)
  {
    this.major = major;
    this.minor = minor;
    this.suffix = suffix;
  }
  
  public VersionNumber(int major, String minor)
  {
    this(major, minor, "");
  }
  
  public VersionNumber(int major)
  {
    this(major, null, null);
  }
  
  public VersionNumber(int major, int minor)
  {
    this(major, Integer.toString(minor), "");
  }
  
  public int getMajor() { return major; }
  public String getMinor() { return minor; }
  public String getSuffix() { return suffix; }
  public String toString() { return major + "." + minor + suffix; }
  
  public static VersionNumber UNSPECIFIED = new VersionNumber(-1,-1) {
    @Override public boolean equals(Object o) { return this == o; }
  };
}