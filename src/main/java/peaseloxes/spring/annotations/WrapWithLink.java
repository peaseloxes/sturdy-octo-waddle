package peaseloxes.spring.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.hateoas.Link;

/**
 * @author peaseloxes
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface WrapWithLink {

    // TODO add annotation processor to test annotation usage at compile time

    /**
     * The path to use.
     * <p>
     * Defaults to the method path.
     *
     * @return the link desired.
     */
    String link() default "";

    /**
     * The relation type.
     * <p>
     * Defaults to {@linkplain Type#DEFAULT}.
     *
     * @return a relation type.
     */
    Type rel() default Type.DEFAULT;

    /**
     * Relation types.
     *
     * @see Type#rel()
     * @see Type#link(String)
     */
    enum Type {

        /**
         * A set of default links.
         * <ul>
         * <li>{@linkplain #SELF}</li>
         * <li>{@linkplain #NEXT}</li>
         * <li>{@linkplain #PREV}</li>
         * <li>{@linkplain #UPDATE}</li>
         * <li>{@linkplain #DELETE}</li>
         * </ul>
         */
        DEFAULT("") {
            @Override
            public Link[] link(final String path) {
                return new Link[]{
                        new Link(path, SELF.rel()),
                        new Link(path, NEXT.rel()),
                        new Link(path, PREV.rel()),
                        new Link(path, UPDATE.rel()),
                        new Link(path, DELETE.rel()),
                };
            }
        },

        /**
         * The self relation.
         */
        SELF("self"),

        /**
         * The next relation.
         */
        NEXT("next"),

        /**
         * The prev relation.
         */
        PREV("prev"),

        /**
         * The update relation.
         */
        UPDATE("update"),

        /**
         * The post relation.
         */
        POST("post"),

        /**
         * The delete relation.
         */
        DELETE("delete");

        private final String relName;

        /**
         * Set the name to be used in the link relationship.
         *
         * @param relName the relationship name.
         */
        Type(final String relName) {
            this.relName = relName;
        }

        /**
         * Return the rel name that should be used in the hateoas link object.
         *
         * @return the relationship name.
         */
        public String rel() {
            return relName;
        }

        /**
         * Create links for this type.
         *
         * @param path the path to use.
         * @return links based on the path and the relations specified in the type.
         */
        public Link[] link(final String path) {
            return new Link[]{
                    new Link(path, relName)
            };
        }
    }
}
