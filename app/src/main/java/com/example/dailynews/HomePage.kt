package com.example.dailynews

import android.content.Intent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.dailynews.screens.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.kwabenaberko.newsapilib.models.Article

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(newsViewModel: NewsViewModel, navController: NavHostController) {
    val articles by newsViewModel.articles.observeAsState(emptyList())
    var selectedCategory by remember { mutableStateOf("Top Headlines") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Daily News",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                actions = {
                    LogoutButton()
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                CategoriesBar(newsViewModel, onCategorySelected = { category ->
                    selectedCategory = category
                })
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = selectedCategory,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(articles) { article ->
                        ArticleItem(article, navController)
                    }
                }
            }
        }
    )
}

@Composable
fun LogoutButton() {
    val context = LocalContext.current
    IconButton(onClick = {
        FirebaseAuth.getInstance().signOut()

        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }) {
        Icon(imageVector = Icons.Default.Person, contentDescription = "Logout Icon")
    }
}

@Composable
fun ArticleItem(article: Article, navController: NavHostController) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = {
            navController.navigate(NewsArticleScreen(article.url))
        },
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = article.urlToImage ?: "https://example.com/default_image.jpg",
                contentDescription = "Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = article.source.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesBar(newsViewModel: NewsViewModel, onCategorySelected: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchExpanded by remember { mutableStateOf(false) }

    val categoriesList = listOf(
        "GENERAL", "SPORTS", "HEALTH", "TECHNOLOGY", "ENTERTAINMENT", "SCIENCE", "BUSINESS"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isSearchExpanded) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search News...") },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(0.7f)
                    .clip(RoundedCornerShape(5.dp)),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = {
                        isSearchExpanded = false
                        if (searchQuery.isNotEmpty()) {
                            newsViewModel.fetchTopHeadlineWithQuery(searchQuery)
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
                    }
                }
            )
        } else {
            IconButton(onClick = { isSearchExpanded = true }) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        categoriesList.forEach { category ->
            Button(
                onClick = {
                    newsViewModel.fetchTopNewsHeadLines(category)
                    onCategorySelected(category)
                },
                modifier = Modifier.padding(4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text(text = category, fontSize = 14.sp)
            }
        }
    }
}