package md.onemap.harta.tile;

import md.onemap.harta.loader.OsmLoader;
import md.onemap.harta.properties.Props;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created by sergpank on 02.01.2018.
 */
public class TileGeneratorOSM extends TileGenerator
{
  private static Logger LOG = LoggerFactory.getLogger(TileGeneratorOSM.class);

  public static void main(String[] args)
  {
    DOMConfigurator.configure("log4j.xml");

    TileGeneratorOSM generator = new TileGeneratorOSM();
    generator.generate();
  }

  public TileGeneratorOSM()
  {
    super(new OsmLoader());
  }

  @Override
  public void generate()
  {
    loader.load(Props.osmFile());

    LOG.info("Area bounds: " + loader.getBounds());

    LocalDateTime generationStart = LocalDateTime.now();

    generateLevels();

    LOG.info("{} seconds", Duration.between(generationStart, LocalDateTime.now()).getSeconds());
  }
}
