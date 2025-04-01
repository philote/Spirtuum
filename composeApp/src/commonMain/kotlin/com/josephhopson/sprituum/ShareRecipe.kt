package com.josephhopson.sprituum

import com.josephhopson.sprituum.domain.model.Recipe

/**
 * Platform-specific implementation for sharing recipe as plain text
 *
 * @param recipe The recipe to share
 */
expect fun shareRecipe(recipe: Recipe)