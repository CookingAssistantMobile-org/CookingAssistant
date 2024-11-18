package com.cookingassistant.ui.screens.recipescreen

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material.icons.outlined.AddComment
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.cookingassistant.data.objects.TextFormatting
import com.cookingassistant.ui.screens.recipescreen.composables.RecipeDetailsPage
import com.cookingassistant.ui.screens.recipescreen.composables.RecipeEndPage
import com.cookingassistant.ui.screens.recipescreen.composables.RecipeRatingPage
import com.cookingassistant.ui.screens.recipescreen.composables.RecipeScreenFrontPage
import com.cookingassistant.ui.screens.recipescreen.composables.RecipeStepPage
import java.io.File

@Composable
fun RecipeScreen(
    recipeScreenViewModel : RecipeScreenViewModel,
    destinationDir: File
) {
    val recipe by recipeScreenViewModel.recipe.collectAsState()
    val img by recipeScreenViewModel.recipeImg.collectAsState()
    val favorite by recipeScreenViewModel.markedFavorite.collectAsState()
    val userReview by recipeScreenViewModel.reviewGetDto.collectAsState()
    val stepsCount by recipeScreenViewModel.stepsCount.collectAsState()

    // each step on separate page + 1 frontpage + 1 details
    val pagesCount by remember { mutableStateOf(2) }
    var currentPage by remember { mutableStateOf(0) }
    var offsetX by remember { mutableStateOf(0f) }
    var savedPage by remember { mutableStateOf(0) }
    Log.i("current_page",currentPage.toString())
    Log.d("current_page",pagesCount.toString())
    Log.v("current_page",recipe.steps?.size.toString())

    val sizeAnim1 by animateFloatAsState(
        targetValue = if (currentPage % 2 == 0) 0.94f else 0f
    )
    val sizeAnim2 by animateFloatAsState(
        targetValue = if (currentPage % 2 == 1) 0.94f else 0f
    )

    Box(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 40.dp, horizontal = 5.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = {change, dragAmount ->
                        change.consume()
                        offsetX = dragAmount
                    },
                    onDragStart = {offsetX = 0f},
                    onDragEnd = {
                        if (offsetX < -5f && currentPage < (pagesCount+stepsCount))
                            currentPage++
                        else if(offsetX > 5f && currentPage != 0) {
                            if(currentPage == (pagesCount+stepsCount) + 1 ) {
                                currentPage = savedPage
                            } else {
                                currentPage--
                            }
                        }
                    }
                )
            }
    ) {
        Column (Modifier
            .fillMaxHeight(if(currentPage % 2 == 0) sizeAnim1 else sizeAnim2)
            .align(Alignment.Center)
        ){
            Spacer(Modifier.fillMaxWidth().height(1.dp).padding(top=35.dp))
            when(currentPage) {
                0 -> {
                    RecipeScreenFrontPage(recipe.id, recipe.name,img,recipe.description,recipe.authorName,recipe.categoryName,recipe.occasionName, recipe.difficultyName ?: "not known", recipeScreenViewModel = recipeScreenViewModel, destinationDir = destinationDir)
                }
                1 -> {
                    RecipeDetailsPage(recipe.caloricity, TextFormatting.formatTime(recipe.timeInMinutes),
                        TextFormatting.formatIngredients(recipe.ingredients), recipe.serves, modifier = Modifier.padding(vertical = 8.dp, horizontal = 5.dp))
                }
                in 2..<(pagesCount+stepsCount) -> {
                    RecipeStepPage(stepNumber = currentPage-1,
                        stepText = recipe.steps?.get(currentPage-2)?.description ?: "")
                }
                (pagesCount+stepsCount) -> {
                    RecipeEndPage()
                }
                (pagesCount+stepsCount)+1 -> {
                    RecipeRatingPage(recipeScreenViewModel)
                }
            }
        }

        Row ( Modifier
            .align(Alignment.TopEnd)
        ) {
            IconButton(onClick = {recipeScreenViewModel.onFavoriteChanged(!favorite)}

            ) {
                if(favorite) {
                    Icon(imageVector = Icons.Filled.Favorite, contentDescription = "mark favorite", tint = Color.Red)
                } else {
                    Icon(imageVector = Icons.Outlined.FavoriteBorder, contentDescription = "remove form favorites")
                }
            }

            if(currentPage != (pagesCount+stepsCount) + 1) {
                IconButton(
                    onClick = {
                        recipeScreenViewModel.onUserEnterRecipeRatingPage()
                        savedPage = (pagesCount+stepsCount)
                        currentPage = (pagesCount+stepsCount) + 1
                    },
                ) {
                    Icon(
                        Icons.Outlined.AddComment,
                        contentDescription = "rate recipe"
                    )
                }
            }

        }

        Row (
            modifier = Modifier.align(Alignment.BottomCenter)
                .fillMaxHeight(0.1f)
                .fillMaxWidth()
            ,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            if(currentPage != 0) {
                IconButton(
                    onClick = {
                        if(currentPage == (pagesCount+stepsCount) + 1) {
                            currentPage = savedPage
                        } else {
                            currentPage -= 1
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardDoubleArrowLeft
                        ,null,
                        Modifier.size(50.dp),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            if(currentPage >= 0 && currentPage < (pagesCount+stepsCount)) {
                IconButton(
                    onClick = {currentPage += 1}
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardDoubleArrowRight
                        ,null,
                        Modifier.size(50.dp),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

        }
    }
}