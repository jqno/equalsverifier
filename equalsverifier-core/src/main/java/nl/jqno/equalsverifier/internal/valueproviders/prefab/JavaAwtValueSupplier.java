package nl.jqno.equalsverifier.internal.valueproviders.prefab;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.util.Optional;

import nl.jqno.equalsverifier.internal.reflection.Tuple;

public class JavaAwtValueSupplier<T> extends ValueSupplier<T> {

    private static final int CS_RED = ColorSpace.CS_sRGB;
    private static final int CS_BLUE = ColorSpace.CS_LINEAR_RGB;

    public JavaAwtValueSupplier(Class<T> type) {
        super(type);
    }

    @Override
    public Optional<Tuple<T>> get() {
        if (is(Color.class)) {
            return val(Color.RED, Color.BLUE, Color.RED);
        }
        if (is(ColorSpace.class)) {
            return val(ColorSpace.getInstance(CS_RED), ColorSpace.getInstance(CS_BLUE), ColorSpace.getInstance(CS_RED));
        }
        if (is(ICC_ColorSpace.class)) {
            return val(
                ICC_ColorSpace.getInstance(CS_RED),
                ICC_ColorSpace.getInstance(CS_BLUE),
                ICC_ColorSpace.getInstance(CS_RED));
        }
        if (is(ICC_Profile.class)) {
            return val(
                ICC_Profile.getInstance(CS_RED),
                ICC_Profile.getInstance(CS_BLUE),
                ICC_Profile.getInstance(CS_RED));
        }
        if (is(Font.class)) {
            return val(
                new Font(Font.SERIF, Font.PLAIN, 10),
                new Font(Font.SANS_SERIF, Font.PLAIN, 12),
                new Font(Font.SERIF, Font.PLAIN, 10));
        }
        if (is(Image.class)) {
            var red = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
            var blue = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
            return val(red, blue, red);
        }

        return Optional.empty();
    }
}
