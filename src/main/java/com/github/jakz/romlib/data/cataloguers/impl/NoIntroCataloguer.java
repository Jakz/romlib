package com.github.jakz.romlib.data.cataloguers.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.jakz.romlib.data.cataloguers.GameCataloguer;
import com.github.jakz.romlib.data.cataloguers.LambdaCataloguer;
import com.github.jakz.romlib.data.game.Date;
import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.Language;
import com.github.jakz.romlib.data.game.Location;
import com.github.jakz.romlib.data.game.Rom;
import com.github.jakz.romlib.data.game.VersionNumber;
import com.github.jakz.romlib.data.game.Version;
import com.github.jakz.romlib.data.game.VideoFormat;
import com.github.jakz.romlib.data.game.attributes.GameAttribute;
import com.github.jakz.romlib.data.platforms.GBC;

public class NoIntroCataloguer implements GameCataloguer
{
  private static class Rule
  {
    public final Predicate<Game> precondition;
    public final Consumer<Game> lambda;
    
    Rule(Predicate<Game> precondition, Consumer<Game> lambda)
    {
      this.precondition = precondition;
      this.lambda = lambda;
    }
    
    Rule(Consumer<Game> lambda)
    {
      this(g -> true, lambda);
    }
  }
  
  private static final Map<String, Rule> mappers;

  private static final List<LambdaCataloguer> lambdas;
  
  public static boolean addImplicitLanguagesForLocation = true;

