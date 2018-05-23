package com.github.jakz.romlib.data.cataloguers.impl;

import com.github.jakz.romlib.data.cataloguers.TitleNormalizer;

public class NoIntroNormalizer implements TitleNormalizer
{
  @Override
  public String normalize(String title)
  {
    int i = title.indexOf('(');
    return i != -1 ? title.substring(0, i).trim() : title;
  }
  
}
