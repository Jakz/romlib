package com.github.jakz.romlib.data.cataloguers.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.jakz.romlib.data.cataloguers.CloneSetCreator;
import com.github.jakz.romlib.data.cataloguers.GameCataloguer;
import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameClone;
import com.github.jakz.romlib.data.game.Language;
import com.github.jakz.romlib.data.game.Location;
import com.github.jakz.romlib.data.game.attributes.GameAttribute;
import com.github.jakz.romlib.data.set.CloneSet;
import com.github.jakz.romlib.data.set.GameList;

public class GoodOldDaysFixer implements GameCataloguer
{
  private static String normalizedTitle(Game g)
  {
    int i = g.getTitle().lastIndexOf("[");
    return g.getTitle().substring(0, i).trim();
  }
  
  private int releaseNumber(String title)
  {
    int i = title.lastIndexOf("[");
    int j = title.lastIndexOf("]");
    
    return Integer.parseInt(title.substring(i+1, j));
  }
  
  @Override
  public void catalogue(Game game)
  {
    /* switch title with description */
    String title = game.getTitle();
    String desc = game.getDescription();
    
    String comment = game.getComment();
    int lastSpace = comment.lastIndexOf(' ');
    if (lastSpace != -1)
    {
      String language = comment.substring(lastSpace+1);
      switch (language)
      {
        case "English": game.getLanguages().add(Language.ENGLISH); game.getLocation().add(Location.USA); break;
        case "Deutsch": game.getLanguages().add(Language.GERMAN); game.getLocation().add(Location.GERMANY); break;
        case "Français": game.getLanguages().add(Language.FRENCH); game.getLocation().add(Location.FRANCE); break;
        case "Castellano": game.getLanguages().add(Language.SPANISH); game.getLocation().add(Location.SPAIN); break;
        case "Italiano": game.getLanguages().add(Language.ITALIAN); game.getLocation().add(Location.ITALY); break;
        case "Nederlands": game.getLanguages().add(Language.DUTCH); game.getLocation().add(Location.NETHERLANDS); break;
        default: throw new IllegalArgumentException("Unhandled language: "+language);
      }
    }
    
    game.setAttribute(GameAttribute.NUMBER, releaseNumber(desc));
    
    game.setTitle(desc);
    game.setDescription(title);
  }
  
  public static class CloneCreator implements CloneSetCreator
  {
    @Override
    public CloneSet generate(GameList set)
    {
      /* truncate to [ and aggregate by name */
      Map<String, List<Game>> mapping = set.stream().collect(Collectors.groupingBy(g -> GoodOldDaysFixer.normalizedTitle(g)));
      GameClone[] clones = mapping.values().stream().map(v -> new GameClone(GoodOldDaysFixer.normalizedTitle(v.get(0)), v)).toArray(i -> new GameClone[i]);

      return new CloneSet(clones);
    }
  }

}
