package com.kissthinker.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author David Ainslie
 */
public abstract class FieldUtil
{
    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(FieldUtil.class);

    /**
     *
     * @param <O>
     * @param object
     * @param annotationClass
     * @return
     */
    public static <O> Field field(O object, Class<? extends Annotation> annotationClass)
    {
        Field[] fields = fields(object, annotationClass);

        if (fields == null)
        {
            return null;
        }
        else if (fields.length == 1)
        {
            return fields[0];
        }
        else
        {
            throw new FieldException(String.format("Found %s occurences of required annotation %s in object %s, when there should be only one!",
                                                   fields.length, annotationClass, object));
        }
    }

    /**
     *
     * @param <O>
     * @param object
     * @param fieldName
     * @param annotationClass
     * @return
     */
    public static <O> Field field(O object, String fieldName, Class<? extends Annotation> annotationClass)
    {
        Field[] fields = fields(object, annotationClass);

        if (fields != null)
        {
            for (Field field : fields)
            {
                if (field.getName().equals(fieldName))
                {
                    return field;
                }
            }
        }

        return null;
    }

    /**
     *
     * @param object can also be a Class<?>
     * @param annotationClass
     * @return
     */
    public static <O> Field[] fields(O object, Class<? extends Annotation> annotationClass)
    {
        assert(object != null);

        Class<?> class_ = null;

        if (object instanceof Class<?>)
        {
            class_ = (Class<?>)object;
        }
        else
        {
            class_ = object.getClass();
        }

        class_ = declaredClass(class_);

        List<Field> fields = new ArrayList<Field>();

        for (Field field : class_.getDeclaredFields())
        {
            if (field.isAnnotationPresent(annotationClass))
            {
                fields.add(field);
            }
        }

        return fields.toArray(new Field[0]);
    }

    /**
     *
     * @param object
     * @param annotationClass
     * @return
     */
    public static Object[] values(Object object, Class<? extends Annotation> annotationClass)
    {
        try
        {
            Field[] fields = fields(object, annotationClass);

            List<Object> objects = new ArrayList<Object>();

            for (Field field : fields)
            {
                field.setAccessible(true);
                objects.add(field.get(object));
            }

            return objects.toArray(new Object[0]);
        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage());
            throw new FieldException(e);
        }
    }

    /**
     * Get declared class that is not an anonymous class;
     * @param class_
     * @return
     */
    public static Class<?> declaredClass(Class<?> class_)
    {
        if (class_.getName().indexOf("$") == -1)
        {
            return class_;
        }
        else
        {
            LOGGER.info("Anonymous class so aquiring 'superclass'");
            return declaredClass(class_.getSuperclass());
        }
    }

    /**
     *
     * @param object
     * @param name
     * @param value
     */
    public static void set(Object object, String name, Object value)
    {
        Field field = null;

        try
        {
            field = object.getClass().getField(name);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
        }

        if (field == null)
        {
            try
            {
                field = object.getClass().getDeclaredField(name);
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
            }
        }

        if (field != null)
        {
            field.setAccessible(true);

            try
            {
                LOGGER.trace(field.get(object).toString());
                field.set(object, value);
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
            }
        }
    }

    /**
     * Utility.
     */
    private FieldUtil()
    {
        super();
    }
}