import com.trinitcore.sqlv2.commonUtils.AssociatingQMap
import com.trinitcore.sqlv2.commonUtils.QMap
import com.trinitcore.sqlv2.commonUtils.row.Rows
import com.trinitcore.sqlv2.queryObjects.SQL
import com.trinitcore.sqlv2.queryObjects.Table
import com.trinitcore.sqlv2.queryUtils.associationV2.Associating
import com.trinitcore.sqlv2.queryUtils.associationV2.table.RowsAssociation
import com.trinitcore.sqlv2.queryUtils.connection.PostgresConnectionManager
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

    //SQL.sharedConnection = PostgresConnectionManager("localhost","trinsqltest", "postgres", "@C[]4m9c17")
    SQL.sharedConnection = PostgresConnectionManager("ec2-23-23-220-163.compute-1.amazonaws.com", "dali3p5b9n1bn", "kkrjxuzslvuuqh", "d14d0dd9116a0be25834fe489e56a8409cd6e51d9a7fcbd84fff91b3672dc401", true)

    val users = Table("users")
            .addAssociation(
                    RowsAssociation("adviser_category_selection", Associating("ID", "categoryID", "userID"))
            )
    users.insert(QMap("ID",62), AssociatingQMap("categoryID", QMap("categoryID",1)))

    val user = users.findRowByID(62)!!
    val categories = user["categoryID"] as Rows
    categories.delete(Where().value("userID", 62))

    users.insert(QMap("ID",62), AssociatingQMap("categoryID", QMap("categoryID",1)))

    val user2 = users.findRowByID(62)!!
    val categories2 = user2["categoryID"] as Rows
    categories2.delete(Where().value("userID", 62))

    categories2.multiValueInsert(
            arrayOf(
                    arrayOf(QMap("userID", 62), QMap("categoryID",1)),
                    arrayOf(QMap("userID", 62), QMap("categoryID",2)),
                    arrayOf(QMap("userID", 62), QMap("categoryID",3)),
                    arrayOf(QMap("userID", 62), QMap("categoryID",4)),
                    arrayOf(QMap("userID", 62), QMap("categoryID",5)),
                    arrayOf(QMap("userID", 62), QMap("categoryID",6)),
                    arrayOf(QMap("userID", 62), QMap("categoryID",7)),
                    arrayOf(QMap("userID", 62), QMap("categoryID",8)),
                    arrayOf(QMap("userID", 62), QMap("categoryID",9))
                    )
    )
    //categories2.delete(Where().value("userID", 62))
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