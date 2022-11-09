package com.ramanie.jetpackpagination

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    /**
     * NOTE THE REPO SHOULDN'T BE CREATED IN THE VM, THIS IS AN EASY SOL.
     */
    val repository = Repository()

    // we're creating a mutable state of the screen state so that it's easily updatable
    var state by mutableStateOf(ScreenState())

    private var paginator = DefaultPaginator(
        initialKey = state.key,
        // what happens when we trigger this onLoadUpdated with a new loading state
        onLoadUpdated = {
            // we're gonna update the isLoading value of the state with the new val. we're getting from the paginator
            state = state.copy(isLoading = it)
        },
        onRequest = { nextPageKey: Int ->
            repository.getItems(nextPageKey, 10)
        },
        getNextKey = {
            // we're getting a list of items, but we don't need that since we're working with incremental pages
            state.key.plus(1)
        },
        onError = {
            // this is just a simple way to handle errors, not good for production app
            state = state.copy(error = it?.localizedMessage)
        },
        onSuccess = { items, newKey ->
            state = state.copy(items = state.items + items,
                key = newKey,
                // the end will be reached if the dataSource returns an empty list
                endReached = items.isEmpty())
        }
    )

    init {
        // we'll be making a req for the first batch here, every subsequent batch will be onScrollToBottom
        loadNextSet()
    }

    // whenever we'd like to load some more items then we'll use the fun below
    fun loadNextSet(){
        viewModelScope.launch {
            paginator.loadNextSet()
        }
    }

}

data class ScreenState(
    val isLoading: Boolean = false,
    val items: List<ListItem> = emptyList(),
    val error: String? = null,
    val endReached: Boolean = false,
    // this is for the current page
    val key: Int = 0
)