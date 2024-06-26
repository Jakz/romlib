package com.github.jakz.romlib.data.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.jakz.romlib.ui.Icon;
import com.github.jakz.romlib.ui.i18n.I18N;
import com.github.jakz.romlib.ui.i18n.Text;

public enum Location
{
  USA(Text.LOCATION_USA, "US", "U", Icon.FLAG_USA, Language.ENGLISH),
	GERMANY(Text.LOCATION_GERMANY, "GE", "G", Icon.FLAG_GERMANY, Language.GERMAN),
	CHINA(Text.LOCATION_CHINA, "CH", "C", Icon.FLAG_CHINA, Language.CHINESE),
	SPAIN(Text.LOCATION_SPAIN, "SP", "S", Icon.FLAG_SPAIN, Language.SPANISH),
	CANADA(Text.LOCATION_CANADA, "CA", "CA", Icon.FLAG_CANADA, Language.ENGLISH),
	FRANCE(Text.LOCATION_FRANCE, "FR", "F", Icon.FLAG_FRANCE, Language.FRENCH),
	ITALY(Text.LOCATION_ITALY, "IT", "I", Icon.FLAG_ITALY, Language.ITALIAN),
	JAPAN(Text.LOCATION_JAPAN, "JP", "J", Icon.FLAG_JAPAN, Language.JAPANESE),
	NETHERLANDS(Text.LOCATION_NETHERLANDS, "NL", "N", Icon.FLAG_NETHERLANDS, Language.DUTCH), 
	DENMARK(Text.LOCATION_DENMARK, "DK", "DK", Icon.FLAG_DENMARK, Language.DANISH), 
	FINLAND(Text.LOCATION_FINLAND, "FI", "FI", Icon.FLAG_FINLAND, Language.FINNISH),
	SWEDEN(Text.LOCATION_SWEDEN, "KR", "K", Icon.FLAG_SWEDEN, Language.SWEDISH),
	NORWAY(Text.LOCATION_NORWAY, "NO", "NO", Icon.FLAG_NORWAY, Language.NORWEGIAN),
	PORTUGAL(Text.LOCATION_PORTUGAL, "PR", "P", Icon.FLAG_PORTUGAL, Language.PORTUGUESE),
	CROATIA(Text.LOCATION_CROATIA, "HR", "HR", Icon.FLAG_CROATIA, Language.CROATIAN),
	POLAND(Text.LOCATION_POLAND, "PL", "PL", Icon.FLAG_POLAND, Language.POLISH),
	GREECE(Text.LOCATION_GREECE, "GR", "GR", Icon.FLAG_GREECE, Language.GREEK),
	HUNGARY(Text.LOCATION_HUNGARY, "HU", "HU", Icon.FLAG_HUNGARY, Language.HUNGARIAN),
	UKRAINE(Text.LOCATION_UKRAINE, "UA", "UA", Icon.FLAG_UKRAINE, Language.UKRAINIAN),
	BELGIUM(Text.LOCATION_BELGIUM, "BE", "BE", Icon.FLAG_BELGIUM, (Language)null), // TODO: no language, special case
	SINGAPORE(Text.LOCATION_SINGAPORE, "SG", "SG", Icon.FLAG_SINGAPORE, (Language)null),
	SLOVENIA(Text.LOCATION_SLOVENIA, "SL", "SL", Icon.FLAG_SLOVENIA, Language.SLOVENE),
	SLOVAKIA(Text.LOCATION_SLOVAKIA, "SK", "SK", Icon.FLAG_SLOVAKIA, Language.SLOVAK),
	INDIA(Text.LOCATION_INDIA, "IN", "IN", Icon.FLAG_INDIA, Language.HINDI),
	ISRAEL(Text.LOCATION_ISRAEL, "IL", "IL", Icon.FLAG_ISRAEL, Language.HEBREW),
	THAILAND(Text.LOCATION_THAILAND, "TH", "TH", Icon.FLAG_THAILAND, Language.THAI),
	UNITED_KINGDOM(Text.LOCATION_UNITED_KINGDOM, "GB", "GB", Icon.FLAG_UNITED_KINGDOM, Language.ENGLISH_UK),
	
	RUSSIA(Text.LOCATION_RUSSIA, "RU", "R", Icon.FLAG_RUSSIA, Language.RUSSIAN), // TODO: flag
	
	AUSTRALIA(Text.LOCATION_AUSTRALIA, "AU", "A", Icon.FLAG_AUSTRALIA, Language.ENGLISH), // TODO: english_au?
	
