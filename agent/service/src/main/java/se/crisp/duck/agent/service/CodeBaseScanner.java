package se.crisp.duck.agent.service;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.runtime.reflect.Factory;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.xml.sax.helpers.DefaultHandler;
import se.crisp.duck.agent.util.Configuration;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Arrays.asList;

/**
 * @author Olle Hallin
 */
@Slf4j
public class CodeBaseScanner {

    /**
     * Uses AspectJ for creating the same signature as AbstractDuckAspect.
     *
     * @return The same signature object as an AspectJ execution pointcut will provide in JoinPoint.getSignature().
     * Returns null unless the method is public.
     */
    private String makeSignature(Class clazz, Method method) {
        return clazz != null && Modifier.isPublic(method.getModifiers()) ? new Factory(null, clazz)
                .makeMethodSig(method.getModifiers(), method.getName(), clazz, method.getParameterTypes(),
                               null, method.getExceptionTypes(), method.getReturnType()).toLongString() : null;
    }

    public static class Result {
        public final Set<String> signatures = new TreeSet<>();
        public final Map<String, String> overriddenSignatures = new HashMap<>();
    }

    Result getPublicMethodSignatures(Configuration config, CodeBase codeBase) {
        long startedAt = System.currentTimeMillis();
        log.info("Scanning code base {}", codeBase);

        String packagePrefix = config.getPackagePrefix();
        URLClassLoader appClassLoader = new URLClassLoader(codeBase.getUrls(), System.class.getClassLoader());
        Reflections reflections = new Reflections(packagePrefix, appClassLoader, new SubTypesScanner(false));

        Result result = new Result();
        for (Class<?> rootClass : asList(Object.class, Enum.class, Thread.class, DefaultHandler.class, Exception.class)) {
            for (Class<?> clazz : reflections.getSubTypesOf(rootClass)) {
                findPublicMethods(result, packagePrefix, clazz);
            }
        }

        checkState(!result.signatures.isEmpty(),
                   "Code base at " + codeBase + " does not contain any classes with package prefix " + packagePrefix);

        log.debug("Code base {} with package prefix '{}' scanned in {} ms, found {} public methods.",
                  codeBase, packagePrefix, System.currentTimeMillis() - startedAt, result.signatures.size());
        return result;
    }

    void findPublicMethods(Result result, String packagePrefix, Class<?> clazz) {
        log.debug("Analyzing {}", clazz);
        Method[] methods = clazz.getMethods();

        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers())) {

                // Some AOP frameworks (e.g., Guice) push methods from a base class down to a subclass created in runtime.
                // We need to map those back to the original declaring signature, or else the original, declared method will look unused.

                String thisSignature = makeSignature(clazz, method);
                String declaringSignature = makeSignature(findDeclaringClass(method.getDeclaringClass(), method, packagePrefix), method);

                if (declaringSignature != null) {
                    if (!declaringSignature.equals(thisSignature)) {
                        log.trace("  Adding {} -> {} to overridden signatures", thisSignature, declaringSignature);
                        result.overriddenSignatures.put(thisSignature, declaringSignature);
                    }

                    if (result.signatures.add(declaringSignature)) {
                        log.trace("  Found {}", declaringSignature);
                    }
                }
            }
        }

        for (Class<?> innerClass : clazz.getDeclaredClasses()) {
            findPublicMethods(result, packagePrefix, innerClass);
        }
    }

    private Class findDeclaringClass(Class<?> clazz, Method method, String packagePrefix) {
        if (clazz == null) {
            return null;
        }
        String pkg = clazz.getPackage().getName();
        if (!pkg.startsWith(packagePrefix)) {
            return null;
        }
        try {
            clazz.getDeclaredMethod(method.getName(), method.getParameterTypes());
            return clazz;
        } catch (NoSuchMethodException ignore) {
        }
        return findDeclaringClass(clazz.getSuperclass(), method, packagePrefix);
    }

}
