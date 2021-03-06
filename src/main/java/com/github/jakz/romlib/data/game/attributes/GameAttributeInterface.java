package com.github.jakz.romlib.data.game.attributes;

import java.util.function.Supplier;

import com.github.jakz.romlib.data.game.LanguageSet;
import com.github.jakz.romlib.data.game.LocationSet;
import com.github.jakz.romlib.data.game.RomSize;
import com.github.jakz.romlib.data.game.Version;
import com.github.jakz.romlib.data.game.VersionNumber;
import com.github.jakz.romlib.data.game.VideoFormat;

public interface GameAttributeInterface
{
  void setAttribute(Attribute key, Object value);
  public <T> T getAttribute(Attribute key);
  
  void setCustomAttribute(Attribute key, Object value);
  boolean hasAttribute(Attribute key);
  boolean hasAnyCustomAttribute();
  
  default void setTitle(String title) { setAttribute(GameAttribute.TITLE, title); }
  default String getTitle() { return getAttribute(GameAttribute.TITLE); }
  
  default void setNormalizedTitle(String title) { setAttribute(GameAttribute.NORMALIZED_TITLE, title); }
  default String getNormalizedTitle() { 
    String title = getAttribute(GameAttribute.NORMALIZED_TITLE);
    return title != null ? title : getTitle();
  }
  
  default void setExportTitle(String title) { setAttribute(GameAttribute.EXPORT_TITLE, title); }
  default String getExportTitle() {
    String title = getAttribute(GameAttribute.EXPORT_TITLE);
    return title != null ? title : getTitle();
  }
  
  default boolean getBoolAttribute(Attribute key)
  {
    Boolean b = getAttribute(key);
    return b != null && b;
  }
  
  default <T> T getOrComputeAttribute(Attribute key, Supplier<T> supplier)
  {
    T attribute = getAttribute(key);
    
    if (attribute == null)
    {
      attribute = supplier.get();
      setAttribute(key, attribute);
    }
        
    return attribute;
  }
  
  default void setDescription(String description) { setAttribute(GameAttribute.DESCRIPTION, description); }
  default String getDescription() { return getAttribute(GameAttribute.DESCRIPTION); }
  
  default RomSize getSize() { return getAttribute(GameAttribute.SIZE); }
  default LocationSet getLocation() { return getAttribute(GameAttribute.LOCATION); }
  default LanguageSet getLanguages() { return getAttribute(GameAttribute.LANGUAGE); }
    
  default void setVersion(Version version) { setAttribute(GameAttribute.VERSION, version); }
  default Version getVersion() { return getAttribute(GameAttribute.VERSION); }
  
  default void setVersionNumber(VersionNumber versionNumber) { setAttribute(GameAttribute.VERSION_NUMBER, versionNumber); }
  default VersionNumber getVersionNumber() { return getAttribute(GameAttribute.VERSION_NUMBER); }
   
  default void setComment(String comment) { setAttribute(GameAttribute.COMMENT, comment); }
  default String getComment() { return getAttribute(GameAttribute.COMMENT); }
  
  default void setLicensed(boolean licensed) { setAttribute(GameAttribute.LICENSED, licensed); }
  default boolean getLicensed() { return getAttribute(GameAttribute.LICENSED); }
  
  default void setVideoFormat(VideoFormat format) { setAttribute(GameAttribute.VIDEO_FORMAT, format); }
  default VideoFormat getVideoFormat() { return getAttribute(GameAttribute.VIDEO_FORMAT); }
  
}
