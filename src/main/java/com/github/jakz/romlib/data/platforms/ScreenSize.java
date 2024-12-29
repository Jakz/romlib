package com.github.jakz.romlib.data.platforms;

import java.awt.Dimension;

public class ScreenSize extends Dimension
{
  public ScreenSize(int width, int height)
  {
    super(width, height);
  }
  
  float aspectRatio() { return width / (float)height; }
}
