import rs.etf.sab.operations.*;
import org.junit.Test;
import rs.etf.sab.student.*;
import rs.etf.sab.tests.TestHandler;
import rs.etf.sab.tests.TestRunner;

import java.util.Calendar;

public class StudentMain {

    public static void main(String[] args) {

        ArticleOperations articleOperations = new ma190084_ArticleOperations();
        BuyerOperations buyerOperations = new ma190084_BuyerOperations();
        CityOperations cityOperations = new ma190084_CityOperations();
        GeneralOperations generalOperations = new ma190084_GeneralOperations();
        OrderOperations orderOperations = new ma190084_OrderOperations();
        ShopOperations shopOperations = new ma190084_ShopOperations();
        TransactionOperations transactionOperations = new ma190084_TransactionOperations();

        TestHandler.createInstance(
                articleOperations,
                buyerOperations,
                cityOperations,
                generalOperations,
                orderOperations,
                shopOperations,
                transactionOperations
        );

        TestRunner.runTests();
    }
}
