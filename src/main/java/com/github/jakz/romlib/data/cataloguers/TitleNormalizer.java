package com.github.jakz.romlib.data.cataloguers;

@FunctionalInterface
public interface TitleNormalizer
{
  String normalize(String title);
}
