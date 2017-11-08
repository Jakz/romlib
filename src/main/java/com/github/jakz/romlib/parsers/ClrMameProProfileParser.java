package com.github.jakz.romlib.parsers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.github.jakz.romlib.data.set.GameSetInfo;
import com.pixbits.lib.io.xml.XMLHandler;

public class ClrMameProProfileParser extends XMLHandler<Set<GameSetInfo>>
{
  private final Set<GameSetInfo> dats = new TreeSet<>((s1, s2) -> s1.getName().compareToIgnoreCase(s2.getName()));
  private GameSetInfo dat;
  
  @Override
  protected void init()
  {
    dats.clear();
  }

  @Override
  protected void start(String ns, String name, Attributes attr) throws SAXException
  {
    if (name.equals("datfile"))
    {
      dat = new GameSetInfo();
    }
  }

  @Override
  protected void end(String ns, String name) throws SAXException
  {
    if (name.equals("datfile"))
    {
      dats.add(dat);
      dat = null;
    }
    else if (name.equals("name"))
      dat.setName(asString());
    else if (name.equals("description"))
      dat.setDescription(asString());
    else if (name.equals("version"))
      dat.setVersion(asString());
    else if (name.equals("author"))
      dat.setAuthor(asString());
    else if (name.equals("comment"))
      dat.setAuthor(asString());
    else if (name.equals("url"))
    {
      // TODO: handle, probably we should refactor GameSetInfo to avoid using Provider and store
      // this directly in attribute map
    }
    else if (name.equals("file"))
    {
      // TODO: same as above
    }
  }

  @Override
  public Set<GameSetInfo> get()
  {
    return dats;
  }

}
