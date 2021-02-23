package nl.jqno.equalsverifier.internal.prefabvalues.factoryproviders;

import static nl.jqno.equalsverifier.internal.prefabvalues.factories.Factories.values;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import nl.jqno.equalsverifier.internal.prefabvalues.FactoryCache;

public final class AwtFactoryProvider implements FactoryProvider {

    private static final int CS_RED = ColorSpace.CS_sRGB;
    private static final int CS_BLUE = ColorSpace.CS_LINEAR_RGB;

    public FactoryCache getFactoryCache() {
        FactoryCache cache = new FactoryCache();

        cache.put(Color.class, values(Color.RED, Color.BLUE, Color.RED));
        cache.put(
            ColorSpace.class,
            values(
                ColorSpace.getInstance(CS_RED),
                ColorSpace.getInstance(CS_BLUE),
                ColorSpace.getInstance(CS_RED)
            )
        );
        cache.put(
            ICC_ColorSpace.class,
            values(
                ICC_ColorSpace.getInstance(CS_RED),
                ICC_ColorSpace.getInstance(CS_BLUE),
                ICC_ColorSpace.getInstance(CS_RED)
            )
        );
        cache.put(
            ICC_Profile.class,
            values(
                ICC_Profile.getInstance(CS_RED),
                ICC_Profile.getInstance(CS_BLUE),
                ICC_Profile.getInstance(CS_RED)
            )
        );
        cache.put(
            Font.class,
            values(
                new Font(Font.SERIF, Font.PLAIN, 10),
                new Font(Font.SANS_SERIF, Font.PLAIN, 12),
                new Font(Font.SERIF, Font.PLAIN, 10)
            )
        );
        Image red = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Image blue = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        cache.put(Image.class, values(red, blue, red));

        return cache;
    }
}
