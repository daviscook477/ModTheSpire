package com.evacipated.cardcrawl.modthespire;

import sun.reflect.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

public class ReflectionHelper {
    private static final String MODIFIERS_FIELD = "modifiers";

    private static final ReflectionFactory reflection =
        ReflectionFactory.getReflectionFactory();

    public static void setStaticFinalField(
        Field field, Object value)
        throws NoSuchFieldException, IllegalAccessException {
        // we mark the field to be public
        field.setAccessible(true);
        // next we change the modifier in the Field instance to
        // not be final anymore, thus tricking reflection into
        // letting us modify the static final field
        Field modifiersField =
            Field.class.getDeclaredField(MODIFIERS_FIELD);
        modifiersField.setAccessible(true);
        int modifiers = modifiersField.getInt(field);
        // blank out the final bit in the modifiers int
        modifiers &= ~Modifier.FINAL;
        modifiersField.setInt(field, modifiers);
        FieldAccessor fa = reflection.newFieldAccessor(
            field, false
        );
        fa.set(null, value);
    }
    
	public static Method[] getStaticMethods(Method[] allMethods, boolean getStatic) {
		List<Method> matchedMethods = new ArrayList<>();
		for (int i = 0; i< allMethods.length; i++) {
			Method curr = allMethods[i];
			if (Modifier.isStatic(curr.getModifiers()) == getStatic) {
				matchedMethods.add(curr);
			}
		}
		Method[] retArr = new Method[matchedMethods.size()];
		return matchedMethods.toArray(retArr);
	}
	
	public static Method[] getAnnotatedMethods(Method[] allMethods, Class<? extends Annotation> clazz) {
		List<Method> matchedMethods = new ArrayList<>();
		for (int i = 0; i< allMethods.length; i++) {
			Method curr = allMethods[i];
			if (curr.isAnnotationPresent(clazz)) {
				matchedMethods.add(curr);
			}
		}
		Method[] retArr = new Method[matchedMethods.size()];
		return matchedMethods.toArray(retArr);
	}
}
