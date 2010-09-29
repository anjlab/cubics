package anjlab.cubics.renders;

import anjlab.cubics.Cube;
import anjlab.cubics.renders.html.HtmlRender;
import anjlab.cubics.renders.html.HtmlRender2;

/**
 * {@link Cube} render.
 * 
 * @author dmitrygusev
 *
 * @param <T> Type of the fact record.
 * @param <R> Type of rendering result.
 * 
 * @see HtmlRender
 * @see HtmlRender2
 */
public interface Render<T, R> {

    /**
     * Renders <code>cube</code> instance into instance specific layout.
     */
    public abstract R render();

    /**
     * Gets {@link Options} instance that controls aggregates appearance 
     * of the specified <code>measure</code>.
     * 
     * @param measure Measure name.
     * @return Options for <code>measure</code>
     */
    public abstract Options<T> getAggregatesOptions(String measure);

    /**
     * Gets {@link Options} instance that controls measures appearance.
     * 
     * {@link Options#setFormat(String, String)} is ignored by render.
     * 
     * @return Options for measures.
     */
    public abstract Options<T> getMeasuresOptions();

    /**
     * Gets {@link Options} instance that controls dimensions appearance.
     * 
     * {@link Options#setFormat(String, String)} usually ignored by render.
     * {@link Options#reorder(String...)} usually ignored by render.
     * {@link Options#exclude(String...)} usually ignored by render.
     * 
     * @return Options for dimensions.
     */
    public abstract Options<T> getDimensionsOptions();

}