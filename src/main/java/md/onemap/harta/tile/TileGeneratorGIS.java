package md.onemap.harta.tile;

import md.onemap.harta.loader.PostgisLoader;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created by sergpank on 02.01.2018.
 */
public class TileGeneratorGIS extends TileGenerator
{
  private static Logger LOG = LoggerFactory.getLogger(TileGeneratorGIS.class);

  public static void main(String[] args)
  {
    DOMConfigurator.configure("log4j.xml");

    GeneratorProperties properties = new GeneratorProperties("properties/db-generator.properties");
    TileGeneratorGIS generator = new TileGeneratorGIS(properties);
    generator.generate();
  }

  public TileGeneratorGIS(GeneratorProperties props) {
    super(props, new PostgisLoader(props.source()));
  }

  @Override
  public void generate()
  {
    LOG.info("Dataset bounds: " + loader.getBounds());

    LocalDateTime generationStart = LocalDateTime.now();

    generateLevels();

    LOG.info("{} seconds", Duration.between(generationStart, LocalDateTime.now()).getSeconds());
  }
}
