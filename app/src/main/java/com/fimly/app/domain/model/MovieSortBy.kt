package com.fimly.app.domain.model

enum class MovieSortBy(val value: String) {
    POPULARITY_DESC("popularity.desc"),
    POPULARITY_ASC("popularity.asc"),
    VOTE_AVERAGE_DESC("vote_average.desc"),
    VOTE_AVERAGE_ASC("vote_average.asc"),
    PRIMARY_RELEASE_DATE_DESC("primary_release_date.desc"),
    PRIMARY_RELEASE_DATE_ASC("primary_release_date.asc"),
    REVENUE_DESC("revenue.desc"),
    REVENUE_ASC("revenue.asc"),
    TITLE_ASC("title.asc"),
    TITLE_DESC("title.desc")
}