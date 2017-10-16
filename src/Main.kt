import com.trinitcore.sqlv2.commonUtils.AssociatingQMap
import com.trinitcore.sqlv2.commonUtils.MultiAssociatingQMap
import com.trinitcore.sqlv2.commonUtils.QMap
import com.trinitcore.sqlv2.commonUtils.row.Row
import com.trinitcore.sqlv2.commonUtils.row.Rows
import com.trinitcore.sqlv2.queryObjects.SQL
import com.trinitcore.sqlv2.queryObjects.Table
import com.trinitcore.sqlv2.queryUtils.builders.Association
import com.trinitcore.sqlv2.queryUtils.connection.PostgresConnectionManager
import com.trinitcore.sqlv2.queryUtils.parameters.Associating
import com.trinitcore.sqlv2.queryUtils.parameters.Where
import com.trinitcore.sqlv2.queryUtils.parameters.columns.IntegerColumn
import com.trinitcore.sqlv2.queryUtils.parameters.columns.TextColumn
import java.security.SecureRandom
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

    //SQL.sharedConnection = PostgresConnectionManager("localhost","trinsqltest", "postgres", "@C[]4m9c17")
    SQL.sharedConnection = PostgresConnectionManager("ec2-23-23-220-163.compute-1.amazonaws.com", "dali3p5b9n1bn", "kkrjxuzslvuuqh", "d14d0dd9116a0be25834fe489e56a8409cd6e51d9a7fcbd84fff91b3672dc401", true)

    val appointments = Table("appointments")
    for (i in 1..60) {
        Thread(Runnable {
            Thread.sleep((1000 * i).toLong())
            appointments.find()
        }).start()

        Thread(Runnable {
            Thread.sleep((1000 * i).toLong())
            appointments.find()
        }).start()
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