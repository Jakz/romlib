package com.github.jakz.romlib.data.cataloguers.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.github.jakz.romlib.data.cataloguers.GameListTransformer;
import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.Rom;
import com.github.jakz.romlib.data.set.GameList;
import com.pixbits.lib.lang.Pair;

public class RedumpAggregatorByDisks implements GameListTransformer
{
  private String trimDiskTokenFromName(String title)
  {
    title = title.replaceAll("\\ *\\(Dis[ck]\\ \\d+\\)\\ *", "");
    title = title.replaceAll("\\ *\\(Install Dis[ck]\\)\\ *", "");
    return title;
  }
  
  
  @Override
  public GameList transform(GameList list)
  {
    List<Game> single = new ArrayList<>();
    Map<String, Pair<Game, List<Rom>>> multiple = new HashMap<>();
    
    for (Game game : list)
    {
      if (game.getTitle().matches(".*\\(Dis[ck]\\ \\d+\\).*") || game.getTitle().contains("(Install Disc"))
      {                
        String trimmedTitle = trimDiskTokenFromName(game.getTitle());
        
        multiple.compute(trimmedTitle, (k, p) -> {
          if (p == null)
            return new Pair<>(game, game.stream().collect(Collectors.toList()));
          else
          {
            game.stream().forEach(p.second::add);
            return p;
          }
        });
      }
      else
        single.add(game);
    }

    multiple.forEach((k, p) -> p.first.setRom(p.second.stream().filter(r -> !r.name.endsWith(".cue")).toArray(i -> new Rom[i])));
    single.forEach(game -> game.setRom(game.stream().filter(r -> !r.name.endsWith(".cue")).toArray(i -> new Rom[i])));
    
    Game[] games = new Game[single.size() + multiple.size()];
    
    for (int i = 0; i < single.size(); ++i)
      games[i] = single.get(i);
    
    AtomicInteger i = new AtomicInteger(0);
    multiple.forEach((k, p) -> games[single.size() + i.getAndIncrement()] = p.first);
        
    return new GameList(games, list.sizeSet());
  }

}
