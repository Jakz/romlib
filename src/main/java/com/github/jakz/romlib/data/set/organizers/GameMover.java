package com.github.jakz.romlib.data.set.organizers;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.jakz.romlib.data.game.Game;

@FunctionalInterface
public interface GameMover
{
  Path getFolderForGame(Game game);
  
  public static final GameMover DUMMY = g -> Paths.get(".");
}
