package com.github.jakz.romlib.data.game;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LanguageSet implements Iterable<Language>
{
  private final Set<Language> languages;
  
  public LanguageSet()
  {
    languages = new TreeSet<>();
  }
  
  public LanguageSet(LanguageSet other)
  {
    languages = new TreeSet<>(other.languages);
  }
  
  public LanguageSet(Language... languages)
  {
    this.languages = new TreeSet<>(Arrays.asList(languages));
  }
  
  public void clear() { languages.clear(); }
  
  public boolean empty() { return languages.isEmpty(); }
  public int size() { return languages.size(); }
  public void add(Language language) { languages.add(language); }
  public void add(Language... languages) { this.languages.addAll(Arrays.asList(languages)); }
  public void add(LanguageSet set) { languages.addAll(set.languages); } 
  
  public boolean includes(Language language) { return languages.contains(language); }
  
  public boolean isJust(Language language)
  {
    return languages.size() == 1 && languages.stream().findFirst().get() == language;
  }
  
  public Iterator<Language> iterator() { return languages.iterator(); }
  public Stream<Language> stream() { return languages.stream(); }

  public String toString()
  {
    return languages.stream().map(l -> l.iso639_1.toLowerCase()).collect(Collectors.joining(","));
  }
}
