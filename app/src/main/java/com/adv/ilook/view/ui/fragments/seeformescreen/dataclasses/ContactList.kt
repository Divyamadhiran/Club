package com.adv.ilook.view.ui.fragments.seeformescreen.dataclasses

import android.os.Parcel
import android.os.Parcelable


data class ContactList(var contactName:String,var contactNumber:String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(contactName)
        parcel.writeString(contactNumber)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ContactList> {
        override fun createFromParcel(parcel: Parcel): ContactList {
            return ContactList(parcel)
        }

        override fun newArray(size: Int): Array<ContactList?> {
            return arrayOfNulls(size)
        }
    }
}

data class MissedCallList(var missedContactName:String,var missedContactNumber:String,var missedCallDate:String,var missedCallTime:String)