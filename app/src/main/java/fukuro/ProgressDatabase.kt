package fukuro

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "playback_progress")
data class ProgressEntity(
    @PrimaryKey val itemId: String,
    val positionSeconds: Double,
    val updatedAt: Long,
    val finished: Boolean,
) {
    fun asModel() = LocalProgress(positionSeconds, updatedAt, finished)
}

@Dao
interface ProgressDao {
    @Query("SELECT * FROM playback_progress")
    fun observeAll(): Flow<List<ProgressEntity>>

    @Query("SELECT * FROM playback_progress")
    suspend fun getAll(): List<ProgressEntity>

    @Query("SELECT * FROM playback_progress WHERE itemId = :itemId LIMIT 1")
    suspend fun get(itemId: String): ProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(progress: ProgressEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLegacy(progress: List<ProgressEntity>)

    @Transaction
    suspend fun update(itemId: String, positionSeconds: Double, finished: Boolean?) {
        val previous = get(itemId)
        upsert(
            ProgressEntity(
                itemId = itemId,
                positionSeconds = positionSeconds,
                updatedAt = System.currentTimeMillis(),
                finished = finished ?: previous?.finished ?: false,
            )
        )
    }
}

@Database(entities = [ProgressEntity::class], version = 1, exportSchema = false)
abstract class ProgressDatabase : RoomDatabase() {
    abstract fun progress(): ProgressDao

    companion object {
        fun create(context: Context): ProgressDatabase =
            Room.databaseBuilder(context, ProgressDatabase::class.java, "playback-progress.db")
                .build()
    }
}
