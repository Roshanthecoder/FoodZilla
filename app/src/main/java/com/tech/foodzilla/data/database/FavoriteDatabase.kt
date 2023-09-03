package com.tech.foodzilla.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tech.foodzilla.model.ProductModel

@Database(entities = [ProductModel::class], version = 1, exportSchema = false)
abstract class FavoriteDatabase: RoomDatabase() {

    abstract fun getFavoriteDao(): FavoriteDao
}