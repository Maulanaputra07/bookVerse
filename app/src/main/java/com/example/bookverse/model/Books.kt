package com.example.bookverse.model

data class Books(
    val id :String ?= null,
    val judul :String ?= null,
    val sinopsis :String ?= null,
    val genre :String ?= null,
    val cover :String ?= null,
    val penulis :String ?= null,
    val tahun_terbit :Int ?= null
)
