package com.github.jakz.romlib.formats;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlaylistM3U implements Iterable<Path>
{
  private final List<Path> paths;
  
  public PlaylistM3U(Path path) throws IOException
  {
    try (Stream<String> lines = Files.lines(path))
    {
      paths = lines
        .filter(l -> !l.isEmpty() && !l.startsWith("#"))
        .map(s -> Paths.get(s))
        .map(p -> p.isAbsolute() ? p : path.getParent().resolve(p))
        .collect(Collectors.toList());
    }
  }
  
  
  public Stream<Path> stream() { return paths.stream(); }
  public Iterator<Path> iterator() { return paths.iterator(); }
}
