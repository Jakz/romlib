package com.github.jakz.romlib.data.platforms;

import java.util.Comparator;

import javax.swing.ImageIcon;


public interface Platform
{
  public String getTag();
  public String fullName();
  
  public String company();
  public String shortName();
  
  public ImageIcon getIcon();
  public String[] fileExtensions();

  default public String defaultFileExtension() { return fileExtensions()[0]; }  
  
  public static Platform of(final String name)
  {
    return new Platform() {

      @Override public String getTag() { return ""; }
      @Override public String fullName() { return name; }
      @Override public String shortName() { return name; }
      @Override public String company() { return ""; }
      @Override public ImageIcon getIcon() { return null; }
      @Override public String[] fileExtensions() { return new String[] { "" }; }   
    };
  }
  
  public static Comparator<Platform> defaultSorter()
  {
    /*return (p1, p2) -> {
      int c = p1.company().compareToIgnoreCase(p2.company());
      
      if (c == 0)
        return p1.shortName().compareToIgnoreCase(p2.shortName());
      else
        return c;
    };*/
    
    return (p1, p2) -> p1.fullName().compareToIgnoreCase(p2.fullName());
  }
}
