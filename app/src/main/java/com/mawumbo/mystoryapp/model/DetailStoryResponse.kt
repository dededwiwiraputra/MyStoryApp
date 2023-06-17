package com.mawumbo.mystoryapp.model

data class DetailStoryResponse(
    val error: Boolean,
    val message: String,
    val story: Story
)