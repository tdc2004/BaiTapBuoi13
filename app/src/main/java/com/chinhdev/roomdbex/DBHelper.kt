package com.chinhdev.roomdbex

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update

@Database(entities = arrayOf(StudentModel::class), version = 1)
abstract class StudentDB:RoomDatabase(){
    abstract fun studentDAO():StudenDAO
}

@Dao
interface StudenDAO{
    @Query("SELECT * FROM student")
    fun getAll(): List<StudentModel>

    @Query("SELECT * FROM student WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<StudentModel>

    @Insert
    fun insert(vararg users: StudentModel)

    @Delete
    fun delete(user: StudentModel)
    @Update
    fun updateStudent(student: StudentModel)
}