package com.coderadar.enums;

import java.lang.reflect.Modifier;

public enum ModifierEnum {

	Default(0),

	Public(Modifier.PUBLIC),

	Private(Modifier.PRIVATE),

	Protected(Modifier.PROTECTED),

	Interface(Modifier.INTERFACE),

	Static(Modifier.STATIC),

	Final(Modifier.FINAL),

	Synchronized(Modifier.SYNCHRONIZED),

	Volatile(Modifier.VOLATILE),

	Transient(Modifier.TRANSIENT),

	Native(Modifier.NATIVE),

	Abstract(Modifier.ABSTRACT),

	Strict(Modifier.STRICT);

	private int val;

	private ModifierEnum(int value) {
		this.val = value;
	}

	public static String getStr(int val) {
		for (ModifierEnum me : ModifierEnum.values()) {
			if (me.val == val) {
				return me.name();
			}
		}
		return "Unknown";
	}
}
