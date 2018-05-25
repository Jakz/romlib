package com.github.jakz.romlib.data.game;

public interface Version 
{    
  public final static Version PROPER = new Version() {
    @Override public boolean equals(Object obj) { return obj == this; }
    @Override public String toString() { return "Proper"; }
  };
  
  public final static Version SAMPLE = new Version() {
    @Override public boolean equals(Object obj) { return obj == this; }
    @Override public String toString() { return "Sample"; }
  };
  
  public final static Version DEMO = new Version() {
    @Override public boolean equals(Object obj) { return obj == this; }
    @Override public String toString() { return "Demo"; }
  };

  public final static Version PROTO = new Version() {
    @Override public boolean equals(Object obj) { return obj == this; }
    @Override public String toString() { return "Proto"; }
  };
  

  public final static Version PROMO = new Version() {
    @Override public boolean equals(Object obj) { return obj == this; }
    @Override public String toString() { return "Promo"; }
  };
  
  public final static Version RERELEASE = new Version() {
    @Override public boolean equals(Object obj) { return obj == this; }
    @Override public String toString() { return "Rerelease"; }
  };
  
  public final static Version UNSPECIFIED = new Version() {
    @Override public boolean equals(Object obj) { return obj == this; }
    @Override public String toString() { return "N/A"; }
  };
  
  public final class Alternative implements Version
  {
    private final int revision;
    
    public Alternative(int revision) { this.revision = revision; }
    public Alternative() { this(0); }
    
    @Override public boolean equals(Object obj) { return obj == this; }
    @Override public String toString() { return revision == 0 ? "Alternative" : ("Alternative" + revision); }
  }
  
  public final class Beta implements Version
  {
    private final String type;
    public Beta(String type)
    {
      this.type = type;
    }
    
    @Override public boolean equals(Object obj)
    {
      return obj instanceof Beta && ((Beta)obj).type.equals(type);
    }  
    
    public String toString() { return "Beta (" + type + ")"; }
  }
  
  public final class Revision implements Version
  {
    private final String type;
    public Revision(String type)
    { 
      this.type = type;
    }
    
    @Override public boolean equals(Object obj)
    { 
      return (obj instanceof Revision) && ((Revision)obj).type.equals(type);
    }
    
    public String toString() { return type; }
  }
}
