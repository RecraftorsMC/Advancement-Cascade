package com.github.nerjalnosk.advancement_cascade;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdvancementCascade {
	public static final String MOD_ID = "advancement_cascade";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static PlayerAdvancementCriterion advGetCriterion;
	private static PlayerAdvancementCriterion advLossCriterion;

	public static void init() {
		LOGGER.debug("[{}] mod loaded", MOD_ID);
	}

	public static void criterion1(PlayerAdvancementCriterion criterion) {
		if (advGetCriterion == null) advGetCriterion = criterion;
	}

	public static void criterion2(PlayerAdvancementCriterion criterion) {
		if (advLossCriterion == null) advLossCriterion = criterion;
	}

	public static PlayerAdvancementCriterion advGetCriterion() {
		return advGetCriterion;
	}

	public static PlayerAdvancementCriterion advLossCriterion() {
		return advLossCriterion;
	}

	public static Identifier id(String s) {
		return new Identifier(MOD_ID, s);
	}
}
