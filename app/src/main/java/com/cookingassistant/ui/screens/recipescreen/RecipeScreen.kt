package com.cookingassistant.ui.screens.recipescreen

import android.widget.Space
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.snapping.SnapPosition
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.cookingassistant.data.RecipeItem
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

//---------------------------------//
//TESTING AREA, TO BE REMOVED LATER//
//---------------------------------//

fun simulateDatabaseQuery() : RecipeItem {
    val result : RecipeItem =
        runBlocking {
            simulateDatabaseConnection().await()
        }
    return result
}

suspend fun simulateDatabaseConnection(): Deferred<RecipeItem> = coroutineScope {
    async {
        delay(1000)
        val recipe : RecipeItem = RecipeItem(
        Id = 1u,
        Name = "Easy bread rolls",
        ImageUrl = "https://realfood.tesco.com/media/images/RFO-1400x919-BreadRolls-f2375e23-c14a-417e-ba88-7cbf26e5ba0f-0-1400x919.jpg",
        Description = "Bake these simple white bread rolls for sandwiches, burger buns or to dunk in soup. Using only a handful of ingredients, bread-making has never been so easy",
        Author = "Barney Desmazery",
        Category = "Bread",
        Type = "Bread roll recipes",

        Difficulty = "Easy",
        Ingredients = listOf("500g strong white bread flour, plus extra for dusting",
            "7g sachet fast action yeast",
            "1 tsp white caster sugar",
            "2 tsp fine salt",
            "1 tsp sunflower oil, plus extra for the work surface and bowl"),
        Rating = 4,
        Calories = 350,
        PreparationTime = "30 mins",
        CookingTime = "25 mins - 30 mins",
        Url = "https://www.bbcgoodfood.com/recipes/easy-bread-rolls",

        Steps = listOf("Tip the flour, yeast, sugar, salt and oil into a bowl. Pour over 325ml warm water, then mix (with a spatula or your hand), until it comes together as a shaggy dough. Make sure all the flour has been incorporated. Cover and leave for 10 mins.",
            "Lightly oil your work surface and tip the dough onto it. Knead the dough for at least 10 mins until it becomes tighter and springy – if you have a stand mixer you can do this with a dough hook for 5 mins. Pull the dough into a ball and put in a clean, oiled bowl. Leave for 1 hr, or until doubled in size.",
            "Tip the dough onto a lightly floured surface and roll into a long sausage shape. Halve the dough, then divide each half into four pieces, so you have eight equal-sized portions. Roll each into a tight ball and put on a dusted baking tray, leaving some room between each ball for rising. Cover with a damp tea towel and leave in a warm place to prove for 40 mins-1 hr or until almost doubled in size.",
            "Heat the oven to 230C/210C fan/gas 8. When the dough is ready, dust each ball with a bit more flour. (If you like, you can glaze the rolls with milk or beaten egg, and top with seeds.) Bake for 25-30mins, until light brown and hollow sounding when tapped on the base. Leave to cool on a wire rack.")
        )
        return@async recipe
    }
}

@Preview
@Composable
fun TestRecipeScreen() {
    val recipeViewModel : RecipeScreenViewModel = viewModel()
    recipeViewModel.loadRecipe(simulateDatabaseQuery())
    RecipeScreen(recipeViewModel)
}

//---------------------------------//
//----------END OF TESTING---------//
//---------------------------------//

@Composable
fun RecipeScreenFrontPage(
    name : String,
    imageUrl : String,
    desc : String,
    author : String,
    category : String,
    type : String,
    difficulty : String,
    size: Float = 0.9f
) {
    Box(Modifier
        .fillMaxHeight(size)
        .fillMaxWidth()
        .padding(10.dp)
        ,
    ) {
        Column (
            Modifier.align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                fontWeight = FontWeight.Bold,
                text=name,
                fontSize = 26.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.padding(vertical = 20.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.Center
            ) {
                item {
                    Text(text="Author: ${author}", color = MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.height(20.dp).fillMaxWidth())
                }
                item {
                    Text(text="Difficulty: ${difficulty}", color = MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.height(20.dp).fillMaxWidth())
                }
                item{
                    HorizontalDivider(thickness = 5.dp, modifier = Modifier.padding(vertical = 10.dp))
                }
                item{
                    HorizontalDivider(thickness = 5.dp, modifier = Modifier.padding(vertical = 10.dp))
                }
                item {
                    Text(text="Category: ${category}", color = MaterialTheme.colorScheme.onBackground)
                }
                item {
                    Text(text="Type: ${type}", color = MaterialTheme.colorScheme.onBackground)
                }
            }
        }

        AsyncImage(
            modifier = Modifier.fillMaxHeight()
                .align(Alignment.Center)
                .clip(RoundedCornerShape(50))
            ,
            model = imageUrl,
            contentScale = ContentScale.FillWidth,
            contentDescription = null
        )

        Column(
            Modifier.align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.tertiary)
                    .padding(5.dp)
            ) {
                Text(
                    fontSize = 20.sp,
                    textAlign = TextAlign.Justify,
                    text = desc,
                    color = MaterialTheme.colorScheme.onTertiary
                )
            }
        }
    }
}

@Composable
fun RecipeScreen(
    recipeScreenViewModel : RecipeScreenViewModel
) {
    val recipe by recipeScreenViewModel.recipe.collectAsState()

    // each step on separate page + 1 frontpage + 1 details + 1 for ingredients
    val pagesCount by remember { mutableStateOf(recipe.Steps.size + 3 - 1) }
    var currentPage by remember { mutableStateOf(0) }
    var offsetX by remember { mutableStateOf(0f) }
    val sizeAnim by animateFloatAsState(
        targetValue = if (currentPage == 0) 1.0f else 0f
    )

    Box(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 50.dp, horizontal = 5.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = {change, dragAmount ->
                        change.consume()
                        offsetX = dragAmount
                    },
                    onDragStart = {offsetX = 0f},
                    onDragEnd = {
                        if (offsetX < -10f && currentPage < pagesCount)
                            currentPage++
                        else if(offsetX > 10f && currentPage != 0)
                            currentPage--
                    }
                )
            }
    ) {

        if(currentPage == 0) {
            Column (Modifier
                .fillMaxHeight(sizeAnim)
                .align(Alignment.Center)
            ){
                RecipeScreenFrontPage(recipe.Name,recipe.ImageUrl,recipe.Description,recipe.Author,recipe.Category,recipe.Type,recipe.Difficulty)
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
                    onClick = {currentPage -= 1}
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardDoubleArrowLeft
                        ,null,
                        Modifier.size(50.dp),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            if(currentPage >= 0 && currentPage < pagesCount) {
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