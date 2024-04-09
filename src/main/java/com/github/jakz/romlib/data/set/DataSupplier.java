package com.github.jakz.romlib.data.set;

import java.util.Optional;
import java.util.function.Function;

import com.github.jakz.romlib.data.cataloguers.CloneSetCreator;
import com.github.jakz.romlib.data.cataloguers.GameListTransformer;
import com.github.jakz.romlib.data.cataloguers.GameCataloguer;
import com.github.jakz.romlib.data.cataloguers.TitleNormalizer;
import com.github.jakz.romlib.data.game.GameClone;
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
    
    Data solve()
    {
      clones.ifPresent(c -> c.forEach(GameClone::updateInfo));
      return this;
    }
  }
  
  Data load(GameSet set);
  DatFormat getFormat();
  
  default DataSupplier apply(final CloneSetCreator cloneSetCreator)
  {
    return new DataSupplier()
    {
      @Override public Data load(GameSet set)
      {
        Data data = DataSupplier.this.load(set);
        if (data.games.isPresent())
        {
          CloneSet cloneSet = cloneSetCreator.generate(data.games.get());    
          return new Data(data.games.orElse(null), cloneSet, data.provider.orElse(null));
        }
        
        return data;
      }
      
      @Override public DatFormat getFormat() { return DataSupplier.this.getFormat(); }
    };
  }
  
  default DataSupplier apply(final TitleNormalizer titleNormalizer)
  {
    return new DataSupplier() {
      @Override public Data load(GameSet set)
      {
        Data data = DataSupplier.this.load(set);
        data.games.ifPresent(games -> games.forEach(game -> {
          game.setAttribute(GameAttribute.NORMALIZED_TITLE, titleNormalizer.normalize(game.getTitle()));
        }));
        return data;
      }
      
      @Override public DatFormat getFormat() { return DataSupplier.this.getFormat(); }
    };
  }
  
  default DataSupplier apply(final GameCataloguer cataloguer)
  {
    return new DataSupplier()
    {
      @Override public Data load(GameSet set)
      {
        Data data = DataSupplier.this.load(set);
        data.games.ifPresent(games -> games.forEach(game -> {
          game.forEach(cataloguer::catalogue);         
          cataloguer.catalogue(game);
        }));
        cataloguer.done();
        return data;
      }
      
      @Override public DatFormat getFormat() { return DataSupplier.this.getFormat(); }
    };
  }
  
  default DataSupplier solve()
  {
    return new DataSupplier()
    {
      @Override public Data load(GameSet set)
      {
        Data data = DataSupplier.this.load(set);
        data.solve();
        return data;
      }
      
      @Override public DatFormat getFormat() { return DataSupplier.this.getFormat(); }
    };
  }
  
  default public DataSupplier apply(final GameListTransformer aggregator)
  {
    return new DataSupplier()
    {
      @Override public Data load(GameSet set)
      {
        Data data = DataSupplier.this.load(set);
        if (data.games.isPresent())
          return new Data(aggregator.transform(data.games.get()), data.clones.orElse(null), data.provider.orElse(null));
        else        
          return data;
      }
      
      @Override public DatFormat getFormat() { return DataSupplier.this.getFormat(); }
    };
  }
    
  public static DataSupplier of(DatFormat format, Function<GameSet, Data> loader)
  {
    return new DataSupplier()
    {
      @Override public Data load(GameSet set) { return loader.apply(set); }
      @Override public DatFormat getFormat() { return format; }
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
