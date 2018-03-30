package com.evacipated.cardcrawl.modthespire.filters;

import java.util.HashSet;
import java.util.Set;

import org.clapper.util.classutil.ClassFilter;
import org.clapper.util.classutil.ClassFinder;
import org.clapper.util.classutil.ClassInfo;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public class SuperClassNameFilter implements ClassFilter
{
    private final String baseClassName;

    public SuperClassNameFilter(Class<?> baseClass)
    {
        this(baseClass.getName());
    }

    public SuperClassNameFilter(String baseClassName)
    {
        this.baseClassName = baseClassName;
    }
    
    private Set<String> findAllSuperClasses(ClassInfo classInfo) {
    	Set<String> superClasses = new HashSet<>();
    	
    	ClassPool pool = ClassPool.getDefault();
    	CtClass ctClazz = null;
    	try {
	    	ctClazz = pool.get(classInfo.getClassName());
	    	
	    	while (!ctClazz.getName().equals(Object.class.getName())) {
	    		ctClazz = ctClazz.getSuperclass();
	    		superClasses.add(ctClazz.getName());
	    	}
    	} catch (NotFoundException e) {
    		throw new FilterException("could not find superclasses for: " + classInfo.getClassName() + ", error was: " + e.toString());
    	}
    	
    	return superClasses;
    }

    @Override
    public boolean accept(ClassInfo classInfo, ClassFinder classFinder)
    {
    	Set<String> superClasses = findAllSuperClasses(classInfo);
        return baseClassName != null && superClasses.contains(baseClassName);
    }
}