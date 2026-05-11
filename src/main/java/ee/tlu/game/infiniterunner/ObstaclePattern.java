package ee.tlu.game.infiniterunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Takistuste muster mängus.
 * Iga muster koosneb mitmest takistusest koos vahemaaga nende vahel.
 */
public class ObstaclePattern {
    private final List<ObstacleType> obstacles;
    private final int spacing;

    /**
     * Loob uue takistuste mustri.
     *
     * @param spacing Vahemaa pikslites takistuste vahel selles mustris
     * @param types   Takistuste tüübid selles mustris
     */
    public ObstaclePattern(int spacing, ObstacleType... types) {
        this.obstacles = new ArrayList<>();
        for (ObstacleType type : types) {
            this.obstacles.add(type);
        }
        this.spacing = spacing;
    }

    /**
     * Tagastab takistuste loendi selles mustris.
     *
     * @return Takistuste tüüpide loend
     */
    public List<ObstacleType> getObstacles() {
        return obstacles;
    }

    /**
     * Tagastab vahemaa takistuste vahel selles mustris.
     *
     * @return Vahemaa pikslites
     */
    public int getSpacing() {
        return spacing;
    }
}