  static
  {
    mappers = new HashMap<>();
    mappers.put("World", new Rule(game -> game.getLocation().add(Location.WORLD)));
    mappers.put("Asia", new Rule(game -> game.getLocation().add(Location.ASIA)));
    mappers.put("Scandinavia", new Rule(game -> {
      game.getLocation().add(Location.SCANDINAVIA);
      
      if (addImplicitLanguagesForLocation)
        game.getLanguages().add(Language.FINNISH, Language.SWEDISH, Language.DANISH, Language.NORWEGIAN);
    }));
    mappers.put("USA", new Rule(game -> { 
      game.getLocation().add(Location.USA);
      game.getLanguages().add(Language.ENGLISH);
    }));
    mappers.put("Japan", new Rule(game -> {
      game.getLocation().add(Location.JAPAN);
      game.getLanguages().add(Language.JAPANESE);
    }));
    mappers.put("Europe", new Rule(game -> game.getLocation().add(Location.EUROPE)));
    mappers.put("Italy", new Rule(game -> game.getLocation().add(Location.ITALY)));
    mappers.put("France", new Rule(game -> game.getLocation().add(Location.FRANCE)));
    mappers.put("Germany", new Rule(game -> game.getLocation().add(Location.GERMANY)));
    mappers.put("Spain", new Rule(game -> game.getLocation().add(Location.SPAIN)));
    mappers.put("Poland", new Rule(game -> game.getLocation().add(Location.POLAND)));
    mappers.put("Portugal", new Rule(game -> game.getLocation().add(Location.PORTUGAL)));
    mappers.put("Canada", new Rule(game -> game.getLocation().add(Location.CANADA)));
    mappers.put("Korea", new Rule(game -> { 
      game.getLocation().add(Location.KOREA);
      game.getLanguages().add(Language.KOREAN);
    }));
    mappers.put("Sweden", new Rule(game -> game.getLocation().add(Location.SWEDEN)));
    mappers.put("China", new Rule(game -> game.getLocation().add(Location.CHINA)));
    mappers.put("Australia", new Rule(game -> game.getLocation().add(Location.AUSTRALIA)));
    mappers.put("Brazil", new Rule(game -> game.getLocation().add(Location.BRASIL)));
    mappers.put("Netherlands", new Rule(game -> game.getLocation().add(Location.NETHERLANDS)));
    mappers.put("Russia", new Rule(game -> game.getLocation().add(Location.RUSSIA)));
    mappers.put("Norway", new Rule(game -> game.getLocation().add(Location.NORWAY)));
    mappers.put("Croatia", new Rule(game -> game.getLocation().add(Location.CROATIA)));
    mappers.put("Taiwan", new Rule(game -> game.getLocation().add(Location.TAIWAN)));
    mappers.put("Hong Kong", new Rule(game -> game.getLocation().add(Location.HONG_KONG)));
    mappers.put("Greece", new Rule(game -> game.getLocation().add(Location.GREECE)));
    mappers.put("Hungary", new Rule(game -> game.getLocation().add(Location.HUNGARY)));
    mappers.put("Ukraine", new Rule(game -> game.getLocation().add(Location.UKRAINE)));
    mappers.put("Belgium", new Rule(game -> game.getLocation().add(Location.BELGIUM)));
    mappers.put("Singapore", new Rule(game -> game.getLocation().add(Location.SINGAPORE)));
    mappers.put("Slovakia", new Rule(game -> game.getLocation().add(Location.SLOVAKIA)));
    mappers.put("Slovenia", new Rule(game -> game.getLocation().add(Location.SLOVENIA)));
    mappers.put("India", new Rule(game -> game.getLocation().add(Location.INDIA)));
    mappers.put("UK", new Rule(game -> game.getLocation().add(Location.UNITED_KINGDOM)));
    mappers.put("Israel", new Rule(game -> game.getLocation().add(Location.ISRAEL)));
    mappers.put("Thailand", new Rule(game -> game.getLocation().add(Location.THAILAND)));

    
    mappers.put("Unknown", new Rule(game -> {})); // maybe a Location.UNKNOWN should be used?

    mappers.put("En", new Rule(game -> game.getLanguages().add(Language.ENGLISH)));
    mappers.put("It", new Rule(game -> game.getLanguages().add(Language.ITALIAN)));
    mappers.put("Fr", new Rule(game -> game.getLanguages().add(Language.FRENCH)));
    mappers.put("De", new Rule(game -> game.getLanguages().add(Language.GERMAN)));
    mappers.put("Es", new Rule(game -> game.getLanguages().add(Language.SPANISH)));
    mappers.put("Ko", new Rule(game -> game.getLanguages().add(Language.KOREAN)));
    mappers.put("Pt", new Rule(game -> game.getLanguages().add(Language.PORTUGUESE)));
    mappers.put("Nl", new Rule(game -> game.getLanguages().add(Language.DUTCH)));
    mappers.put("Ja", new Rule(game -> game.getLanguages().add(Language.JAPANESE)));
    mappers.put("No", new Rule(game -> game.getLanguages().add(Language.NORWEGIAN)));
    mappers.put("Sv", new Rule(game -> game.getLanguages().add(Language.SWEDISH)));
    mappers.put("Fi", new Rule(game -> game.getLanguages().add(Language.FINNISH)));
    mappers.put("Da", new Rule(game -> game.getLanguages().add(Language.DANISH)));
    mappers.put("Ca", new Rule(game -> game.getLanguages().add(Language.CATALAN)));
    mappers.put("Zh", new Rule(game -> game.getLanguages().add(Language.CHINESE)));
    mappers.put("Hr", new Rule(game -> game.getLanguages().add(Language.CROATIAN)));
    mappers.put("Ru", new Rule(game -> game.getLanguages().add(Language.RUSSIAN)));
    mappers.put("Pl", new Rule(game -> game.getLanguages().add(Language.POLISH)));
    mappers.put("Ru", new Rule(game -> game.getLanguages().add(Language.RUSSIAN)));
    mappers.put("El", new Rule(game -> game.getLanguages().add(Language.GREEK)));
    mappers.put("Hu", new Rule(game -> game.getLanguages().add(Language.HUNGARIAN)));
    mappers.put("Tr", new Rule(game -> game.getLanguages().add(Language.TURKISH)));
    mappers.put("Hi", new Rule(game -> game.getLanguages().add(Language.HINDI)));
    mappers.put("Cs", new Rule(game -> game.getLanguages().add(Language.CZECH)));
    mappers.put("Sl", new Rule(game -> game.getLanguages().add(Language.SLOVENE)));
    mappers.put("Sk", new Rule(game -> game.getLanguages().add(Language.SLOVAK)));
    mappers.put("He", new Rule(game -> game.getLanguages().add(Language.HEBREW)));
    mappers.put("Th", new Rule(game -> game.getLanguages().add(Language.THAI)));

        
    /* TODO: verify that settng the version doesn't override another one in case
     * of multiple versions. Actually maybe it would be better to manage version
     * as List<Version>
     */    
    
    Predicate<Game> versionUntouched = g -> {
      boolean correct = g.getVersion() == Version.PROPER;
      //if (!correct)
      //  throw new IllegalArgumentException("Version should be untouched for "+g.getTitle());
      return correct;
    };
    
    mappers.put("Demo", new Rule(versionUntouched, game -> game.setVersion(Version.DEMO)));
    mappers.put("OEM", new Rule(versionUntouched, game -> game.setVersion(Version.OEM)));
    mappers.put("Sample", new Rule(versionUntouched, game -> game.setVersion(Version.SAMPLE)));
    mappers.put("Proto", new Rule(versionUntouched, game -> game.setVersion(Version.PROTO)));
    mappers.put("Promo", new Rule(versionUntouched, game -> game.setVersion(Version.PROMO)));
    mappers.put("Rerelease",new Rule(versionUntouched, game -> game.setVersion(Version.RERELEASE)));
    
    mappers.put("Preview",new Rule(versionUntouched, game -> game.setVersion(new Version.Custom("Preview"))));
    mappers.put("Preview Beta",new Rule(versionUntouched, game -> game.setVersion(new Version.Custom("Preview Beta"))));
    mappers.put("Prerelease",new Rule(versionUntouched, game -> game.setVersion(new Version.Custom("Prerelease"))));

    
    
    mappers.put("Unl", new Rule(game -> game.setLicensed(false)));

    mappers.put("NTSC",new Rule( game -> game.setVideoFormat(VideoFormat.NTSC)));
    mappers.put("PAL", new Rule(game -> game.setVideoFormat(VideoFormat.PAL)));

    mappers.put("GB Compatible", new Rule(game -> game.setCustomAttribute(GBC.Attribute.GB_COMPATIBLE, true)));
    mappers.put("SGB Enhanced", new Rule(game -> game.setCustomAttribute(GBC.Attribute.SGB_ENHANCED, true)));
    mappers.put("Rumble Version", new Rule(game -> game.setCustomAttribute(GBC.Attribute.RUMBLE_VERSION, true)));

    mappers.put("[b]", new Rule(game -> game.setAttribute(GameAttribute.BAD_DUMP, true)));


    /* PSP coll(new Ruleections */
    mappers.put("PSP Essentials", new Rule(game -> game.setAttribute(GameAttribute.COLLECTION, "PSP Essentials")));
    mappers.put("Platinum", new Rule(game -> game.setAttribute(GameAttribute.COLLECTION, "Platinum")));
    mappers.put("PSP The Best", new Rule(game -> game.setAttribute(GameAttribute.COLLECTION, "PSP The Best")));

    
    lambdas = new ArrayList<>();
    
    lambdas.add((token,game) -> {
      boolean valid = token.startsWith("Alt");   
      if (valid)
        game.setVersion(new Version.Revision(token));
      return valid;
    });
  
    lambdas.add((token,game) -> {
      boolean valid = token.startsWith("Rev ");   
      if (valid)
        game.setVersion(new Version.Revision(token));
      return valid;
    });
    
    lambdas.add((token,game) -> {
      boolean valid = token.startsWith("Beta");   
      if (valid)
        game.setVersion(new Version.Beta(token));
      return valid;
    });
    
    lambdas.add(
      new LambdaCataloguer() {
        private final Pattern versionPattern = Pattern.compile("^v([0-9]+)\\.([0-9]+)(\\.?[a-zA-Z0-9]*)?$");
        
        @Override
        public boolean catalogue(String token, Game game)
        {
          Matcher matcher = versionPattern.matcher(token);
          boolean matched = matcher.find();
          
          if (matched)
          {
            int major = Integer.parseInt(matcher.group(1));
            //int minor = Integer.parseInt(matcher.group(2));
            String suffix = matcher.group(3);
            game.setVersionNumber(new VersionNumber(major, matcher.group(2), suffix));
          }
          
          return matched;
        }
        
      }
    );
    
    lambdas.add(
      new LambdaCataloguer() {
        private final Pattern datePattern = Pattern.compile("^([1-2][0-9]{3})(?:-(1[0-2]|0?[1-9]))?(?:-([0-3][0-9]))?$");
        
        @Override
        public boolean catalogue(String token, Game game)
        {
          Matcher matcher = datePattern.matcher(token);
          boolean matched = matcher.find();
          
          if (matched)
          {       
            int year = Integer.parseInt(matcher.group(1));
            int month = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : -1;
            int day = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : -1;
            
            Date date = new Date(year, month, day);
              
            //TODO: set attribute
          }
          
          return matched;
        }
      });
        
    
  }
  
