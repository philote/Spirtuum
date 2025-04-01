package com.josephhopson.sprituum

import com.josephhopson.sprituum.domain.model.Recipe
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

/**
 * JVM implementation of shareRecipe
 * For desktop, we copy the recipe text to clipboard since there's no standard sharing mechanism
 */
@JvmOverloads
actual fun shareRecipe(recipe: Recipe) {
    // Format the recipe content as text
    val formattedRecipe = formatRecipeForSharing(recipe)

    // Copy to clipboard
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    val selection = StringSelection(formattedRecipe)
    clipboard.setContents(selection, selection)

    // Show feedback (in a real app, you would want to display a toast or notification)
    println("Recipe copied to clipboard")
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