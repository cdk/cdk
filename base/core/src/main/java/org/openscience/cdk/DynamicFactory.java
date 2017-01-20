/*
 * Copyright (C) 2000-2012  John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk;

import org.openscience.cdk.interfaces.ICDKObject;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * A factory class for constructing {@link ICDKObject} and {@link IChemObject}
 * implementations. Instances can be created by registering a construction key
 * ({@link ConstructorKey}) with a corresponding creator ({@link Creator}). In
 * most cases a class can simply be registered by providing an interface and an
 * implementation {@link #register(Class, Class)}.
 *
 * Internally the factory stores the object creators in a symbol table which
 * allows near constant time access for all registered classes. 
 *
 * In cases of a non-direct parameter match (e.g. {@code Atom(Atom)} can resolve
 * to {@code Atom(Element)}) the constructor is matched by invoking {@link
 * #find(ConstructorKey)}. If a constructor was found the new key will be
 * registered with cache to avoid the overhead of finding the correct
 * constructor again. 
 *
 * <pre>{@code
 *
 *     // create an instance of the factory
 *     DynamicFactory factory = new DynamicFactory(5);
 *
 *     // register some implementations
 *     factory.register(IAtom.class,          Atom.class);
 *     factory.register(IElement.class,       Element.class);
 *     factory.register(IAtomContainer.class, AtomContainer.class);
 *
 *     // create an instance using the default constructor
 *     IAtom a1 = factory.ofClass(IAtom.class);
 *
 *     // create an instance using a parametrised constructor
 *     IAtom c  = factory.ofClass(IAtom.class, "C");
 *
 *     // using a custom creator to actually invoke a constructor
 *     // import static org.openscience.cdk.DynamicFactory.key;
 *     // import static org.openscience.cdk.DynamicFactory.BasicCreator;
 *     factory.register(key(IBond.class, IAtom[].class),
 *                      new BasicCreator<IAtom>(null) {
 *                          public IAtom create(Object[] objects) {
 *                              return new Bond((IAtom[]) objects);
 *                          }
 *                      });
 *
 * }</pre>
 *  It is not always convenient to specify a custom {@link Creator} for
 * every construction type but the objects may still need some modification
 * directly after creation. The factory provides {@link CreationModifier} which
 * is invoked directly after object creation. This allows changing the default
 * of creation. As an example it is possible to set a non-null charge on all
 * atoms that are created. 
 *
 * <pre>{@code
 *      // import static org.openscience.cdk.DynamicFactory.CreationModifier;
 *     factory.register(IAtom.class, Atom.class,
 *                      new CreationModifier<Atom>() {
 *                          public void modify(Atom atom) {
 *                               atom.setFormalCharge(0);
 *                          }
 *                      }));
 * }</pre>
 *
 * 
 *
 * It is also possible change the object creation based on the input parameters.
 * 
 *
 * <pre>{@code
 * factory.register(key(IAtom.class, String.class),
 *                  new DynamicFactory.BasicCreator<IAtom>() {
 *
 *                      public IAtom create(Object[] objects) {
 *                          String symbol = (String) objects[0];
 *                          return "R".equals(symbol)
 *                                      ? new PseudoAtom(symbol)
 *                                      : new Atom(symbol);
 *                      }
 *                  });
 *
 * factory.ofClass(IAtom.class, "C"); // IAtom
 * factory.ofClass(IAtom.class, "R"); // IPseudoAtom
 * }</pre>
 *
 * @author John May
 * @cdk.module core
 * @cdk.githash
 */
public class DynamicFactory {

    /**
     * logger for use in this class
     */
    private static final ILoggingTool             LOGGER            = LoggingToolFactory
                                                                            .createLoggingTool(DynamicFactory.class);

    /**
     * an empty class array which can be used to avoid object creation in
     * default constructors
     */
    private static final Class<?>[]               EMPTY_CLASS_ARRAY = new Class<?>[0];

    /**
     * conversion map of primitives to their boxed equivalents
     */
    private static final Map<Class<?>, Class<?>>  BOXED_EQUIVALENT  = new HashMap<Class<?>, Class<?>>(20);

