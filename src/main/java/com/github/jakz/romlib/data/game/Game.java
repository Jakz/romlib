package com.github.jakz.romlib.data.game;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import com.github.jakz.romlib.data.assets.Asset;
import com.github.jakz.romlib.data.assets.AssetData;
import com.github.jakz.romlib.data.attachments.Attachments;
import com.github.jakz.romlib.data.game.attributes.Attribute;
import com.github.jakz.romlib.data.game.attributes.GameAttribute;
import com.github.jakz.romlib.data.game.attributes.GameAttributeInterface;
import com.github.jakz.romlib.data.game.attributes.GameInfo;
import com.github.jakz.romlib.data.platforms.Platform;
import com.github.jakz.romlib.data.set.GameSet;
import com.github.jakz.romlib.data.set.GameSetFeatures;
import com.pixbits.lib.io.FileUtils;

public class Game implements Comparable<Game>, Iterable<Rom>, GameAttributeInterface, Drawable
{
	private final GameSet set;

	private Rom[] roms;
	private final GameInfo info;
	private GameClone clone;
	
  private boolean favourite;
  private GameStatus status;

	private Map<Asset, AssetData> assetData = new HashMap<>();
	private final Attachments attachments = new Attachments();
	
	public void setAttribute(Attribute key, Object value) { info.setAttribute(key, value); }
	public void setCustomAttribute(Attribute key, Object value) { info.setCustomAttribute(key, value); }
	
	public <T> T getAttribute(Attribute key) { return info.getAttribute(key); }
	public boolean hasAttribute(Attribute key) { return info.hasAttribute(key); }
	
  public Stream<Map.Entry<Attribute, Object>> getCustomAttributes() { return info.getCustomAttributes(); }
  public boolean hasCustomAttribute(Attribute attrib) { return info.hasCustomAttribute(attrib); }
  public boolean hasAnyCustomAttribute() { return info.hasAnyCustomAttribute(); }
  public void clearCustomAttribute(Attribute attrib) { info.clearCustomAttribute(attrib); }

  public Attachments getAttachments() { return attachments; }
  
  private GameID<?> id;

	public Game(GameSet set)
	{
    this.set = set;
    this.info = new GameInfo();
	  status = GameStatus.MISSING;
	}
	
	public Game(GameSet set, Rom... roms)
	{
	  this(set);
	  setRom(roms);
	}
	
	public Game(Rom... roms)
	{
	  this.set = null;
    this.info = new GameInfo();
    status = GameStatus.MISSING;
    setRom(roms);
	}

	public void setRom(Rom... roms)
	{
	  this.roms = roms;
	  Arrays.stream(roms).forEach(r -> r.setGame(this));
	  info.setAttribute(GameAttribute.SIZE, new RomSize(stream().mapToLong(Rom::size).sum()));
	}
	
	public GameSet getGameSet() { return set; }
	
	public boolean hasClone() { return clone != null; }
	public GameClone getClone() { return clone; }
	public void setClone(GameClone clone) { this.clone = clone; }

	public long getSizeInBytes()
	{
	  return info.getSize().bytes();
	}

	@Override public Iterator<Rom> iterator() { return Arrays.asList(roms).iterator(); }
	public Stream<Rom> stream() { return Arrays.stream(roms); }
	public Stream<Rom> foundStream() { return stream().filter(Rom::isPresent); }
	public Rom rom()
	{ 
	  if (roms.length > 1)
	    throw new UnsupportedOperationException("Can't invoke Game::rom to obtain the single rom on a game with multiple roms");
	  return roms[0];
	}
	public Rom rom(int index) { return roms[index]; }
	public int romCount() { return roms.length; }
	
	public boolean shouldSerializeState()
	{
	  return isFavourite() || status != GameStatus.MISSING || info.hasAnyCustomAttribute();
	}
	
	public GameID<?> getID()
	{ 
	  if (id == null)
	    id = set.helper().gameIdGenerator().compute(this);
	  
	  return id;
	}
	
	public boolean isMatching(GameRef ref)
	{
	  return ref.isMatching(this);
	}
		
	public void setStatus(GameStatus status) { this.status = status; } // TODO: should be visible?
	public GameStatus getStatus() { return status; }
	public Platform getPlatform() { return set.platform(); }
		
	public AssetData getAssetData(Asset asset)
	{
	  return assetData.computeIfAbsent(asset, k -> new AssetData(k, this));
	}
	
