package com.example.messenger.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable


class User: Serializable {
    var uid:String?=null
    var username:String?=null
    var profileImageUrl:String?=null
    constructor() {}
    constructor(uid: String, username: String, profileImageUrl: String) {
        this.uid = uid
        this.username = username
        this.profileImageUrl = profileImageUrl
    }

    override fun toString(): String {
        return "uid: $uid username: $username profileImageUrl: $profileImageUrl"
    }
}