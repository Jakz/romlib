package com.github.jakz.romlib.data.platforms;

import java.awt.Dimension;

public class ScreenSize extends Dimension
{
  DisplayType type;
  
  public ScreenSize(DisplayType type, int width, int height)
  {
    super(width, height);
    this.type = type;
  }
  
  float aspectRatio() { return width / (float)height; }
}
