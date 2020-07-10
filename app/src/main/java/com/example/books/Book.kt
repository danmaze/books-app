package com.example.books

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableField
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class Book : Parcelable {
    var id = ObservableField<String>()
    lateinit var title: String
    var subTitle = ObservableField<String>()
    var authors = ObservableField<String>()
    var publisher = ObservableField<String>()
    var publishedDate = ObservableField<String>()
    var description = ObservableField<String>()
    var thumbnail = ObservableField<String>()

    constructor(
        id: String, title: String, subTitle: String, authors: Array<String?>, publisher: String,
        publishedDate: String, description: String, thumbnail: String) {
        try {
            this.id.set(id)
            this.title = title
            this.subTitle.set(subTitle)
            this.authors.set(TextUtils.join(", ", authors))
            this.publisher.set(publisher)
            this.publishedDate.set(publishedDate)
            this.description.set(description)
            this.thumbnail.set(thumbnail)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    protected constructor(source: Parcel) {
        id.set(source.readString())
        title = source.readString()
        subTitle.set(source.readString())
        authors.set(source.readString())
        publisher.set(source.readString())
        publishedDate.set(source.readString())
        description.set(source.readString())
        thumbnail.set(source.readString())
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeString(id.get())
        parcel.writeString(title)
        parcel.writeString(subTitle.get())
        parcel.writeString(authors.get())
        parcel.writeString(publisher.get())
        parcel.writeString(publishedDate.get())
        parcel.writeString(description.get())
        parcel.writeString(thumbnail.get())
    }

    companion object {

        @JvmField
        val CREATOR: Parcelable.Creator<Book> = object : Parcelable.Creator<Book> {
            override fun createFromParcel(source: Parcel): Book = Book(source)
            override fun newArray(size: Int): Array<Book?> {
                return arrayOfNulls(size)
            }
        }

        @JvmStatic
        @BindingAdapter("android:imageUrl")
        fun loadImage(view: ImageView, imageUrl: String) {
            if (!imageUrl.isEmpty()) {
                Glide.with(view.context)
                    .load(imageUrl)
                    .apply(RequestOptions().error(R.drawable.book_open))
                    .into(view)
            } else {
                view.setBackgroundResource(R.drawable.book_open)
            }
        }

    }

}
