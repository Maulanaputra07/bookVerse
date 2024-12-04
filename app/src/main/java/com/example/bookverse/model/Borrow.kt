import android.os.Parcel
import android.os.Parcelable
import java.util.Date

data class Borrow(
    val id: String? = null,
    val id_user: String? = null,
    val id_book: String? = null,
    val date_borrow: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()// Deserialize the Date object
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(id_user)
        parcel.writeString(id_book)
        parcel.writeSerializable(date_borrow) // Serialize the Date object
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Borrow> {
        override fun createFromParcel(parcel: Parcel): Borrow {
            return Borrow(parcel)
        }

        override fun newArray(size: Int): Array<Borrow?> {
            return arrayOfNulls(size)
        }
    }
}
