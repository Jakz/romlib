package com.github.jakz.romlib.data.attachments;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.github.jakz.romlib.data.game.Game;

public class Attachment
{
  private Path path;
  private String name;
  private String description;
  private AttachmentType type;
  private AttachmentType.Subtype subtype;
  
  public Attachment() { }
  
  public Attachment(Game rom, Path file) throws IOException
  {
    // move to attachments folder
    // TODO: manage embedding in rom archive
    Path attachmentsPath = rom.getGameSet().getAttachmentPath();
    
    Files.createDirectories(attachmentsPath);
    
    this.path = attachmentsPath.resolve(file.getFileName());
    
    if (!Files.isSameFile(file.getParent(), attachmentsPath))
      Files.move(file, path);
    
    name = path.getFileName().toString();
    guessType();
    description = "";
  }
  
  private void guessType()
  {
    if (name.endsWith(".ips"))
    {
      type = AttachmentType.IPS_PATCH;
      subtype = AttachmentType.IPS.MISC;
    }
    else
      type = AttachmentType.OTHER;
  }
  
  public Path getFilename() { return path.getFileName(); }
  public Path getPath() { return path; }
  
  public String getName() { return name; }
  public String getDescription() { return description; }
  public AttachmentType getType() { return type; }
  public AttachmentType.Subtype getSubType() { return subtype; }
  
  public void setDescription(String v) { this.description = v; }
  public void setType(AttachmentType t) { this.type = t; }
  public void setSubType(AttachmentType.Subtype t) { this.subtype = t; }
  public void setName(String name) { this.name = name; }
  public void setPath(Path path) { this.path = path; }
  
  public void updateName(String name) { setName(name); /*TODO: rename path?*/ }
}
