package com.chinhdev.roomdbex

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student")
class StudentModel(
    @PrimaryKey(autoGenerate = true) var uid: Int = 0,
    @ColumnInfo(name = "hoten") var hoten: String?,
    @ColumnInfo(name = "mssv") var mssv: String?,
    @ColumnInfo(name = "diemTB") var diemTB: Float?
) {
    fun getThongtin(): String {
        return "Ho ten: $hoten \n" +
                "MSSV: $mssv \n" +
                "Diem trung binh: $diemTB \n"
    }
    fun copy(
        uid: Int = this.uid,
        hoten: String? = this.hoten,
        mssv: String? = this.mssv,
        diemTB: Float? = this.diemTB
    ): StudentModel {
        return StudentModel(uid, hoten, mssv, diemTB)
    }

}