package org.mos.kit.unit.args;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.mos.kit.unit.args.FieldConfig.FieldConfigBuilder;

public class TestCaseToString<TYPE> {

	private static final String ARGS_DELIMITER = ", ";
	private static final int FIRST_PARAM_INDEX = 0;
	private static final BiFunction<Object, Object, String> DEFAULT_FORMATTER = (instance, value) -> String.valueOf(value);
	private static final int SINGLE_ARGUMENT = 1;
	private static final String TO_STRING = "toString";
	private final List<FieldConfig> fields;
	private final SortedMap<Class<?>, BiFunction<Object, Object, String>> formatters;

	public TestCaseToString(Class<? extends TYPE> clazz) {
		Objects.requireNonNull(clazz);
		fields = new LinkedList<>();
		formatters = new TreeMap<>(new ClassInheritanceComparator());
		initToStringMethods(clazz);
		initFields(clazz);
	}

	private void initToStringMethods(Class<?> clazz) {
		init(clazz, Class::getDeclaredMethods, this::initMethod);
	}

	private void initFields(Class<?> clazz) {
		init(clazz, Class::getDeclaredFields, this::initField);
	}

	private <T> void init(Class<?> clazz, Function<Class<?>, T[]> values, Consumer<T> initElement) {
		if (!Object.class.equals(clazz.getSuperclass())) {
			initFields(clazz.getSuperclass());
		}
		Arrays.stream(values.apply(clazz)).forEach(initElement);
	}

	private void initMethod(Method method) {
		if (isToStringMethod(method)) {
			method.setAccessible(true);
			formatters.put(method.getParameterTypes()[FIRST_PARAM_INDEX], createFormatter(method));
		}
	}

	private BiFunction<Object, Object, String> createFormatter(Method method) {
		return (instance, value) -> invoke(method, instance, value);
	}

	private String invoke(Method method, Object instance, Object value) {
		try {
			return (String) method.invoke(instance, value);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private boolean isToStringMethod(Method method) {
		//@formatter:off
		return TO_STRING.equals(method.getName()) 
				&& method.getParameterCount() == SINGLE_ARGUMENT 
				&& String.class.equals(method.getReturnType());
		//@formatter:on
	}

	private void initField(Field field) {
		if (isFieldIncluded(field)) {
			field.setAccessible(true);
			fields.add(buildConfig(field));
		}
	}

	private FieldConfig buildConfig(Field field) {
		FieldConfigBuilder builder = FieldConfig.builder();
		builder.field(field);
		builder.formatter(getFormatter(field));
		builder.name(getName(field));
		return builder.build();
	}

	private String getName(Field field) {
		return Optional.ofNullable(field.getDeclaredAnnotation(TestCaseParam.class)).map(TestCaseParam::value).orElse(field.getName());
	}

	private BiFunction<Object, Object, String> getFormatter(Field field) {
		return getFormatter(field.getType());
	}

	private BiFunction<Object, Object, String> getFormatter(Class<?> fieldType) {
		BiFunction<Object, Object, String> formatter = formatters.get(fieldType);
		if (formatter == null) {
			formatter = getFormatterByInheritance(fieldType);
		}
		return Optional.ofNullable(formatter).orElse(DEFAULT_FORMATTER);
	}

	private BiFunction<Object, Object, String> getFormatterByInheritance(Class<?> fieldType) {
		//@formatter:off
		return formatters.entrySet().stream()
				.filter(entry -> fieldType.isAssignableFrom(entry.getKey()))
				.map(Entry::getValue)
				.findFirst()
				.orElse(null);
		//@formatter:on
	}

	private boolean isFieldIncluded(Field field) {
		//@formatter:off
		return !Modifier.isStatic(field.getModifiers()) 
				&& !Optional.ofNullable(field.getDeclaredAnnotation(TestCaseParamIgnore.class)).isPresent();
		//@formatter:on
	}

	public String toString(TYPE instance) {
		return fields.stream().map(it -> format(it, instance)).collect(Collectors.joining(ARGS_DELIMITER));
	}

	public String toString(TYPE instance, Object value) {
		BiFunction<Object, Object, String> formatter = Optional.ofNullable(value).map(Object::getClass).map(this::getFormatter).orElse(DEFAULT_FORMATTER);
		return formatter.apply(instance, value);
	}

	private String format(FieldConfig field, TYPE instance) {
		return field.getName() + "=" + printField(field, instance);
	}

	private String printField(FieldConfig field, TYPE instance) {
		return field.getFormatter().apply(instance, getValue(field, instance));
	}

	private Object getValue(FieldConfig field, TYPE instance) {
		try {
			return field.getField().get(instance);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
