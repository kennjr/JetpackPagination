package com.ramanie.jetpackpagination

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeFragment(){

    val viewModel = viewModel<MainViewModel>()
    //we're getting what we need to access the data
    val vmState = viewModel.state

    LazyColumn(modifier = Modifier.fillMaxSize()){
        items(vmState.items.size){ index ->
            // we're gonna check whether we're at the bottom of the list and need to load more data
            /**
             * TASK : check the viability of moving this if into a sideEffect block
             */
            if (index >= vmState.items.size.minus(1) && !vmState.endReached && !vmState.isLoading){
                viewModel.loadNextSet()
            }
            // we're getting the actual item for the current index
            val item = vmState.items[index]
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
                Text(text = item.title, fontSize = 20.sp, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = item.description, fontSize = 14.sp, color = Color.LightGray)
            }
        }
        item {
            if (vmState.isLoading){
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}