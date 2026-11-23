package com.example.playsport

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface BookingDao {
    // CREATE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity)

    // READ
    @Query("SELECT * FROM booking ORDER BY id DESC")
    suspend fun getAllBooking(): List<BookingEntity>

    @Query("SELECT * FROM booking WHERE id = :id")
    suspend fun getById(id: Int): BookingEntity?

    // UPDATE (dicocokkan lewat Primary Key)
    @Update
    suspend fun updateBooking(booking: BookingEntity)

    // DELETE
    @Delete
    suspend fun deleteBooking(booking: BookingEntity)

    @Query("DELETE FROM booking WHERE id = :id")
    suspend fun deleteById(id: Int)

    // Cek slot bentrok: lapangan & tanggal sama, rentang jam saling tumpang tindih.
    // excludeId dipakai saat EDIT agar booking itu sendiri tidak dianggap bentrok.
    @Query(
        """
        SELECT COUNT(*) FROM booking
        WHERE lapanganId = :lapanganId
          AND tanggal = :tanggal
          AND jamMulai < :jamSelesai
          AND (jamMulai + durasi) > :jamMulai
          AND id != :excludeId
        """
    )
    suspend fun hitungBentrok(
        lapanganId: Int,
        tanggal: String,
        jamMulai: Int,
        jamSelesai: Int,
        excludeId: Int
    ): Int
}
