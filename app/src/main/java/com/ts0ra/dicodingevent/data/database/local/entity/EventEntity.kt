package com.ts0ra.dicodingevent.data.database.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo("id")
    val id: Int,

    @ColumnInfo("summary")
    val summary: String,

    @ColumnInfo("mediaCover")
    val mediaCover: String,

    @ColumnInfo("registrants")
    val registrants: Int,

    @ColumnInfo("imageLogo")
    val imageLogo: String,

    @ColumnInfo("link")
    val link: String,

    @ColumnInfo("description")
    val description: String,

    @ColumnInfo("ownerName")
    val ownerName: String,

    @ColumnInfo("cityName")
    val cityName: String,

    @ColumnInfo("quota")
    val quota: Int,

    @ColumnInfo("name")
    val name: String,

    @ColumnInfo("beginTime")
    val beginTime: String,

    @ColumnInfo("endTime")
    val endTime: String,

    @ColumnInfo("category")
    val category: String,

    @ColumnInfo("upcoming")
    val isUpcoming: Boolean,

    @ColumnInfo("finished")
    val isFinished: Boolean,

    @ColumnInfo("favorite")
    var isFavorite: Boolean
)