    // populates the primitive conversion map
    static {
        BOXED_EQUIVALENT.put(int.class, Integer.class);
        BOXED_EQUIVALENT.put(byte.class, Byte.class);
        BOXED_EQUIVALENT.put(short.class, Short.class);
        BOXED_EQUIVALENT.put(long.class, Long.class);
        BOXED_EQUIVALENT.put(boolean.class, Boolean.class);
        BOXED_EQUIVALENT.put(float.class, Float.class);
        BOXED_EQUIVALENT.put(double.class, Double.class);
        BOXED_EQUIVALENT.put(char.class, Character.class);
    }

    /*
     * direct map of keys to constructors
     */
    private final Map<ConstructorKey, Creator<?>> cache;

    /*
     * lookup to help find non-exact constructors and print suggestions
     */
    private final ConstructorLookup               lookup;

    /**
     * provide the interfaces this class implements
     */
    private final InterfaceProvider               interfaceProvider;

    /**
     * Create a new default factory with an expected number of registered
     * classes and an interface provider. The interface provider is used when
     * registering an implementation without a specified interface {@link
     * #register(Class)}.
     *
     * @param interfaceProvider provides the interfaces of a given
     *                          implementation class
     * @param n                 the expected number of constructors that will be
     *                          stored.
     * @see #DynamicFactory(int)
     */
    public DynamicFactory(InterfaceProvider interfaceProvider, int n) {

        if (n < 0) throw new IllegalArgumentException("cannot create factory with negative size");
        if (interfaceProvider == null) throw new IllegalArgumentException("null interface provider");

        this.interfaceProvider = interfaceProvider;

        int size = n > 3 ? n + n / 3 : n + 1;

        // double for the cache to account for subclass constructor caching
        // (e.g. Atom(Atom) -> Atom(Element)
        cache = new HashMap<ConstructorKey, Creator<?>>(size * 2);
        lookup = new ConstructorLookup(size);

    }

    /**
     * Create a new default factory with an expected number of registered
     * classes and a default interface provider. The default interface provider
     * simply invokes {@link Class#getInterfaces()} when registering an
     * implementation without an explicit interface {@link #register(Class)}.
     *
     * @param n the expected number of constructors that will be stored.
     */
    public DynamicFactory(int n) {
        this(new DefaultInterfaceProvider(), n);
    }

    /**
     * Inspects whether the provided class is a {@link ICDKObject} interface.
     *
     * @param c class to test
     * @return whether the class is a CDK interface
     */
    private boolean isCDKInterface(Class<?> c) {
        return ICDKObject.class.isAssignableFrom(c) && c.isInterface();
    }

    /**
     * Inspects whether the provided class is a concrete implementation and thus
     * instantiable.
     *
     * @param c class to test
     * @return whether the class is concrete
     */
    private boolean isConcrete(Class<?> c) {
        return !c.isInterface() && !Modifier.isAbstract(c.getModifiers());
    }

    /**
     * Registers a class with the factory. The interfaces of the class will be
     * checked to see if they are CDK interfaces before they are registered.
     * This method is provided for utility but will register the implementation
     * with all valid interfaces. If a restricted registration is required then
     * the safer {@link #register(Class, Class)} can be used.
     *
     * @param impl a concrete class
     * @throws IllegalArgumentException thrown if a non-concrete class is
     *                                  registered
     * @see #register(Class, Class)
     * @see #register(Class, java.lang.reflect.Constructor)
     * @see #register(org.openscience.cdk.DynamicFactory.ConstructorKey,
     *      org.openscience.cdk.DynamicFactory.Creator)
     */
    public <T extends ICDKObject> boolean register(Class<? extends T> impl) {

        if (!isConcrete(impl)) throw new IllegalArgumentException("non-concrete implementation provided");

        boolean registered = Boolean.FALSE;

        // only check direct interfaces,
        //     IPolymer -> Polymer not IAtomContainer -> Polymer
        for (Class<?> c : interfaceProvider.getInterfaces(impl)) {

            // register this implementation with it's direct interfaces
            if (isCDKInterface(c)) {
                @SuppressWarnings("unchecked")
                Class<T> intf = (Class<T>) c;
                registered = register(intf, impl) || registered;
            }

        }

        return registered;

    }

    /**
     * Explicitly register a concrete class with a provided interface. The
     * {@link Creator} will be automatically created for all public
     * constructors.
     *
     * @param intf the interface class to register
     * @param impl the concrete class which should implement the interface
     *             class
     * @return whether registration was successful
     */
    public <T extends ICDKObject> boolean register(Class<T> intf, Class<? extends T> impl) {
        return register(intf, impl, null);
    }

