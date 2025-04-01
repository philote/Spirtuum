package com.josephhopson.sprituum

import android.content.Intent
import com.josephhopson.sprituum.domain.model.Recipe
import kotlin.jvm.JvmOverloads

/**
 * Android implementation of shareRecipe
 */
@JvmOverloads
actual fun shareRecipe(recipe: Recipe) {
    val context = getPlatformContext()
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, recipe.name)
        putExtra(Intent.EXTRA_TEXT, formatRecipeForSharing(recipe))
    }

    val chooser = Intent.createChooser(shareIntent, "Share Recipe")
    chooser.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(chooser)
}

/**
 * Format recipe for sharing
 */
private fun formatRecipeForSharing(recipe: Recipe): String {
    var text = ""

    text += "${recipe.name}\n"
    recipe.altName?.let { text += "Also known as: $it\n" }
    text += if (recipe.alcoholic) "Alcoholic" else "Non-alcoholic"
    recipe.glassware?.let { text += " • $it" }
    text += "\n\n"

    recipe.about?.let { text += "About: $it\n\n" }

    if (recipe.ingredients.isNotEmpty()) {
        text += "Ingredients:\n"
        recipe.ingredients.forEach { ingredient ->
            text += "- ${ingredient.name}"
            ingredient.amount?.let { amount ->
                text += " (${amount.value} ${amount.label})"
            }
            ingredient.notes?.let { notes ->
                text += " - $notes"
            }
            text += "\n"
        }
        text += "\n"
    }

    if (recipe.instructions.isNotEmpty()) {
        text += "Instructions:\n"
        recipe.instructions.sortedBy { it.step }.forEach { instruction ->
            text += "${instruction.step}. ${instruction.value}\n"
        }
        text += "\n"
    }

    recipe.notes?.let { text += "Notes: $it\n" }

    text += "\nShared from Spirituum Cocktail App"

    return text
}