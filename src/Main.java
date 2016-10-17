import com.trinitcore.sql.Map;
import com.trinitcore.sql.Row;
import com.trinitcore.sql.SQL;
import com.trinitcore.sql.queryObjects.returnableQueries.Select;
import com.trinitcore.sql.queryObjects.returnableQueries.Table;
import config.Sqlite;
import org.json.simple.JSONObject;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        /*
        SQL.setConfiguration(new Sqlite());

        Select users = new Select("users");

        Select comments = new Select("products_comments")
                .setAssociation("USER_ID","ID",users);

        Table products = new Table("products")
                .setAssociation("USER_ID","ID",users)
                .createAssociation("ID","PRODUCT_ID",comments,"COMMENTS");

        JSONObject output = new JSONObject();
        output.put("products",products.toJSONArray());
        System.out.println(output.toJSONString());
        // productsIteration(products);

        //System.out.println(products.rowCount());
        // products.insert(new Map("USER_ID",1));
        // products.update("USER_ID",1, new Map("NAME","McCockerson's"));
        // products.delete("USER_ID",1);
        //System.out.println(products.rowCount());
        */
    }

    /*
    static void productsIteration(Select products) {
        for (Row product : products.getRows()) {
            System.out.println(product.get("NAME"));

            Row userRow = (Row) product.get("USER_ID");
            System.out.println(userRow.get("USERNAME"));
            try {
                for (Row comment : (Row[]) product.get("COMMENTS")) {
                    System.out.println("--" + comment.get("CONTENT") + "--");
                    Row user = (Row) comment.get("USER_ID");
                    System.out.println("--"+user.get("USERNAME")+"--");
                }
            }catch (NullPointerException ignored) {
                System.out.println("-- No comments found --");
            }
        }
    }*/

}
