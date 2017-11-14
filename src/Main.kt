import com.trinitcore.sqlv2.commonUtils.AssociatingQMap
import com.trinitcore.sqlv2.commonUtils.QMap
import com.trinitcore.sqlv2.queryObjects.SQL
import com.trinitcore.sqlv2.queryObjects.Table
import com.trinitcore.sqlv2.queryUtils.associations.Association
import com.trinitcore.sqlv2.queryUtils.connection.PostgresConnectionManager
import com.trinitcore.sqlv2.queryUtils.associations.Associating
import com.trinitcore.sqlv2.queryUtils.builders.AssociationBuilder
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

    val appointments = Table("appointments")
            .addAssociation(
                    AssociationBuilder("messages", false, Associating("ID", "messages", "appointmentID"))
                            .addAssociation(
                                    AssociationBuilder("users", true, Associating("senderUserID", "userDetails", "ID"))
                            )
            )

    appointments.insert(
            AssociatingQMap("messages",
                    QMap("content", "lah"),
                    QMap("senderUserID", 55),
                    QMap("dateTime", Date().time)
            )
    )
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