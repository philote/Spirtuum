package com.josephhopson.sprituum

import com.josephhopson.sprituum.domain.model.Recipe
import platform.Foundation.NSArray
import platform.Foundation.NSString
import platform.Foundation.arrayWithObject
import platform.Foundation.stringWithString
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

/**
 * iOS implementation of shareRecipe
 */
actual fun shareRecipe(recipe: Recipe) {
    val formattedRecipe = formatRecipeForSharing(recipe)

    // Get the current root view controller
    UIApplication.sharedApplication.keyWindow?.rootViewController?.let { rootViewController ->
        // Create the share sheet with text content
        val textToShare = NSString.stringWithString(formattedRecipe)
        val items = NSArray.arrayWithObject(textToShare)
        val activityViewController = UIActivityViewController(items, null)
        
        // Present the share sheet
        rootViewController.presentViewController(activityViewController, true, null)
    }
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