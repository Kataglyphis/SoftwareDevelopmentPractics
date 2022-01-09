package edu.kit.valaris.generation.roadgeneration;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import edu.kit.valaris.generation.GeneratorSettings;

import java.util.Set;



/**
 * Schnittstelle zur Generierung eines Streckenabschnitts. Ein ausgegebener Streckenabschnitt begint
 * immer am Startpunkt (0,0,0) und Verläuft am Start in Z-Richtung.
 * @author Sidney Hansen
 */
public interface IRoadGenerator {

    /**
     * Erzeugt eine Strecke in einer Halbkugel, gegeben durch den Radius (radius).
     * Die Strecke beginnt am Rande der Halbkugel.
     * Danach nimmt sie einen scheinbar belibigen Verlauf innerhalb der Halbkugel
     * an. Die Ausprägung des Verlaufs hängt vom Biom (biom) ab.
     * Die Strecke terminiert wieder am Rande der Halbkugel.
     * Die Strecke kollidiert nie mit sich selber.
     *
     * @param seed       Zum bestimmen von Pseuozufallswerten, die den Verlauf der
     *                   Strecke bestimmen.
     * @param radius     Radius der Halbkugel.
     * @param biom       Bestimmt Ausprägung der Stecke.
     * @param entryWidth Bestimmt den Querschnitt der Strecke am Eingang.
     * @param exitAngle  Zahl zwischen [-Pi,PI]. Präferierter Winkel zwischen Einund
     *                   Ausgang.
     * @param flags      Kollektion an Parametern,
     *                   die als Präferenzen zur Generierung dienen.
     * @return Datenstruktur (Road), die eine Strecke beschreibt.
     */
    public Road generateSphereRoad(int seed, float radius, String biom, Vector2f entryWidth, float exitAngle,
                                   Set<GeneratorSettings> flags);


    /**
     * Erzeugt eine Verbindungsstrecke vom Startpunkt zum relativen Zielpunkt
     * (RoadCursor target).
     *
     * @param seed       Zum bestimmen von Pseuozufallswerten, die den Verlauf der
     *                   Strecke beeinfussen.
     * @param target     Beschreibt den Zielpunkt der Strecke aus dem Bezugssystem
     *                   des Eingangspunkt
     * @param entryWidth Bestimmt den Querschnitt der Strecke am Eingang.
     * @param settings   Bestimmt welche in der Konfig definierten einstellungen zu verwenden sind.
     * @param flags      Kollektion an Parametern,
     *                   die als Präferenzen zur Generierung dienen.
     * @return Datenstruktur (Road), die eine Strecke beschreibt.
     */
    public Road generateConnectingRoad(int seed, RoadCursor target, Vector2f entryWidth,
                                       String settings, Set<GeneratorSettings> flags);



    /**
     * Erzeugt eine Strecke durch einen Quader, beschrieben durch cuboidDimensions.
     * Ausgang und Eingang benden sich rechtwinklig zu den jeweiligen Wänden
     * des Quaders, an denen sie platziert sind.
     *
     * @param seed             Zum bestimmen von Pseuozufallswerten, die den Verlauf der
     *                         Strecke beeinflussen.
     * @param cuboidDimensions Dimension des Quaders
     * @param entryWidth       Bestimmt den Querschnitt der Strecke am Eingang.
     * @param exitAngle        Zahl zwischen [-Pi,PI]. Präferierter Winkel zwischen Einund
     *                         Ausgang.
     * @param flags            Kollektion an Parametern,
     * @param settings   Bestimmt welche in der Konfig definierten einstellungen zu verwenden sind.
     *                         die als Präferenzen zur Generierung dienen.
     * @return Datenstruktur (Road), die eine Strecke beschreibt.
     */
    public Road generateCuboidRoad(int seed, Vector3f cuboidDimensions, Vector2f entryWidth, float exitAngle,
                                   String settings, Set<GeneratorSettings> flags);

}
