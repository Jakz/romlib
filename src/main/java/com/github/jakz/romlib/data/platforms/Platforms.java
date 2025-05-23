package com.github.jakz.romlib.data.platforms;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;

import com.github.jakz.romlib.ui.Icon;

public enum Platforms implements Platform
{
  NES("nes", "Nintendo", "NES", new String[] {"nes", "rom", "unf"}, Icon.SYSTEM_NES, new ScreenSize(DisplayType.AnalogPAL, 256, 240)),
  SNES("snes", "Nintendo", "Super Nintendo", new String[] {"smc", "sfc", "fig"}, Icon.SYSTEM_SUPER_NINTENDO),
  N64("n64", "Nintendo", "64", new String[] {"n64", "v64", "u64", "z64"}, Icon.SYSTEM_NINTENDO_64),
  GC("gc", "Nintendo", "GameCube", new String[] {"iso", "bin" }, Icon.SYSTEM_NINTENDO_GAMECUBE),
  GB("gb", "Nintendo", "GameBoy", new String[] {"gb"}, Icon.SYSTEM_GAMEBOY),
  GBC("gbc", "Nintendo", "GameBoy Color", new String[] {"gbc"}, Icon.SYSTEM_GAMEBOY_COLOR),
  GBA("gba", "Nintendo", "GameBoy Advance",new String[] {"gba", "agb", "bin"}, Icon.SYSTEM_GAMEBOY_ADVANCE),
  NDS("nds", "Nintendo", "DS", new String[] {"nds", "dsi"}, Icon.SYSTEM_NINTENDO_DS),
  _3DS("3ds", "Nintendo", "3DS", new String[] {"3ds", "cia", "3dsx"}),
  SWITCH("ns", "Nintendo", "Switch", new String[] {"nsp", "xci"}),

  
  LYNX("lynx", "Atari", "Lynx", new String[] { "lnx" }),
  
  PS1("ps1", "Sony", "Playstation", new String[] { "bin", "iso", "chd" }),
  PS2("ps2", "Sony", "Playstation 2", new String[] { "bin", "iso" }),

  PSP("psp", "Sony", "Playstation Portable", new String[] { "iso", "cso" }, Icon.SYSTEM_SONY_PLAYSTATION_PORTABLE, new ScreenSize(DisplayType.LCD, 480, 272)),
  
  WS("ws", "Bandai", "WonderSwan", new String[] {"ws"}, Icon.SYSTEM_WONDERSWAN),
  GG("gg", "Sega", "Game Gear", new String[] {"gg"}, Icon.SYSTEM_GAME_GEAR),
  MD("md", "Sega", "Mega Drive", new String[] {"md", "gen"}),
  
  C64("c64", "Commodore", "64", null, Icon.SYSTEM_COMMODORE_64),
  AMIGA("amiga", "Commodore", "Amiga", new String[] { "idf", "adf" }, Icon.SYSTEM_AMIGA),

  NGP("ngp", null, "Neo-Geo Pocket", new String[] {"ngp"}, Icon.SYSTEM_NEO_GEO_POCKET),
  
  IBM_PC("pc", null, "PC", new String[] { "bin" }),
  
  A2600("a2600", "Atari", "2600", new String[] { "a26" }),
  
  
  MAME("mame", null, "MAME", new String[] { }),
  ;
  
  public final String tag;
  public final String company;
  public final String name;
  public final String[] exts;
  public final PlatformDetails details;
  
  private final Icon icon;
  
  private Platforms(String tag, String company, String name, String[] exts, Icon icon)
  {
    this(tag, company, name, exts, icon, new ScreenSize[0]);
  }
  
  private Platforms(String tag, String company, String name, String[] exts, Icon icon, ScreenSize[] screenSizes)
  {
    this.tag = tag;
    this.company = company;
    this.name = name;
    this.exts = exts;
    this.icon = icon;
    this.details = new PlatformDetails();
  }
  
  private Platforms(String tag, String company, String name, String[] exts, Icon icon, ScreenSize screenSize)
  {
    this.tag = tag;
    this.company = company;
    this.name = name;
    this.exts = exts;
    this.icon = icon;
    this.details = new PlatformDetails(screenSize);
  }
  
  private Platforms(String tag, String company, String name, String[] exts)
  {
    this(tag, company, name, exts, null, new ScreenSize[0]);
  }
  
  @Override
  public ImageIcon getIcon()
  {
    return icon != null ? icon.getIcon() : null;
  }
  
  @Override
  public String fullName()
  {
    return company != null ? (company + " - " + name) : name;
  }
  
  @Override
  public String getTag()
  {
    return tag;
  }
  
  @Override
  public PlatformDetails details()
  {
    return details;
  }
    
  @Override public String company() { return company; }
  @Override public String shortName() { return name; }
  
  @Override
  public String[] fileExtensions() { return exts; }
  
  public static List<Platform> sortedValues()
  {
    return Arrays.asList(values()).stream().sorted((s1, s2) -> s1.name.compareTo(s2.name)).collect(Collectors.toList());
  }
  
  public static Platform forIdent(String tag)
  {
    return Arrays.asList(values()).stream().filter(s -> s.tag.equals(tag)).findFirst().get();
  }
}