	@Override
  public String toString()
	{
		return getCorrectName();
	}

	public boolean hasAsset(Asset asset)
	{
	  return getAssetData(asset).isPresent();
	}
	
	public boolean hasAllAssets()
	{
	  for (Asset asset : set.getAssetManager().getSupportedAssets())
	    if (!hasAsset(asset))
	      return false;
	  
	  return true;
	}
	
	public boolean isOrganized()
	{
	  GameSetFeatures helper = set.helper();
	  
	  String correctName = helper.renamer().getNameForGame(this);
	  
	  // TODO: manage archived/unarchived/folder organizer
	  /* just checking if all roms are archived with same filename (eg. same archive) */
	  if (isComplete())
	    return stream()
	      .map(rom -> rom.handle().path())
	      .map(FileUtils::fileNameWithoutExtension)
	      .allMatch(correctName::equals);
	  else
	    return false;
	}
	
	public void forgetStatus()
	{
    setStatus(GameStatus.MISSING);
    stream().forEach(r -> r.setHandle(null));
    if (clone != null)
      clone.updateStatus();
	}
	
	public void updateStatus()
	{ 
	  AtomicBoolean anyFound = new AtomicBoolean(false);
	  AtomicBoolean allFound = new AtomicBoolean(true);

	  stream().forEach(rom -> {
	    anyFound.set(anyFound.get() || rom.isPresent());
	    allFound.set(allFound.get() && rom.isPresent());
	  });
	  
	  if (!anyFound.get())
	    status = GameStatus.MISSING;
	  else if (!allFound.get())
	    status = GameStatus.INCOMPLETE;
	  else if (!isOrganized()) 
	    status = GameStatus.UNORGANIZED;
	  else
	    status = GameStatus.FOUND;
	  
	  if (clone != null)
	    clone.updateStatus();
	}
  
  public String getCorrectName()
  { 
    //TODO: necessary because set maybe null
    if (set != null)
      return set.helper().renamer().getNameForGame(this);
    else
      return getTitle();
  }

  public Path getCorrectFolder()
  {
    throw new UnsupportedOperationException("Move is not implemented anymore");

    /*FolderPlugin mover = set.getSettings().getFolderOrganizer();
    return mover.getFolderForRom(this);*/
  }
    
  public boolean hasMultipleRoms() { return roms.length > 1; }
  public boolean hasAnyRom() { return stream().anyMatch(Rom::isPresent); }
  public boolean isComplete() { return stream().allMatch(Rom::isPresent); }
  
  public boolean hasEquivalentRom(Rom rom)
  {
    return Arrays.stream(roms).anyMatch(irom -> irom.isEquivalent(rom));
  }
  
  public boolean isEquivalent(Game game)
  {
    return Arrays.stream(roms).allMatch(rom -> game.hasEquivalentRom(rom));
  }

	@Override
	public boolean equals(Object other)
	{ 
	  if (set.doesSupportAttribute(GameAttribute.NUMBER) && other instanceof Game)
	  {
	    int n1 = getAttribute(GameAttribute.NUMBER);
	    int n2 = ((Game)other).getAttribute(GameAttribute.NUMBER);
	    return n1 == n2;
	  }
	  else if (other instanceof Game)
	  {
	    return getTitle().equals(((Game)other).getTitle());
	  }
	  
	  return false;
	}
	
	@Override
  public int compareTo(Game rom)
	{
		if (set != null && set.doesSupportAttribute(GameAttribute.NUMBER))
		{
      int n1 = getAttribute(GameAttribute.NUMBER);
      int n2 = rom.getAttribute(GameAttribute.NUMBER);
      
      return Integer.compare(n1, n2);
		}
	  
	  return getTitle().compareTo(rom.getTitle());
	}
		
	public boolean isFavourite() { return favourite; }
	public void setFavourite(boolean value) { favourite = value; }
	
	@Override public String getDrawableCaption() { return set.helper().renamer().getNameForGame(this); /* getTitle(); */ }
	@Override public LocationSet getDrawableLocation() { return getLocation(); }
	@Override public LanguageSet getDrawableLanguages() { return getLanguages(); }
  @Override public boolean getDrawableFavourite() { return isFavourite(); }
  @Override public GameStatus getDrawableStatus() { return getStatus(); }
  @Override public long getDrawableSize() { return getSizeInBytes(); }
  @Override public int getDrawableOrdinal() { return getAttribute(GameAttribute.ORDINAL); }

}
