package org.simbrain.world.imageworld.filters;

import org.simbrain.world.imageworld.ImageSource;

/**
 * FlipImageFilter vertically flips an ImageSource for passing BufferedImages between y-up and y-down
 * coordinate systems.
 * @author Tim Shea
 */
public class FlipImageFilter extends ImageFilter {
    /**
     * Construct a new FlipImageFilter to vertically flip each image generated by the ImageSource.
     * @param source the source on which to apply the filter
     * @param width the width of the flipped output image
     * @param height the height of the flipped output image
     */
    public FlipImageFilter(ImageSource source, int width, int height) {
        super(source, ImageFilter.getIdentityOp(), width, height);
    }

    @Override
    public void onResize(ImageSource source) {
        float scaleX = (float) getWidth() / source.getWidth();
        float scaleY = -(float) getHeight() / source.getHeight();
        setScaleOp(getScaleOp(scaleX, scaleY, true));
    }
}