  private final Map<String, List<String>> unknownTokens;
  
  public NoIntroCataloguer()
  {
    unknownTokens = new TreeMap<>();
  }
  
  public void catalogue(Game game)
  {
    String title = game.getTitle();

    int firstParen = title.indexOf('(');

    if (firstParen != -1)
    {
      Arrays.stream(title.substring(firstParen).split("\\(|\\)")).filter(s -> !s.isEmpty()).map(s -> s.trim()).forEach(s -> {
        Arrays.stream(s.split(",")).map(t -> t.trim()).filter(t -> !t.isEmpty()).forEach(t -> {
          Rule rule = mappers.get(t);
          
          if (rule != null)
          {
            if (rule.precondition.test(game))
            {
              Consumer<Game> mapper = rule.lambda;
              mapper.accept(game);
            }
            
            return;
          }
          
          for (LambdaCataloguer lambda : lambdas)
          {
            if (lambda.catalogue(t, game))
              return;
          }
          
          //if (game.getLocation().isJust(Location.EUROPE) && game.getLanguages().empty())
          //  game.getLanguages().add(Language.ENGLISH_UK);
          
          List<String> games = unknownTokens.computeIfAbsent(t, kk -> new ArrayList<>());
          games.add(game.getTitle());
        }); 
      }); 
    }
  }
  
  public void catalogue(Rom rom)
  {
    String title = rom.name;

    int firstParen = title.indexOf('(');
    
    if (firstParen != -1)
    {
      Arrays.stream(title.substring(firstParen).split("\\(|\\)")).filter(s -> !s.isEmpty()).map(s -> s.trim()).forEach(s -> {
        Arrays.stream(s.split(",")).map(t -> t.trim()).filter(t -> !t.isEmpty()).forEach(t -> {
          if (t.startsWith("Track "))
          {
            //TODO: set track attribute
          }
        });
      });
    }
  }
  
  @Override public void done() { printAddendums(); }
  
  public void printAddendums()
  {    
    unknownTokens.forEach((k,v) -> {
      System.out.println(k+":");
      v.forEach(g -> System.out.println("   "+g));
    });
  }
}
