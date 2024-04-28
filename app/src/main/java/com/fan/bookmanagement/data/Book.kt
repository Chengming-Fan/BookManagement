package com.fan.bookmanagement.data

import java.io.Serializable

data class Book(
    var id: Int,
    var title: String,
    var author: String,
    var year: Int,
    var isbn: String
): Serializable {
    constructor(title: String, author: String, year: Int, isbn: String) : this(
        0,
        title,
        author,
        year,
        isbn
    )

}