    /**
     * Explicitly register a concrete class with a provided interface and a
     * given modifier. The {@link Creator} will be automatically created for all
     * public constructors. The modifier is incorporated into the {@link
     * Creator} and is invoked directly after instantiation. 
     *
     *
     * <pre>{@code
     *      // import static org.openscience.cdk.DynamicFactory.CreationModifier;
     *     factory.register(IAtom.class, Atom.class,
     *                      new CreationModifier<Atom>() {
     *                          public void modify(Atom atom) {
     *                               atom.setFormalCharge(0);
     *                          }
     *                      }));
     * }</pre>
     *
     * @param intf     the interface class to register
     * @param impl     the concrete class which should implement the interface
     *                 class
     * @param modifier modify a instance after creation
     * @param <S>      interface type
     * @param <T>      implementation type (must extend interface)
     * @return whether registration was successful
     */
    public <S extends ICDKObject, T extends S> boolean register(Class<S> intf, Class<T> impl,
            CreationModifier<T> modifier) {

        if (!isConcrete(impl)) throw new IllegalArgumentException("attempt to register non-concrete class");

        if (!intf.isInterface())
            throw new IllegalArgumentException("attempt to register a non-interface interface: " + intf.getSimpleName());

        boolean registered = Boolean.FALSE;
        for (Constructor<?> untyped : impl.getConstructors()) {
            @SuppressWarnings("unchecked")
            Constructor<T> typed = (Constructor<T>) untyped;
            registered = register(intf, typed, modifier) || registered;
        }

        if (registered) {
            LOGGER.debug("registered '", intf.getSimpleName(), "' with '", impl.getSimpleName(), "' implementation");
        } else {
            LOGGER.debug("could not registered '", intf.getSimpleName(), "' with '", impl.getSimpleName(),
                    "' implementation");
        }

        return registered;

    }

    /**
     * Register a specific constructor with an explicit interface. 
     *
     * <pre>{@code
     *     // only register construction of IAtom using a string - Atom("C")
     *     factory.register(IAtom.class,
     *                      Atom.class.getConstructor(String.class));
     * }</pre>
     *
     * @param intf        the interface
     * @param constructor a constructor which builds the given interface
     * @param <S>         interface type
     * @param <T>         implementation type (must extend interface)
     * @return whether the constructor was registered
     */
    public <S extends ICDKObject, T extends S> boolean register(Class<S> intf, Constructor<T> constructor) {
        return register(intf, constructor, null);
    }

    /**
     * Register a specific constructor with a creation modifier to an explicit
     * interface. 
     *
     * <pre>{@code
     *      // only register construction of IAtom using a string - Atom("C")
     *      factory.register(IAtom.class,
     *                       Atom.class.getConstructor(String.class),
     *                       new DynamicFactory.CreationModifier<Atom>() {
     *
     *                           public void modify(Atom instance) {
     *                              instance.setFormalCharge(0);
     *                           }
     *                       });
     * }</pre>
     *
     * @param intf        the interface
     * @param constructor a constructor which builds the given interface
     * @param <S>         interface type
     * @param <T>         implementation type (must extend interface)
     * @return whether the constructor was registered
     */
    public <S extends ICDKObject, T extends S> boolean register(Class<S> intf, Constructor<T> constructor,
            CreationModifier<T> modifier) {

        // do not register private/protected constructors
        if (!Modifier.isPublic(constructor.getModifiers())) return Boolean.FALSE;

        return register(key(intf, constructor), constructor, modifier) != null;

    }

    /**
     * Register a constructor key with a public constructor.
     *
     * @param key         the key to register this constructor with
     * @param constructor the constructor to invoke when the key is match
     * @param <T>         the type the constructor will create
     * @return return the constructor passed as the parameter
     */
    private <T> Creator<T> register(ConstructorKey key, Constructor<T> constructor, CreationModifier<T> modifier) {

        Creator<T> creator = new ReflectionCreator<T>(constructor);

        if (modifier != null) creator = new ModifiedCreator<T>(creator, modifier);

        return register(key, creator);
    }

