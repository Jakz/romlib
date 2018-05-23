package com.github.jakz.romlib.data.set;

import java.util.Optional;
import java.util.function.Function;

import com.github.jakz.romlib.data.cataloguers.CloneSetCreator;
import com.github.jakz.romlib.data.cataloguers.GameAggregator;
import com.github.jakz.romlib.data.cataloguers.GameCataloguer;
import com.github.jakz.romlib.data.cataloguers.TitleNormalizer;
import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.attributes.GameAttribute;

public interface DataSupplier
{ 
  public class Data
  {
    public final Optional<GameList> games;
    public final Optional<CloneSet> clones;
    public final Optional<Provider> provider;    
    
    public Data(GameList games, CloneSet clones, Provider provider)
    {
      this.games = Optional.ofNullable(games);
      this.clones = Optional.ofNullable(clones);
      this.provider = Optional.ofNullable(provider);
    }
    
    public Data(GameList games) { this(games, null, null); } 
    public Data(GameList games, CloneSet clones) { this(games, clones, null); }
    public Data(GameList games, Provider provider) { this(games, null, provider); }
  }
  
  Data load(GameSet set);
  DatFormat getFormat();
  
  public static DataSupplier of(DatFormat format, Function<GameSet, Data> loader)
  {
    return new DataSupplier()
    {
      @Override public Data load(GameSet set) { return loader.apply(set); }
      @Override public DatFormat getFormat() { return format; }
    };
  }
  
  public static DataSupplier derive(final DataSupplier supplier, final GameCataloguer cataloguer, final TitleNormalizer normalizer)
  {
    return new DataSupplier()
    {
      @Override public Data load(GameSet set)
      {
        Data data = supplier.load(set);
        data.games.ifPresent(games -> games.forEach(game -> {
          cataloguer.catalogue(game);
          game.setAttribute(GameAttribute.NORMALIZED_TITLE, normalizer.normalize(game.getTitle()));
        }));
        cataloguer.done();
        return data;
      }
      
      @Override public DatFormat getFormat() { return supplier.getFormat(); }
    };
  }
  
  public static DataSupplier derive(final DataSupplier supplier, final CloneSetCreator cloneSetCreator)
  {
    return new DataSupplier()
    {
      @Override public Data load(GameSet set)
      {
        Data data = supplier.load(set);
        if (data.games.isPresent())
        {
          CloneSet cloneSet = cloneSetCreator.generate(data.games.get());    
          return new Data(data.games.orElse(null), cloneSet, data.provider.orElse(null));
        }
        
        return data;
      }
      
      @Override public DatFormat getFormat() { return supplier.getFormat(); }
    };
  }
  
  public static DataSupplier derive(final DataSupplier supplier, final GameAggregator aggregator)
  {
    return new DataSupplier()
    {
      @Override public Data load(GameSet set)
      {
        Data data = supplier.load(set);
        
        if (data.games.isPresent())
          return new Data(aggregator.aggregate(data.games.get()), data.clones.orElse(null), data.provider.orElse(null));
        else        
          return data;
      }
      
      @Override public DatFormat getFormat() { return supplier.getFormat(); }
    };
  }

  public static DataSupplier build(final DatFormat format)
  {
    return new DataSupplier()
    {
       @Override public Data load(GameSet set) { throw new UnsupportedOperationException("DatLoader for " + format + " doesn't have a loader."); }
       @Override public DatFormat getFormat() { return format; }
    };
  }
  
  public static DataSupplier build(final GameList gameList, final CloneSet clones)
  {
    return new DataSupplier()
    {
      @Override public Data load(GameSet set) { return new Data(gameList, clones); }
      @Override public DatFormat getFormat() { return DatFormat.DUMMY; }
    };
  }
  
  public static DataSupplier build(final GameList gameList)
  {
    return new DataSupplier()
    {
      @Override public Data load(GameSet set) { return new Data(gameList); }
      @Override public DatFormat getFormat() { return DatFormat.DUMMY; }
    };
  }
  
  public static DataSupplier build(final GameList gameList, final Provider provider)
  {
    return new DataSupplier()
    {
      @Override public Data load(GameSet set) { return new Data(gameList, provider); }
      @Override public DatFormat getFormat() { return DatFormat.DUMMY; }
    };
  }
}
