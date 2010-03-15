package edu.wustl.common.factory;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.wustl.common.bizlogic.InputUIRepOfDomain;
import edu.wustl.common.bizlogic.UIDomainTransformer;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.domain.UIRepOfDomain;

/**
 * Registers transformers given the list of transformer; alternatively,
 * subclasses can manually register the transformers by initing with default
 * constructor and then calling <code>addTransformer</code>.
 * <p>
 * If a specified class doesn't implement {@link UIDomainTransformer}, an
 * exception is raised.
 * <p>
 * Note that a "transformer" must additionally
 * <ul>
 * <li>be a non-abstract class,</li>
 * <li>have a default constructor, and</li>
 * <li>be annotated with {@link InputUIRepOfDomain}</li>
 * </ul>
 * A class that doesn't satisfy these constraints is ignored (not registered). *
 * 
 * @author srinath_k
 * 
 */
public class AbstractTransformerFactory implements ITransformerFactory {

    private static Map<Class<? extends UIRepOfDomain>, Class<? extends UIDomainTransformer>> transformerMap = new HashMap<Class<? extends UIRepOfDomain>, Class<? extends UIDomainTransformer>>();

    /**
     * Determines the {@link UIRepOfDomain} for each transformer using its
     * {@link InputUIRepOfDomain} annotation; and this registers all the
     * transformers. Note that if an input transformer does not have a valid
     * {@link InputUIRepOfDomain} annotation, then it is ignored (not
     * registered).
     * 
     * @throws IllegalArgumentException multiple transformers compete for a
     *             {@link UIRepOfDomain}.
     */
    protected AbstractTransformerFactory(List<Class<? extends UIDomainTransformer>> classes) {
        for (Class<? extends UIDomainTransformer> trans : classes) {
            Class<? extends UIRepOfDomain> uiRep = getUIRep(trans);
            if (uiRep != null)
                addTransformer(uiRep, trans);
        }
    }

    /**
     * if subclasses want to register transformers "manually"
     */
    protected AbstractTransformerFactory() {

    }

    /**
     * if subclasses want to register transformers "manually". Note that if
     * specified transformer is an abstract class, then it is ignored.
     * 
     * @return <code>true</code> if the transformer was registered;
     *         <code>false</code> otherwise (a transformer won't be registered
     *         if it doesn't satisfy the constraints mentioned (see class level
     *         comments)).
     * @throws IllegalArgumentException if an attempt is made to register more
     *             than one transformer for a {@link UIRepOfDomain}
     */
    protected final boolean addTransformer(Class<? extends UIRepOfDomain> uiRep,
            Class<? extends UIDomainTransformer> trans) {
        if (!isTransformer(trans)) {
            // not sure if this can occur??
            throw new IllegalArgumentException(trans + "is not a transformer.");
        }
        if (isAbstract(trans)) {
            return false;
        }
        Class<? extends UIDomainTransformer> existingTrans = transformerMap.put(uiRep, trans);
        if (existingTrans != null) {
            throw new IllegalArgumentException("Duplicate mapping for " + uiRep + ": " + trans + " and "
                    + existingTrans);
        }

        return true;
    }

    /**
     * @return all transformers as a <code>map</code> of UIRep -> Transformer;
     *         the factory is unaffected by changes to returned <code>map</code>.
     */
    protected final Map<Class<? extends UIRepOfDomain>, Class<? extends UIDomainTransformer>> getAllTransformers() {
        return new HashMap<Class<? extends UIRepOfDomain>, Class<? extends UIDomainTransformer>>(transformerMap);
    }

    protected static boolean isTransformer(Class<?> c) {
        return UIDomainTransformer.class.isAssignableFrom(c);
    }

    /**
     * @see edu.wustl.common.factory.ITransformerFactory#getTransformer(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public final UIDomainTransformer<UIRepOfDomain, AbstractDomainObject> getTransformer(
            Class<? extends UIRepOfDomain> uiClass) {
        try {
            Class<?> c = transformerMap.get(uiClass);
            if (c == null) {
                throw new IllegalArgumentException("No transformer found for " + uiClass);
            }
            return (UIDomainTransformer<UIRepOfDomain, AbstractDomainObject>) c.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isAbstract(Class<?> c) {
        return c.isInterface() || Modifier.isAbstract(c.getModifiers());
    }

    private static Class<? extends UIRepOfDomain> getUIRep(Class<? extends UIDomainTransformer> c) {
        InputUIRepOfDomain a = c.getAnnotation(InputUIRepOfDomain.class);
        if (a == null)
            return null;

        return a.value();
    }
}
