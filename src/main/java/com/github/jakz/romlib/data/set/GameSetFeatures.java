package com.github.jakz.romlib.data.set;

import java.nio.file.Path;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameID;
import com.github.jakz.romlib.data.set.organizers.GameMover;
import com.github.jakz.romlib.data.set.organizers.GameRenamer;
import com.pixbits.lib.searcher.DummySearcher;
import com.pixbits.lib.searcher.Searcher;

public interface GameSetFeatures
{
  boolean hasFeature(Feature feature);
  Searcher<Game> searcher();
  GameRenamer renamer();
  GameMover mover();
  Path getAttachmentPath();
  GameID.Generator gameIdGenerator(); 
  
  public static GameSetFeatures of(final GameSet set)
  {
    return new GameSetFeatures() {

      final Searcher<Game> searcher = new DummySearcher<>();
      final GameRenamer renamer = GameRenamer.DUMMY;
      final GameMover mover = GameMover.DUMMY;
      
      @Override public boolean hasFeature(Feature feature) { return false; }
      @Override public Searcher<Game> searcher() { return searcher; }
      @Override public GameRenamer renamer() { return renamer; }
      @Override public GameMover mover() { return mover; }
      @Override public GameID.Generator gameIdGenerator() { return GameID.Generator.DEFAULT; }
      
      @Override
      public Path getAttachmentPath()
      {
        throw new UnsupportedOperationException("Can't get attachment path for dummy GameSetFeatures");
      }     
    };
  }
}
