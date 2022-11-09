package com.ramanie.jetpackpagination

// this is an impl of the paginator interface, and will describe how we'd like to paginate data
// the class has a key cast to it, the key is for the value that the paginator needs for it to get us the next set of data, in this case the page
// our key's gon be an Int since we paginate based on the page whose type is Int, nevertheless we can use any other type there it's all based on the dataSource
class DefaultPaginator<Key, Item>(
    // the initialKey is the value for the key that we'd like to start with
    private val initialKey: Key,
    // the onLoadUpdated will return a bool val. of of whether the loading is ongoing or done, we'll use that val to show the progressbar
    // it's a lambda callback that'll be called when the loading value is updated i.e
    // when we're either loading some more data or we're done loading the new set of data
    private inline val onLoadUpdated :(Boolean) -> Unit,
    // below is the fun. that we use to define how we get the next batch of data
    // we're gonna pass the next page's key and should get a Result returned
    // we make the call a suspend call so that we can use it on actual APIs
    private inline val onRequest: suspend (nextPageKey: Key) -> Result<List<Item>>,
    // the line below is where we'll write the logic of how we're gonna get the next key, for a pages dataSource it's very easy(currentPage + 1),
    // but as mentioned before we might be working with a key that's a custom str token and if that's
    // the case then the logic for getting that token will be written in the callback below
    // we're passing the list of items since it might contain some info need to get the next key
    private inline val getNextKey : suspend (List<Item>) -> Key,
    // NOTE : we use the keyword inline bc that'll optimise the code since the compiler will copy the
    // fun that we create, for the callback, into this class param instead of having the class  on it's own, so we won't have duplicate classes
    private inline val onError: suspend (Throwable?) -> Unit,
    // this is the code that'll be executed when a request is successful, we'll get a list of items and the nextKey from it
    private inline val onSuccess: suspend (items: List<Item>, newKey: Key) -> Unit

): Paginator<Key, Item>{

    private var currentKey: Key = initialKey
    // the var. below will prevent us from making two quick calls, one after the other, this will prevent a bug since we'll load 2 pages at once
    private var isMakingRequest: Boolean = false

    override suspend fun loadNextSet() {
        // check whether we're making a req, if so then we'd like to cancel this one
        if (isMakingRequest){
            return
        }
        // if we aren't making a req then we'd like to make one and will update the bool var.
        isMakingRequest = true
        // then we're gonna update the loading status
        onLoadUpdated(true)
        // then we'll make the actual request
        val result = onRequest(currentKey)
        // once the req us made we'll update the val. of the isMakingRequest
        isMakingRequest = false
        // then we'll get the items from the result
        // the result.getOrElse{} will get us the items and if that failed the else will run and give us a throwable
        val items = result.getOrElse {
            onError(it)
            // we update the loading status back to false
            onLoadUpdated(false)
            // then we abort the call
            return
        }
        // if we did get the items then we're gonna get the next key
        currentKey = getNextKey(items)
        // then we'll call the onSuccess and pass the latest batch of items and the new key
        onSuccess(items, currentKey)
        onLoadUpdated(false)

    }

    override fun reset() {
        currentKey = initialKey
    }

}