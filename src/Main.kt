import com.trinitcore.v2.commonUtils.QMap
import com.trinitcore.v2.commonUtils.row.Rows
import com.trinitcore.v2.queryObjects.SQL
import com.trinitcore.v2.queryObjects.Table
import com.trinitcore.v2.queryUtils.connection.ConnectionManager
import com.trinitcore.v2.queryUtils.builders.Association
import com.trinitcore.v2.queryUtils.connection.PostgresConnectionManager
import com.trinitcore.v2.queryUtils.parameters.Associating
import com.trinitcore.v2.queryUtils.parameters.Where
import com.trinitcore.v2.queryUtils.parameters.columns.IntegerColumn
import com.trinitcore.v2.queryUtils.parameters.columns.TextColumn
import java.security.SecureRandom
import java.sql.Connection
import java.sql.DriverManager
import java.util.Random
import java.util.Objects
import java.util.Locale
import java.util.concurrent.ThreadLocalRandom


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

    SQL.sharedConnection = PostgresConnectionManager("trinsqltest", "postgres", "@C[]4m9c17")

    SQL.session {
        val table = Table("products_lel", TextColumn("name").default("Hello"), TextColumn("description"), IntegerColumn("age").notNull(76))
                .addAssociation(
                        Association("comments", parameters = Associating("ID", columnTitle = "comments", childColumnName = "productID"))
                                .addAssociation(
                                        Association("users", notArray = true, parameters = Associating("userID", columnTitle = "userDetails", childColumnName = "ID"))
                                )
                )
        var r: Rows? = null
        println("Getting every single value: " + measureOperation {
            r = table.find(Where())
            println(r?.toJSONArray())
        })


        var array: MutableList<Array<QMap>> = mutableListOf()
        val gen = RandomString(8, ThreadLocalRandom.current())

        println("Make mutable list operation: " + measureOperation {
            for (i in 1..3000) {
                array.add(arrayOf(QMap("name", gen.nextString()), QMap("description", gen.nextString())))
            }
        })
        var typedArray:Array<Array<QMap>> = emptyArray()
        println("Make typed array: " + measureOperation {
            typedArray = array.toTypedArray()
        })

        println("Inserting multiple values: " + measureOperation {
            // r?.multiValueInsert(typedArray)
        })
    }
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