package org.betterx.wover.block.api.model;

import java.lang.reflect.Method;

public final class DatagenModelDispatch {
    private DatagenModelDispatch() {}

    public static Object propertyDispatchInitial(Object... properties) {
        try {
            Class<?> dispatchClass = Class.forName("net.minecraft.client.data.models.blockstates.PropertyDispatch");
            return findMethod(dispatchClass, "initial", properties).invoke(null, properties);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Failed to create PropertyDispatch via reflection", ex);
        }
    }

    public static void propertyDispatchSelect(Object dispatch, Object... args) {
        try {
            findMethod(dispatch.getClass(), "select", args).invoke(dispatch, args);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Failed to call PropertyDispatch#select via reflection", ex);
        }
    }

    public static Object dispatchWith(Object block, Object dispatch) {
        return withDispatch(multiVariantDispatch(block), dispatch);
    }

    public static Object multiVariantDispatch(Object block) {
        try {
            Class<?> multiVariantClass = Class.forName("net.minecraft.client.data.models.blockstates.MultiVariantGenerator");
            return findMethod(multiVariantClass, "dispatch", block).invoke(null, block);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Failed to call MultiVariantGenerator#dispatch via reflection", ex);
        }
    }

    public static Object multiVariantDispatch(Object block, Object variant) {
        try {
            Class<?> multiVariantClass = Class.forName("net.minecraft.client.data.models.blockstates.MultiVariantGenerator");
            return findMethod(multiVariantClass, "dispatch", block, variant).invoke(null, block, variant);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Failed to call MultiVariantGenerator#dispatch with variant via reflection", ex);
        }
    }

    public static Object withDispatch(Object multiVariantGenerator, Object dispatch) {
        try {
            return findMethod(multiVariantGenerator.getClass(), "with", dispatch).invoke(multiVariantGenerator, dispatch);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Failed to call MultiVariantGenerator#with via reflection", ex);
        }
    }

    private static Method findMethod(Class<?> type, String name, Object... args) {
        Method fallback = null;
        for (Method method : type.getMethods()) {
            if (!method.getName().equals(name)) {
                continue;
            }

            if (method.getParameterCount() != args.length) {
                continue;
            }

            Class<?>[] parameterTypes = method.getParameterTypes();
            boolean compatible = true;
            for (int i = 0; i < parameterTypes.length; i++) {
                Object arg = args[i];
                if (arg == null) {
                    continue;
                }

                Class<?> parameterType = wrap(parameterTypes[i]);
                if (!parameterType.isAssignableFrom(arg.getClass())) {
                    compatible = false;
                    break;
                }
            }

            if (compatible) {
                return method;
            }

            if (fallback == null) fallback = method;
        }

        if (fallback != null) return fallback;

        throw new IllegalStateException(
                "Method not found: " + type.getName() + "#" + name + " with " + args.length + " parameters"
        );
    }

    private static Class<?> wrap(Class<?> type) {
        if (!type.isPrimitive()) return type;
        if (type == boolean.class) return Boolean.class;
        if (type == byte.class) return Byte.class;
        if (type == short.class) return Short.class;
        if (type == int.class) return Integer.class;
        if (type == long.class) return Long.class;
        if (type == float.class) return Float.class;
        if (type == double.class) return Double.class;
        if (type == char.class) return Character.class;
        return type;
    }
}
