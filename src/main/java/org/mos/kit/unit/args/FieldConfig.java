package org.mos.kit.unit.args;

import java.lang.reflect.Field;
import java.util.function.BiFunction;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FieldConfig {
	private final Field field;
	private final String name;
	private final BiFunction<Object, Object, String> formatter;
}
