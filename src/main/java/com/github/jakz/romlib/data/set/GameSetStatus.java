package com.github.jakz.romlib.data.set;

import java.util.stream.Stream;

import com.github.jakz.romlib.data.game.Game;

public class GameSetStatus
{
  private int countCorrect;
  private int countNotFound;
  private int countIncomplete;
  private int countBadlyNamed;
  
  private int countFoundRoms;
  private int countMissingRoms;
  
  private long foundSizeInBytes;
  private long actualSizeInBytes;
  
  
  public GameSetStatus()
  {
    countCorrect = 0;
    countNotFound = 0;
    countBadlyNamed = 0;
    countIncomplete = 0;
  }
  
  public void refresh(Stream<Game> games)
  {    
    countNotFound = 0;
    countBadlyNamed = 0;
    countCorrect = 0;
    countIncomplete = 0;
    
    foundSizeInBytes = 0;
    actualSizeInBytes = 0;
    
    countFoundRoms = 0;
    countMissingRoms = 0;
    
    games.forEach(g -> {
      switch (g.getStatus())
      {
        case MISSING: ++countNotFound; break;
        case INCOMPLETE: ++countIncomplete; break;
        case UNORGANIZED: ++countBadlyNamed; break;
        case FOUND: ++countCorrect; break;
      }
      
      g.stream().forEach(r -> {   
        if (r.isPresent())
        {
          foundSizeInBytes += r.size.bytes();
          actualSizeInBytes += r.handle().compressedSize();
          ++countFoundRoms;
        }
        else
          ++countMissingRoms;
      });
    });
  }
  
  public int getFoundRomsCount() { return countFoundRoms; }
  public int getMissingRomsCount() { return countMissingRoms; }
  
  public int getCorrectCount() { return countCorrect; }
  public int getNotFoundCount() { return countNotFound; }
  public int getUnorganizedCount() { return countBadlyNamed; }
  public int getIncompleteCount() { return countIncomplete; }
  public int getFoundCount() { return countCorrect + countBadlyNamed; }
  
  public long foundBytes() { return foundSizeInBytes; }
  public long compressedBytes() { return actualSizeInBytes; }
}
