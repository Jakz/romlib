package com.github.jakz.romlib.data.assets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.github.jakz.romlib.data.game.Game;
import com.pixbits.lib.io.FileUtils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

public class AssetData
{  
  private final Asset asset;
  private final Game game;
  private Path path;
  private String urlData;
  private long crc;
  
  public AssetData(Asset asset, Game game)
  { 
    this.asset = asset;
    this.game = game;
    this.path = Paths.get(".");
  }

  public void setCRC(long crc) { this.crc = crc; }
  public void setPath(Path path) { this.path = path; }
  public void setURLData(String urlData) { this.urlData = urlData; }
    
  public long getCRC() { return crc; }
  public Path getPath() { return path; }
  public Path getFinalPath() { return game.getGameSet().getAssetPath(asset, false).resolve(path); }
  public String getURLData() { return urlData; }
  
  public boolean isPresent() { return isPresentAsFile() || isPresentAsArchive(); }
  
  public boolean isPresentAsArchive()
  {
    return game.getGameSet().assetCache().isPresent(game, asset);
  }
  
  public boolean isPresentAsFile()
  {
    Path finalPath = getFinalPath();
    try
    {
      return Files.exists(finalPath) && (!asset.hasCRC() || FileUtils.calculateCRCFast(finalPath) == crc);
    } 
    catch (IOException e)
    {
      e.printStackTrace();
      return false;
    } 
  }
  
  public ImageIcon asImage()
  {
    if (isPresentAsFile())
    {
      return new ImageIcon(getFinalPath().toString());
    }
    else
    {
      try
      {
        Path archivePath = game.getGameSet().getAssetPath(asset,true);
        ZipFile zip = new ZipFile(archivePath.toFile());
        FileHeader header = zip.getFileHeader(path.toString());
        return new ImageIcon(ImageIO.read(zip.getInputStream(header)));
      }
      catch (ZipException|IOException e)
      {
        e.printStackTrace();
        return null;
      }
    }
  }
}
