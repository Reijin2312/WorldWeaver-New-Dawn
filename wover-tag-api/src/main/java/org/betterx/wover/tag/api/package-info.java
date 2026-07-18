/**
 * Tag Management API.
 * <p>
 * The {@link org.betterx.wover.tag.api.TagManager} is the main entry point for the API. It provides access
 * to {@link org.betterx.wover.tag.api.TagRegistry}s that manage typical tag types and allows you to create
 * custom registries.
 * <p>
 * Use {@link org.betterx.wover.datagen.api.WoverTagProvider} implementations in datagen to populate tags.
 * Register providers via your {@link org.betterx.wover.datagen.api.WoverDataGenEntryPoint}.
 */
package org.betterx.wover.tag.api;
