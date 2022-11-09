package com.ramanie.jetpackpagination

import kotlinx.coroutines.delay

class Repository {

    private val remoteDataSource = ( 1..100 ).map {
        ListItem(
            title = "Title $it",
            description = "Description $it"
        )
    }

    suspend fun getItems(page: Int, pageSize: Int): Result<List<ListItem>>{
        /**
         * the @param page is the key in this situation, it can be replaced by anything we'd like.
         * so if we're making requests to an API that needs a custom token for us to get the next set of data we can replace the page with that
         */
        // since this is a simulation, we're gonna delay the result by 2sec
        delay(2000L)
        // we get the start index by multiplying the page requested for by the user with the pagesize
        val startingIndex = page.times(pageSize)
        // we're checking whether the starting index plus the pagesize is less than the entire list's size,
        // if that's the case then we're gonna return some data
        return if (startingIndex.plus(pageSize) <= remoteDataSource.size){
            Result.success(
                // since we just need a specified portion of the data we'll slice the list to get that portion
            remoteDataSource.slice(startingIndex until (startingIndex.plus(pageSize)))
            )
        }else{
            Result.success(emptyList())
        }
    }

}