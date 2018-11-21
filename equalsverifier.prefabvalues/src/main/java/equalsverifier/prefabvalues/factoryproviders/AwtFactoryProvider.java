package equalsverifier.prefabvalues.factoryproviders;

import equalsverifier.prefabvalues.FactoryCache;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;

import static equalsverifier.prefabvalues.factories.Factories.values;

public final class AwtFactoryProvider implements FactoryProvider {

    private static final int CS_RED = ColorSpace.CS_sRGB;
    private static final int CS_BLACK = ColorSpace.CS_LINEAR_RGB;

    public FactoryCache getFactoryCache() {
        FactoryCache cache = new FactoryCache();

        cache.put(
            Color.class,
            values(Color.RED, Color.BLACK, Color.RED));
        cache.put(
            ColorSpace.class,
            values(ColorSpace.getInstance(CS_RED), ColorSpace.getInstance(CS_BLACK), ColorSpace.getInstance(CS_RED)));
        cache.put(
            ICC_ColorSpace.class,
            values(ICC_ColorSpace.getInstance(CS_RED), ICC_ColorSpace.getInstance(CS_BLACK), ICC_ColorSpace.getInstance(CS_RED)));
        cache.put(
            ICC_Profile.class,
            values(ICC_Profile.getInstance(CS_RED), ICC_Profile.getInstance(CS_BLACK), ICC_Profile.getInstance(CS_RED)));

        return cache;
    }
}
