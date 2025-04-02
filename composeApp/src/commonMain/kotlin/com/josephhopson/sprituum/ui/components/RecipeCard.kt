package com.josephhopson.sprituum.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.josephhopson.sprituum.domain.model.Recipe

/**
 * Card component for displaying a recipe in a list
 *
 * @param recipe The recipe to display
 * @param onClick Callback for when the card is clicked
 * @param onFavoriteToggle Callback for when the favorite button is clicked
 * @param modifier Modifier for the card
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animate elevation change on favorite status
    val elevation by animateDpAsState(
        targetValue = if (recipe.favorite) 4.dp else 2.dp,
        animationSpec = tween(durationMillis = 300),
        label = "Card elevation animation"
    )
    
    // Animate favorite icon color
    val favoriteColor by animateColorAsState(
        targetValue = if (recipe.favorite) 
            MaterialTheme.colorScheme.primary 
        else 
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        animationSpec = tween(durationMillis = 300),
        label = "Favorite icon color animation"
    )

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(16.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
            .semantics {
                contentDescription = "Recipe card for ${recipe.name}"
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp // Shadow is handled by the modifier
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Card header with gradient background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        )
                    )
            )
            
            // Card content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = recipe.name,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = onFavoriteToggle,
                        modifier = Modifier
                            .size(48.dp)
                            .semantics {
                                contentDescription = if (recipe.favorite) {
                                    "Remove ${recipe.name} from favorites"
                                } else {
                                    "Add ${recipe.name} to favorites"
                                }
                            }
                    ) {
                        Icon(
                            imageVector = if (recipe.favorite) {
                                Icons.Default.Favorite
                            } else {
                                Icons.Default.FavoriteBorder
                            },
                            contentDescription = null, // Content description is on the parent IconButton
                            tint = favoriteColor
                        )
                    }
                }

                if (recipe.altName != null) {
                    Text(
                        text = recipe.altName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryPill(
                        text = if (recipe.alcoholic) "Alcoholic" else "Non-alcoholic"
                    )

                    if (recipe.glassware != null) {
                        Text(
                            text = "•",
                            modifier = Modifier.padding(horizontal = 4.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        CategoryPill(text = recipe.glassware)
                    }
                }

                if (recipe.about != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = recipe.about,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }

                if (recipe.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row {
                        recipe.tags.take(3).forEach { tag ->
                            Text(
                                text = "#$tag",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                        if (recipe.tags.size > 3) {
                            Text(
                                text = "+${recipe.tags.size - 3}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryPill(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
