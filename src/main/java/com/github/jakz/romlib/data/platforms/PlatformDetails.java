package com.github.jakz.romlib.data.platforms;

public class PlatformDetails
{
  ScreenSize[] supportedScreenSizes;
  
  PlatformDetails(ScreenSize screenSize)
  {
    supportedScreenSizes = new ScreenSize[] { screenSize };
  }
  
  PlatformDetails()
  {
    supportedScreenSizes = new ScreenSize[0];
  }
  
  public ScreenSize screenSizes()
  {
    return supportedScreenSizes.length > 0 ? supportedScreenSizes[0] : null;
  }
}