    /**
     * Register a constructor key with a defined {@link Creator}. The key
     * defines the interface and parameters of the creation and the creator
     * actually creates the object. 
     *
     * <pre>{@code
     *     // import static org.openscience.cdk.DynamicFactory.key;
     *     factory.register(key(IBond.class, IAtom[].class),
     *                      new BasicCreator<IAtom>(null) {
     *                          public IAtom create(Object[] objects) {
     *                              return new Bond((IAtom[]) objects);
     *                          }
     *                      });
     * }</pre>
     *
     * @param key construction key, defines interface and parameter types
     * @param creator creates the actual object
     * @param <T> type of object that will be created
     * @return the registered creator - null if not registered
     */
    public <T> Creator<T> register(ConstructorKey key, Creator<T> creator) {

        if (creator == null) return null;

        // make sure we don't register a constructor over an existing key
        if (cache.containsKey(key))
            throw new IllegalArgumentException("cannot register " + key + " suppressed " + cache.get(key));

        lookup.put(key.intf(), key);
        cache.put(key, creator);

        return creator;

    }

    /**
     * Creates a constructor key for use in accessing constructors. The key
     * combines the interface and types in a single instance which we can then
     * use in a map.
     *
     * @param intf        the interface to build the key for
     * @param constructor the constructor value which this key will be linked
     *                    to
     * @return a constructor key which can be used to lookup a constructor
     */
    private static ConstructorKey key(Class<?> intf, Constructor<?> constructor) {
        return key(intf, constructor.getParameterTypes());
    }

    /**
     * Creates a constructor key for use in accessing constructors. The key
     * combines the interface and types in a single instance which we can then
     * use in a map.
     *
     * @param intf  the interface to build the key for
     * @param types the classes that the the constructor requires
     * @return a constructor key which can be used to lookup a constructor
     */
    public static ConstructorKey key(Class<?> intf, Class<?>... types) {
        return new ClassBasedKey(intf, convert(types));
    }

    /**
     * Converts the provided array of classes to a version with 'boxed'
     * primitives. This will convert {int, int, int} to {Integer, Integer,
     * Integer}.
     *
     * @param classes converted classes
     * @return converted types
     */
    private static Class<?>[] convert(Class<?>[] classes) {
        Class<?>[] types = new Class<?>[classes.length];
        for (int i = 0; i < types.length; i++) {
            types[i] = convert(classes[i]);
        }
        return types;
    }

    /**
     * Converts primitive types to their boxed equivalent {@link int} converts
     * to {@link Integer}. If not conversion was found the parameter is returned
     * without modification.
     *
     * @param unboxed the type to convert
     * @return the boxed type
     */
    private static Class<?> convert(Class<?> unboxed) {
        Class<?> boxed = BOXED_EQUIVALENT.get(unboxed);
        return boxed == null ? unboxed : boxed;
    }

