package com.example.bookverse.model

import android.os.Parcel
import android.os.Parcelable

data class UserData(
    val id: String? = null,
    val username: String? = null,
    val name: String? = null,
    val email: String? = null,
    val password: String? = null,
    val role: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this (
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(username)
        dest.writeString(name)
        dest.writeString(email)
        dest.writeString(password)
        dest.writeString(role)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserData> {
        override fun createFromParcel(parcel: Parcel): UserData {
            return UserData(parcel)
        }

        override fun newArray(size: Int): Array<UserData?> {
            return arrayOfNulls(size)
        }
    }
}
