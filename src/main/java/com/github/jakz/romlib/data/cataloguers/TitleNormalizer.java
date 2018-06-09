package com.github.jakz.romlib.data.cataloguers;

@FunctionalInterface
public interface TitleNormalizer extends GameSetTransformer
{
  String normalize(String title);
}
