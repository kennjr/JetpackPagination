package com.ramanie.jetpackpagination

// this interface will define what an instance of our pagination can do
// the type casted to the interface is a generic type that will allow us to pass in our own data class
interface Paginator<Key, Item> {
    suspend fun loadNextSet ()
    fun reset ()
}