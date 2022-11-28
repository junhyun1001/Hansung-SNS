package com.example.instagram.model

data class FollowDTO(
    var followerCount: Int = 0,
    var followers: MutableMap<String, Boolean> = HashMap(), // 중복 팔로우 방지
    var followingCount: Int = 0,
    var followings: MutableMap<String, Boolean> = HashMap() // 중복 팔로우 방지
)