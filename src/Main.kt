import com.trinitcore.sqlv2.commonUtils.row.Row
import com.trinitcore.sqlv2.queryObjects.ModuleTable
import com.trinitcore.sqlv2.queryObjects.SQL
import com.trinitcore.sqlv2.queryObjects.Table
import com.trinitcore.sqlv2.queryUtils.associationV2.Associating
import com.trinitcore.sqlv2.queryUtils.associationV2.format.ReformatAssociation
import com.trinitcore.sqlv2.queryUtils.associationV2.table.RowAssociation
import com.trinitcore.sqlv2.queryUtils.associationV2.table.RowsAssociation
import com.trinitcore.sqlv2.queryUtils.connection.PostgresConnectionManager
import com.trinitcore.sqlv2.queryUtils.module.SubTestModule
import com.trinitcore.sqlv2.queryUtils.module.TestModule
import com.trinitcore.sqlv2.queryUtils.parameters.Where
import java.security.SecureRandom
import java.util.*


/**
 * Created by Cormac on 17/08/2017.
 */

fun measureOperation(stream: () -> Unit) : Long {
    val systemTime1 = System.currentTimeMillis()
    stream()
    val systemTime2 = System.currentTimeMillis()

    return (systemTime2 - systemTime1)
}

fun main(args: Array<String>) {
    SQL.sharedConnection = PostgresConnectionManager("ec2-23-23-220-163.compute-1.amazonaws.com", "dali3p5b9n1bn", "kkrjxuzslvuuqh", "d14d0dd9116a0be25834fe489e56a8409cd6e51d9a7fcbd84fff91b3672dc401", true)

    val minHourAssoc = ReformatAssociation("min", { row -> return@ReformatAssociation row["minimumHour"].toString() + ":" + row["minimumMinute"] })
    val maxHourAssoc = ReformatAssociation("max", { row -> return@ReformatAssociation row["maximumHour"].toString() + ":" + row["maximumMinute"] })

    val users = ModuleTable<TestModule>("users", { TestModule() })
            .addAssociation(RowsAssociation("adviser_unavailable_time_ranges",
                    Associating("ID","unavailableTimeRanges","userID")
                            .skipRowIfParentRowExcludesValue("userType",1)
                            .blankRowsIfMatchNotFound()
            ).addAssociation(minHourAssoc).addAssociation(maxHourAssoc))
            .addAssociation(RowAssociation("adviser_category_selection", Associating("ID", "categorySelection", "userID")).module { SubTestModule() })
    val row = users.findModuleByID(138)

    row
    /*
    //SQL.sharedConnection = PostgresConnectionManager("localhost","trinsqltest", "postgres", "@C[]4m9c17")
    SQL.sharedConnection = PostgresConnectionManager("ec2-23-23-220-163.compute-1.amazonaws.com", "dali3p5b9n1bn", "kkrjxuzslvuuqh", "d14d0dd9116a0be25834fe489e56a8409cd6e51d9a7fcbd84fff91b3672dc401", true)

    val minHourAssoc = ReformatAssociation("min", { row -> return@ReformatAssociation row["minimumHour"].toString() + ":" + row["minimumMinute"] })
    val maxHourAssoc = ReformatAssociation("max", { row -> return@ReformatAssociation row["maximumHour"].toString() + ":" + row["maximumMinute"] })

    val users = Table("users")
            .addAssociation(RowsAssociation("adviser_unavailable_time_ranges",
                    Associating("ID","unavailableTimeRanges","userID")
                            .skipRowIfParentRowExcludesValue("userType",1)
                            .blankRowsIfMatchNotFound()
            )
                    .addAssociation(minHourAssoc)
                    .addAssociation(maxHourAssoc)
            )
    val row = users.find(Where().value("ID",62))[62] as Row
    val rows = row["unavailableTimeRanges"] as Rows
    rows.delete(Where().value("userID", 62))

    rows.multiValueInsert(arrayOf(
            arrayOf(QMap("minimumHour",1), QMap("minimumMinute",1), QMap("maximumHour",2), QMap("maximumMinute",2), QMap("userID",62)),
            arrayOf(QMap("minimumHour",1), QMap("minimumMinute",1), QMap("maximumHour",2), QMap("maximumMinute",2), QMap("userID",62))
    ))

    val a = rows.toJSONArray()
    //categories2.delete(Where().value("userID", 62))
    */
}

class RandomString @JvmOverloads constructor(length: Int = 21, random: Random = SecureRandom(), symbols: String = alphanum) {

    /**
     * Generate a random string.
     */
    fun nextString(): String {
        for (idx in buf.indices)
            buf[idx] = symbols[random.nextInt(symbols.size)]
        return String(buf)
    }

    private val random: Random

    private val symbols: CharArray

    private val buf: CharArray

    init {
        if (length < 1) throw IllegalArgumentException()
        if (symbols.length < 2) throw IllegalArgumentException()
        this.random = Objects.requireNonNull(random)
        this.symbols = symbols.toCharArray()
        this.buf = CharArray(length)
    }

    companion object {

        val upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

        val lower = upper.toLowerCase(Locale.ROOT)

        val digits = "0123456789"

        val alphanum = upper + lower + digits
    }

}
/**
 * Create an alphanumeric string generator.
 */
/**
 * Create an alphanumeric strings from a secure generator.
 */
/**
 * Create session identifiers.
 */