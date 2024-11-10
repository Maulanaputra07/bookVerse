import android.os.Parcel
import android.os.Parcelable

data class Books(
    val id: String? = null,
    val judul: String? = null,
    val penulis: String? = null,
    val tahun_terbit: Int? = null,
    val cover: String? = null,
    val sinopsis: String? = null,
    val genre: String? = null,
    val createDate: Long? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(judul)
        parcel.writeString(penulis)
        parcel.writeValue(tahun_terbit)
        parcel.writeString(cover)
        parcel.writeString(sinopsis)
        parcel.writeString(genre)
        parcel.writeValue(createDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Books> {
        override fun createFromParcel(parcel: Parcel): Books {
            return Books(parcel)
        }

        override fun newArray(size: Int): Array<Books?> {
            return arrayOfNulls(size)
        }
    }
}
