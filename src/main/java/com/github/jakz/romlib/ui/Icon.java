package com.github.jakz.romlib.ui;

import javax.swing.ImageIcon;

import com.pixbits.lib.exceptions.FileNotFoundException;

public enum Icon
{
  FLAG_AUSTRALIA("flag_australia"),
  FLAG_BELGIUM("flag_belgium"),
  FLAG_BRAZIL("flag_brazil"),
  FLAG_CANADA("flag_canada"),
  FLAG_CHINA("flag_china"),
  FLAG_CROATIA("flag_croatia"),
  FLAG_CZECH_REPUBLIC("flag_czech_republic"),
  FLAG_DENMARK("flag_denmark"),
  FLAG_EUROPE("flag_europe"),
  FLAG_FINLAND("flag_finland"),
  FLAG_FRANCE("flag_france"),
  FLAG_GERMANY("flag_germany"),
  FLAG_GREECE("flag_greece"),
  FLAG_HONG_KONG("flag_hong_kong"),
  FLAG_HUNGARY("flag_hungary"),
  FLAG_INDIA("flag_india"),
  FLAG_ISRAEL("flag_israel"),
  FLAG_ITALY("flag_italy"),
  FLAG_JAPAN("flag_japan"),
  FLAG_KOREA("flag_korea"),
  FLAG_NETHERLANDS("flag_netherlands"),
  FLAG_NORWAY("flag_norway"),
  FLAG_POLAND("flag_poland"),
  FLAG_PORTUGAL("flag_portugal"),
  FLAG_RUSSIA("flag_russia"),
  FLAG_SAUDI_ARABIA("flag_saudi_arabia"),
  FLAG_SINGAPORE("flag_singapore"),
  FLAG_SLOVAKIA("flag_slovakia"),
  FLAG_SLOVENIA("flag_slovenia"),
  FLAG_SPAIN("flag_spain"),
  FLAG_SWEDEN("flag_sweden"),
  FLAG_TAIWAN("flag_taiwan"),
  FLAG_THAILAND("flag_thailand"),
  FLAG_TURKEY("flag_turkey"),
  FLAG_UKRAINE("flag_ukraine"),
  FLAG_UNITED_KINGDOM("flag_united_kingdom"),
  FLAG_USA("flag_usa"),
  FLAG_SCANDINAVIA("flag_scandinavia"),
  FLAG_USA_EUROPE("flag_usa_europe"),
  FLAG_JAPAN_USA("flag_japan_usa"),
  FLAG_WORLD("flag_usa"),
  FAVORITE("favorite"),
  EDIT("edit"),
  DELETE("delete"),
  ADD("add"),
  ARROW_UP("arrow-up"),
  ARROW_DOWN("arrow-down"),
  ARROW_UP_DOWN("arrow-up-down"),
  
  ROM("rom"),

  SYSTEM_GAME_GEAR("systems/game-gear"),
  SYSTEM_GAMEBOY("systems/gameboy"), //TODO: fix icon which is now a copy of GBC
  SYSTEM_GAMEBOY_ADVANCE("systems/gameboy-advance"),
  SYSTEM_GAMEBOY_COLOR("systems/gameboy-color"),
  SYSTEM_NINTENDO_DS("systems/nintendo-ds"),
  
  SYSTEM_SONY_PLAYSTATION_PORTABLE("systems/sony-playstation-portable"),
  
  SYSTEM_NES("systems/nes"),
  SYSTEM_SUPER_NINTENDO("systems/super-nintendo"),
  SYSTEM_NINTENDO_64("systems/nintendo-64"),
  SYSTEM_NINTENDO_GAMECUBE("systems/nintendo-gamecube"),
  
  SYSTEM_SEGA_MASTER_SYSTEM("systems/sega-master-system"),
  
  SYSTEM_COMMODORE_64("systems/commodore-64"),
  SYSTEM_AMIGA("systems/amiga"),

  SYSTEM_WONDERSWAN("systems/wonderswan"),
  SYSTEM_NEO_GEO_POCKET("systems/neo-geo-pocket"),
  SYSTEM_MISSING("systems/missing"),
  ;
  
  private final String name;
  private ImageIcon icon;
  
  Icon(String name)
  {
    this.name = name;
  }
  
  public ImageIcon getIcon()
  {
    String path = "com/github/jakz/romlib/ui/resources/"+name+".png";
    
    try
    {
      if (icon == null)
        icon = new ImageIcon(this.getClass().getClassLoader().getResource(path));
    }
    catch (NullPointerException e)
    {
      throw new RuntimeException("Missing resource file: "+path);
    }
    
    return icon;
  }
}
