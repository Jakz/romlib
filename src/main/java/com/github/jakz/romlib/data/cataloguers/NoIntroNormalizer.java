package com.github.jakz.romlib.data.cataloguers;

public class NoIntroNormalizer implements TitleNormalizer
{
  @Override
  public String normalize(String title)
  {
    int i = title.indexOf('(');
    return i != -1 ? title.substring(0, i).trim() : title;
  }
  
}
