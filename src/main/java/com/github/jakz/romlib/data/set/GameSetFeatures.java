package com.github.jakz.romlib.data.set;

import java.nio.file.Path;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.set.organizers.GameRenamer;
import com.pixbits.lib.searcher.DummySearcher;
import com.pixbits.lib.searcher.Searcher;

public interface GameSetFeatures
{
  boolean hasFeature(Feature feature);
  Searcher<Game> searcher();
  GameRenamer renamer();
  Path getAttachmentPath();
  
  public static GameSetFeatures of(final GameSet set)
  {
    return new GameSetFeatures() {

      final Searcher<Game> searcher = new DummySearcher<>();
      final GameRenamer renamer = GameRenamer.DUMMY;
      
      @Override public boolean hasFeature(Feature feature) { return false; }
      @Override public Searcher<Game> searcher() { return searcher; }
      @Override public GameRenamer renamer() { return renamer; }

      @Override
      public Path getAttachmentPath()
      {
        throw new UnsupportedOperationException("Can't get attachment path for dummy GameSetFeatures");
      }     
    };
  }
}