	KOREA(Text.LOCATION_KOREA, "SW", "SW", Icon.FLAG_KOREA, Language.KOREAN),
	TAIWAN(Text.LOCATION_TAIWAN, "TW", "TW", Icon.FLAG_TAIWAN, Language.CHINESE),
	HONG_KONG(Text.LOCATION_HONG_KONG, "HK", "HK", Icon.FLAG_HONG_KONG, Language.CHINESE),

	BRASIL(Text.LOCATION_BRAZIL, "BR", "BR", Icon.FLAG_BRAZIL, Language.PORTUGUESE_BR),

	EUROPE(Text.LOCATION_EUROPE, "EU", "E", Icon.FLAG_EUROPE, ITALY, FRANCE, GERMANY, SPAIN, SWEDEN), // TODO: decide how to manage macro zones properly
	USA_JAPAN(Text.LOCATION_USA_JAPAN, "US-JP", "UJ", Icon.FLAG_JAPAN_USA, USA, JAPAN),
	JAPAN_EUROPE(Text.LOCATION_JAPAN_EUROPE, "JP-EU", "JE", Icon.FLAG_JAPAN_USA, JAPAN, EUROPE), //TODO japan europe icon
	EUROPE_AUSTRALIA(Text.LOCATION_EUROPE_AUSTRALIA, "EU-AU", "EA", Icon.FLAG_JAPAN_USA, EUROPE, AUSTRALIA), //TODO europe australia icon

	USA_EUROPE(Text.LOCATION_USA_EUROPE, "US-EU", "UE", Icon.FLAG_USA_EUROPE, USA, EUROPE),
	ASIA(Text.LOCATION_ASIA, "Asia", "Asia", Icon.FLAG_JAPAN, JAPAN, CHINA, HONG_KONG, TAIWAN), // TODO verify
	SCANDINAVIA(Text.LOCATION_SCANDINAVIA, "Scandinavia", "Scandinavia", Icon.FLAG_SCANDINAVIA, SWEDEN, NORWAY, FINLAND, DENMARK), // TODO: flag
	
	WORLD(Text.LOCATION_WORLD, "W", "W", Icon.FLAG_WORLD, USA, JAPAN, EUROPE),
	
	NONE(Text.LOCATION_NONE, "NA", "N", Icon.FLAG_USA)
	
	;
		
  public final long mask;
	public final String fullName;
	public final String shortName;
	public final String tinyName;
	public final Icon icon;
	public final Language language;
	
	Location(I18N name, String shortName, String tinyName, Icon icon, Language language)
	{
		if (this.ordinal() >= Long.BYTES*8)
		  new EnumConstantNotPresentException(Location.class, "Maximum amount of locations is " + (Long.BYTES*8));
		
    this.fullName = name.text();
    this.shortName = shortName;
    this.tinyName = tinyName;
    this.icon = icon;
		this.mask = 1L << this.ordinal();
		this.language = language;
	}
	
  private Location(I18N name, String shortName, String tinyName, Icon icon, Location... locations)
  { 
    this.fullName = name.text();
    this.shortName = shortName;
    this.tinyName = tinyName;
    this.icon = icon;
    this.mask = Arrays.stream(locations).reduce(0L, (m,l) -> m | l.mask, (u,v) -> u | v); 
    this.language = null;
  }
  
  public boolean isComposite() { return Long.bitCount(mask) > 1; }
  
  private static final Map<Long, Location> mapping = new HashMap<Long, Location>();
  
  static
  {
    for (Location location : values())
      mapping.put(location.mask, location);
  }
  
  public static Location getExactLocation(LocationSet set)
  {
    long mask = set.getMask();
    
    //TODO: hack for US+EU+JP considered as WORLD
    if ((mask & WORLD.mask) == WORLD.mask)
      mask = WORLD.mask;
    
    return mapping.getOrDefault(mask, Location.NONE);
  }
  
  public static List<Location> getFuzzyLocations(LocationSet set)
  {
    int matching = 0;
    final List<Location> locations = new ArrayList<>();
    
    for (Location location : locations)
    {
      long masked = set.getMask() & location.mask;
      if (masked > matching)
        locations.clear();
      if (masked >= matching)
        locations.add(location);
    }
    
    return locations;
  }
  
  public static Location forName(String string)
  {
    return Arrays.stream(values())
      .filter(l -> l.fullName.compareToIgnoreCase(string) == 0)
      .findFirst()
      .orElse(null);
  }
	
	@Override
  public String toString()
	{
		return fullName;
	}
}