    /**
     * Construct an implementation using a constructor whose parameters match
     * that of the provided objects.
     *
     * @param intf the interface to construct an instance of
     * @param <T>  the type of the class
     * @return an implementation of provided interface
     * @throws IllegalArgumentException thrown if the implementation can not be
     *                                  constructed
     * @throws IllegalArgumentException thrown if the provided class is not an
     *                                  interface
     */
    public <T extends ICDKObject> T ofClass(Class<T> intf, Object... objects) {

        try {

            if (!intf.isInterface()) throw new IllegalArgumentException("expected interface, got " + intf.getClass());

            Creator<T> constructor = get(new ObjectBasedKey(intf, objects));
            return constructor.create(objects);

        } catch (InstantiationException e) {
            throw new IllegalArgumentException("unable to instantiate chem object: ", e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("constructor is not accessible: ", e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException("invocation target exception: ", e);
        }

    }

    /**
     * Construct an implementation using the default constructor. This provides
     * some speed boost over invoking {@link #ofClass(Class, Object...)}.
     *
     * @param intf the interface to construct an instance of
     * @param <T>  the type of the class
     * @return an implementation of provided interface constructed using the
     *         default constructor.
     * @throws IllegalArgumentException thrown if the implementation can not be
     *                                  constructed
     * @throws IllegalArgumentException thrown if the provided class is not an
     *                                  interface
     */
    public <T extends ICDKObject> T ofClass(Class<T> intf) {

        try {

            if (!intf.isInterface()) throw new IllegalArgumentException("expected interface, got " + intf.getClass());

            Creator<T> creator = get(new ClassBasedKey(intf, EMPTY_CLASS_ARRAY));
            return creator.create(null); // throws an exception if no impl was found

        } catch (InstantiationException e) {
            throw new IllegalArgumentException("unable to instantiate chem object: ", e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("constructor is not accessible: ", e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException("invocation target exception: ", e);
        }

    }

    /**
     * Access a constructor for a given constructor key. If the key is not found
     * the {@link #find(ConstructorKey)} is automatically invoked.
     *
     * @param key the constructor key of the interface to find
     * @param <T> the type of the constructor
     * @return a constructor for the given key
     * @throws IllegalArgumentException thrown if a key is provided which cannot
     *                                  be resolved.
     */
    @SuppressWarnings("unchecked")
    private <T> Creator<T> get(ConstructorKey key) {

        Creator<T> creator = (Creator<T>) cache.get(key);

        if (creator != null) {
            return creator;
        } else {
            synchronized (lock) {
                // if the creator is still null... try and find one and register it
                if ((creator = (Creator<T>) cache.get(key)) == null) {
                    creator = find(key);
                    creator = register(key, creator); // avoids invoking find again
                }
            }
        }

        return creator;

    }

    /* thread lock for finding new constructors */
    private final Object lock = new Object();

    /**
     * Find a constructor whose parameters are assignable from the provided
     * key.
     *
     * @param key a key to find the constructor for
     * @param <T> type of the constructor
     * @return a constructor compatible with the given key
     * @throws IllegalArgumentException when no constructor is found the given
     *                                  key
     */
    private <T> Creator<T> find(final ConstructorKey key) {

        for (ConstructorKey candidate : lookup.getCandidates(key)) {
            if (key.isAssignable(candidate)) {
                return get(candidate);
            }
        }

        // if the key has uniform parameter types
        if (key.isUniform()) {

            // convert the key parameter types (length doesn't matter)
            Object types = Array.newInstance(key.type(0), 0);
            final ConstructorKey alt = new ObjectBasedKey(key.intf(), new Object[]{types});

            // find the creator for the new alternate key
            Creator<T> creator = get(alt);

            // if found -> get/find will return the correct constructor, we then need
            //             to wrap the constructor with an creator that will
            //             wrap the objects in an array
            // if not found -> the method will return a non-null creator that
            //                 will throw an exception (see. below) - wrapping
            //                 has no harm as the params never get used
            //
            return new ArrayWrapCreator<T>(creator);
        }

        LOGGER.debug("no instance handler found for ", key);

        // return an instance handler that will throw an exception when invoked
        return new Creator<T>() {

            @Override
            public T create(Object[] objects) {
                throw new IllegalArgumentException(getSuggestionMessage(key));
            }

            @Override
            public Class<T> getDeclaringClass() {
                throw new IllegalArgumentException("missing declaring class");
            }
        };

    }

    /**
     * Access the registered implementations for a given interface.
     *
     * @param intf an interface which has registered implementations in the
     *             factory
     * @param <T>  the type of the interface
     * @return set of implementation classes (empty if none found)
     */
    public <T extends ICDKObject> Set<Class<?>> implementorsOf(Class<T> intf) {
        Set<Class<?>> implementations = new HashSet<Class<?>>(5);
        for (ConstructorKey key : lookup.getConstructors(intf)) {
            implementations.add(get(key).getDeclaringClass());
        }
        return implementations;
    }

    /**
     * Provides a list of all possible constructor keys for the provided
     * interface.
     *
     * @param intf an interface to find all constructors for
     * @return an iterator of constructor keys
     */
    public Iterator<ConstructorKey> suggest(Class<?> intf) {
        return lookup.getConstructors(intf).iterator();
    }

    /**
     * Provides a message for use with exceptions when no valid constructor is
     * found. The message is built using the suggestions from {@link
     * #suggest(Class)}.
     *
     * @param key the constructor key to build the message for
     * @return a message listing possible constructors
     */
    private String getSuggestionMessage(ConstructorKey key) {

        StringBuilder sb = new StringBuilder(200);

        sb.append("No constructor found for '").append(key);
        sb.append("' candidates are: ");

        Iterator<ConstructorKey> iterator = suggest(key.intf());

        while (iterator.hasNext()) {
            sb.append(iterator.next());
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }

        return sb.toString();

    }

    /**
     * A simple wrapper class for a HashMap so we can insert and access
     * constructors easier.
     */
    private static class ConstructorLookup {

        private final Map<Class<?>, Map<Integer, Set<ConstructorKey>>> keys;
        private final Set<ConstructorKey>                              EMPTY_KEY_SET = new HashSet<ConstructorKey>(0);

        /**
         * Create a new lookup with an expected number of entries.
         *
         * @param n size
         */
        public ConstructorLookup(int n) {
            keys = new HashMap<Class<?>, Map<Integer, Set<ConstructorKey>>>(n);
        }

        /**
         * Add a key for a given interface.
         *
         * @param intf an interface
         * @param key  the key to add for the interface
         */
        public void put(Class<?> intf, ConstructorKey key) {

            if (!keys.containsKey(intf)) {
                keys.put(intf, new HashMap<Integer, Set<ConstructorKey>>());
            }

            Map<Integer, Set<ConstructorKey>> map = keys.get(intf);

            int n = key.n();

            if (!map.containsKey(n)) {
                map.put(n, new HashSet<ConstructorKey>(5)); // few constructors per class
            }

            map.get(n).add(key);

        }

        /**
         * Access all constructors for a given interface.
         *
         * @param intf the interface to lookup the constructors of.
         * @return set of constructors
         */
        private Set<ConstructorKey> getConstructors(Class<?> intf) {

            Map<Integer, Set<ConstructorKey>> candidates = keys.get(intf);
            Set<ConstructorKey> keys = new TreeSet<ConstructorKey>();

            if (candidates != null) {
                for (Map.Entry<Integer, Set<ConstructorKey>> e : candidates.entrySet()) {
                    keys.addAll(e.getValue());
                }
            }

            return keys;

        }

        /**
         * Access a set of candidates for a given key. Candidates match the
         * interface an number of parameters for the constructors. A key may
         * match when it's parameters are subclasses.
         *
         * @param key the key to find possible candidates for
         * @return set of constructors which 'could' match the given key
         */
        public Set<ConstructorKey> getCandidates(ConstructorKey key) {
            return getCandidates(key.intf(), key.n());
        }

        /**
         * Find all constructor keys which match the given interface and
         * parameters number.
         *
         * @param intf the interface to lookup
         * @param n    number of parameters
         * @return set of constructors which 'could' match the given key
         */
        public Set<ConstructorKey> getCandidates(Class<?> intf, int n) {

            LOGGER.debug("getting candidates for ", intf, " ", n);

            Map<Integer, Set<ConstructorKey>> map = keys.get(intf);
            if (map == null) {
                LOGGER.debug("no keys for ", intf);
                LOGGER.debug(keys);
                return EMPTY_KEY_SET;
            }

            Set<ConstructorKey> candidates = map.get(n);
            if (candidates == null) {
                LOGGER.debug("no keys for parameter count", n);
                LOGGER.debug(map);
                return EMPTY_KEY_SET;
            }

            return candidates;

        }

    }

    /**
     * A simple class based key which allows a key to use an object array for
     * it's parameter types.
     */
    private final static class ObjectBasedKey extends ConstructorKey {

        private final Class<?> intf;
        private final Object[] params;
        private final int      n;

        /**
         * Create the key with an interface and array of parameters.
         *
         * @param intf   interface class
         * @param params the object parameters
         */
        private ObjectBasedKey(Class<?> intf, Object[] params) {
            this.intf = intf;
            this.params = params;
            this.n = params.length;
        }

        /**
         *{@inheritDoc}
         */
        @Override
        public Class<?> intf() {
            return intf;
        }

        /**
         *{@inheritDoc}
         */
        @Override
        public Class<?> type(int i) {
            if (params[i] == null) throw new IllegalArgumentException("null param type");
            return params[i].getClass();
        }

        /**
         *{@inheritDoc}
         */
        @Override
        public int n() {
            return n;
        }

    }

    /**
     * A simple class based key which allows a key to use a class array for it's
     * parameter types.
     */
    private final static class ClassBasedKey extends ConstructorKey {

        private final Class<?>   intf;
        private final Class<?>[] params;
        private final int        n;

        /**
         * Create the key with an interface and array of parameters.
         *
         * @param intf   interface class
         * @param params the parameter types
         */
        private ClassBasedKey(Class<?> intf, Class<?>[] params) {
            this.intf = intf;
            this.params = params;
            this.n = params.length;
        }

        /**
         *{@inheritDoc}
         */
        @Override
        public Class<?> intf() {
            return intf;
        }

        /**
         *{@inheritDoc}
         */
        @Override
        public Class<?> type(int i) {
            return params[i];
        }

        /**
         *{@inheritDoc}
         */
        @Override
        public int n() {
            return n;
        }

    }

    /**
     * A class which encapsulates the information about an interface (of this
     * implementation) and the parameter types of the constructor.
     */
    public static abstract class ConstructorKey implements Comparable<ConstructorKey> {

        /**
         * Access the interface this key indexes.
         *
         * @return class of the interface
         */
        public abstract Class<?> intf();

        /**
         * Access the type of class at the given parameter index.
         *
         * @param i index of the parameter
         * @return class of the parameter at index
         */
        public abstract Class<?> type(int i);

        /**
         * Access the number of parameters in the constructor.
         *
         * @return number of parameters
         */
        public abstract int n();

        /**
         *{@inheritDoc}
         */
        @Override
        public boolean equals(Object o) {

            if (o == null || !(o instanceof ConstructorKey)) return false;

            ConstructorKey that = (ConstructorKey) o;

            if (intf() != that.intf() || n() != that.n()) {
                return false;
            }
            for (int i = 0; i < n(); i++) {
                if (!type(i).equals(that.type(i))) return false;
            }
            return true;

        }

        /**
         * Indicates whether this key has multiple parameters and they are of
         * uniform type. If there are less then two types this method will
         * return false.  {@code new Object[]{ Atom, Bond, Atom } } // false
         * {@code new Object[]{ Atom, Atom, Atom } } // true
         *
         * @return whether the key is uniform
         */
        public boolean isUniform() {

            if (n() < 2) return false;

            Class<?> base = type(0);
            for (int i = 1; i < n(); i++) {
                if (type(i) != base) {
                    return false;
                }
            }

            return true;

        }

        /**
         *{@inheritDoc}
         */
        @Override
        public int hashCode() {
            int result = intf().hashCode();

            for (int i = 0; i < n(); i++)
                result = 31 * result + type(i).hashCode();

            return result;
        }

        /**
         * Orders constructor keys by the number of parameters and then this
         * name.
         *
         * @param o another constructor key
         * @return whether the other key is greater than, less than or equal to
         *         this key
         */
        @Override
        public int compareTo(ConstructorKey o) {

            // order by number of params first
            if (n() != o.n()) {
                return n() > o.n() ? 1 : n() < o.n() ? -1 : 0;
            }

            // use the lexicographic order of the toString method
            return toString().compareTo(o.toString());

        }

        /**
         * Checks whether this key is assignable to the candidate.
         *
         * @param candidate another constructor key
         * @return whether the provided candidate is assignable
         */
        public boolean isAssignable(ConstructorKey candidate) {

            for (int i = 0; i < candidate.n(); i++) {
                // check whether the parameters are assignable
                if (!candidate.type(i).isAssignableFrom(type(i))) {
                    return false;
                }
            }

            // no conflicts this constructor is okay
            return true;

        }

        /**
         *{@inheritDoc}
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(n() * 50);
            sb.append(intf().getSimpleName());
            sb.append('(');
            int max = n() - 1;
            for (int i = 0; i <= max; i++) {
                sb.append(type(i).getSimpleName());
                if (i != max) sb.append(", ");
            }
            sb.append(')');
            return sb.toString();
        }

    }

    /**
     * A default interface provider implementation that simply returns the
     * classes from {@link Class#getInterfaces()}.
     */
    protected static class DefaultInterfaceProvider implements InterfaceProvider {

        /**
         *{@inheritDoc}
         */
        @Override
        public Class<?>[] getInterfaces(Class<?> c) {
            return c.getInterfaces();
        }
    }

    /**
     * An interface that can provide which interfaces the given class
     * implements. This interface is constructor injectable allowing us to test
     * the registration using this method.
     */
    public static interface InterfaceProvider {

        /**
         * Access the interfaces for a given class.
         *
         * @param c the class to access the interfaces of
         * @return a set of interfaces
         */
        public Class<?>[] getInterfaces(Class<?> c);
    }

    /**
     * An interface that allows posterior modification of an instance after it
     * has been created.
     *
     * @param <T> instance instance to be modified
     */
    public static interface CreationModifier<T> {

        public void modify(T instance);
    }

    /**
     * A Creator wrapper which can include a modifier.
     *
     * @param <T> the type of object that will be created
     */
    private static class ModifiedCreator<T> implements Creator<T> {

        private final CreationModifier<T> modifier;
        private final Creator<T>          parent;

        /**
         * Create a new modified created which delegate instance creation to the
         * given parent and applies the modifier after the instance has been
         * constructed.
         *
         * @param parent   parent creator
         * @param modifier a modify to apply after creation
         */
        private ModifiedCreator(Creator<T> parent, CreationModifier<T> modifier) {
            this.parent = parent;
            this.modifier = modifier;
        }

        /**
         *{@inheritDoc}
         */
        @Override
        public T create(Object[] objects) throws InvocationTargetException, IllegalAccessException,
                InstantiationException {
            T instance = parent.create(objects);
            modifier.modify(instance);
            return instance;
        }

        /**
         *{@inheritDoc}
         */
        @Override
        public Class<T> getDeclaringClass() {
            return parent.getDeclaringClass();
        }
    }

    /**
     * An interface that wraps object creation via the {@link #create(Object[])}
     * method.
     *
     * @param <T> the type of object that will be created
     */
    public static interface Creator<T> {

        /**
         * Create a new instance with the provided object parameters.
         *
         * @param objects parameters for the constructor excluding
         * @return a new instance, created with the provided parameters
         * @throws InvocationTargetException thrown if an error occurred during
         *                                   construction
         * @throws IllegalAccessException    if the constructor can't be
         *                                   accessed (e.g. private)
         * @throws InstantiationException    thrown if class is abstract
         */
        public T create(Object[] objects) throws InvocationTargetException, IllegalAccessException,
                InstantiationException;

        /**
         * Access the implementation of this class.
         *
         * @return the implementation class to be created
         */
        public Class<T> getDeclaringClass();
    }

    /**
     * A simple creator that helps in creating an anonymous classes for a
     * creator.
     *
     * @param <T> the type of object that will be created
     */
    public static abstract class BasicCreator<T> implements Creator<T> {

        private final Class<T> c;

        /**
         * Create a basic constructor with a given declaring class.
         *
         * @param c class to be created
         */
        public BasicCreator(Class<T> c) {
            this.c = c;
        }

        /**
         *{@inheritDoc}
         */
        @Override
        public Class<T> getDeclaringClass() {
            return c;
        }
    }

    /**
     * A simple creator that wraps the object parameters before invoking {@link
     * #create(Object[])} on the provided parent.
     *
     * @param <T> the type of object that will be created
     */
    private static class ArrayWrapCreator<T> implements Creator<T> {

        private final Creator<T> parent;

        /**
         * Create a new creator with a given parent. The parent is used to
         * actually create the object, this creator wraps the provided params in
         * another and is required if 'varargs' are 'unpacked'.
         *
         * @param parent the parent creator
         */
        public ArrayWrapCreator(Creator<T> parent) {
            this.parent = parent;
        }

        /**
         *{@inheritDoc}
         */
        @Override
        public T create(Object[] objects) throws InvocationTargetException, IllegalAccessException,
                InstantiationException {
            return parent.create(new Object[]{objects});
        }

        /**
         *{@inheritDoc}
         */
        @Override
        public Class<T> getDeclaringClass() {
            return parent.getDeclaringClass();
        }

    }

    /**
     * A simple instantiation that uses reflection to create an object.
     *
     * @param <T> type of object that will be created
     */
    private static class ReflectionCreator<T> implements Creator<T> {

        private final Constructor<T> constructor;

        /**
         * Create a reflection creator for a given constructor.
         *
         * @param constructor constructor for a class
         */
        private ReflectionCreator(Constructor<T> constructor) {
            this.constructor = constructor;
        }

        /**
         *{@inheritDoc}
         */
        @Override
        public T create(Object[] objects) throws InvocationTargetException, IllegalAccessException,
                InstantiationException {
            return constructor.newInstance(objects);
        }

        /**
         *{@inheritDoc}
         */
        @Override
        public Class<T> getDeclaringClass() {
            return constructor.getDeclaringClass();
        }
    }

}
