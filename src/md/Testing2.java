package md;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class Testing2 extends HttpServlet {

    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        requestHandling(request, response);
    }

    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        requestHandling(request, response);
    }

    private void requestHandling(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        System.out.println("INSIDE Test2 class...");
        System.out.println("Fetching the record from memory...");

        Testing1 test = new Testing1();
        String b = test.a;
        System.out.println("VALUE OF B " + b);
//        test.myMethod(request, response);
        System.out.println("SIZE OF HMAP is " + test.myMap.size());

        Set set = test.myMap.entrySet();
        for (Object aSet : set) {
            Map.Entry mentry = (Map.Entry) aSet;
            System.out.print("key is: " + mentry.getKey() + " & Value is: ");
            System.out.println(mentry.getValue());
        }
    }
}
