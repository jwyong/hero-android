package sg.com.agentapp.sql.general_queries

import androidx.room.Dao
import androidx.room.Query

/* Created by jay on 01/10/2018. */

@Dao
interface GeneralUpdateQuery {
    // deviceUsage
    @Query("UPDATE AppUsage SET time_end = :timeEnd WHERE `row` = :lastRow")
    fun updateLastRowDU(timeEnd: Long, lastRow: Int)

    //===== MESSAGE =====
    // bulk
//    @Query("UPDATE Message SET MsgFlagDate = :msgFlagDate WHERE MsgJid IN (:jidList) AND MsgUniqueId IN (:msgIDList)")
//    fun updateFlagMsgBulk(jidList: List<String>, msgIDList: List<String>, msgFlagDate: Long)
}