package org.mos.kit.unit.args;

import java.util.Comparator;

public class ClassInheritanceComparator implements Comparator<Class<?>> {

	private static final int GREATER_ELEMENT = 1;
	private static final int SAME_ELEMENT = 0;

	@Override
	public int compare(Class<?> o1, Class<?> o2) {
		int result;
		if (o1 == null) {
			result = o2 == null ? SAME_ELEMENT : GREATER_ELEMENT;
		} else if (o1.equals(o2)) {
			result = SAME_ELEMENT;
		} else if (o1.isAssignableFrom(o2) || o2.isAssignableFrom(o1)) {
			result = compareInheritedTypes(o1, o2);
		} else {
			result = compareDifferentTypes(o1, o2);
		}
		return result;
	}

	private int compareInheritedTypes(Class<?> o1, Class<?> o2) {
		return getCountTillRoot(o1) - getCountTillRoot(o2);
	}

	private int compareDifferentTypes(Class<?> o1, Class<?> o2) {
		return getRoot(o1).hashCode() - getRoot(o2).hashCode();
	}

	private int getCountTillRoot(Class<?> clz) {
		int cnt = 0;
		for (Class<?> current = clz; !isSuperObject(current); current = current.getSuperclass()) {
			cnt++;
		}
		return cnt;
	}

	private Class<?> getRoot(Class<?> clz) {
		return isSuperObject(clz) ? clz : getRoot(clz.getSuperclass());
	}

	private boolean isSuperObject(Class<?> clz) {
		return Object.class.equals(clz.getSuperclass());
	}

}
