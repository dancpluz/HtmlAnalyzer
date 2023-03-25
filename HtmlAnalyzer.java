
package HtmlAnalyzer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Stack;

/**
 *
 * @author Daniel
 */

public class HtmlAnalyzer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String result = getContentFromURL("http://hiring.axreng.com/internship/example3.html");
        if (!result.equals("Done")) {
            System.out.println(result);
        }
        
    }

    public static String getContentFromURL(String urlStr) {
    try {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(2000);
        connection.setReadTimeout(2000);

        // Utilizando BufferedReader para ler cada linha do código
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        Stack<TreeNode> stack = new Stack<>();
        TreeNode root = new TreeNode(reader.readLine());
        String line;
        
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            int j = line.indexOf('<');
            if (j == -1) {
                root.children.add(new TreeNode(line));
                continue;
            }
            String tag = line;
            
            if (tag.charAt(1) == '/') {
                if (stack.isEmpty()){
                    break;
                }
                stack.peek().children.add(root);
                root = stack.peek();
                stack.pop();
            } else {
                stack.push(root);
                root = new TreeNode(tag);
            }
            
        }
        reader.close();
        TreeNode.printTree(root);
        System.out.println(root.deepest().tagName);
        
      // Caso não seja possível conectar ou erro na leitura, o output mudará
    } catch (Exception e) {
        e.printStackTrace(); // Por motivos do teste, os erros foram omitidos
        return "URL connection error"; 
    }
    return "Done";
    }
}
/*


public static String getContentFromURL(String urlStr) {
    StringBuilder contentBuilder = new StringBuilder();
    try {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            contentBuilder.append(line);
        }
        reader.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
    return contentBuilder.toString();
}

Moved the code that retrieves the content into a separate method.
Used HttpURLConnection instead of URLConnection for better performance and more features.
Added timeouts to the connection to avoid hanging in case of network issues.
Used BufferedReader instead of Scanner for reading the content line by line.
Changed the exception handling to only print the stack trace and return an empty string in case of errors.


String content = null;
        URLConnection connection = null;
        try {
          connection =  new URL("http://hiring.axreng.com/internship/example1.html").openConnection();
          Scanner scanner = new Scanner(connection.getInputStream());
          scanner.useDelimiter("\\Z");
          content = scanner.next();
          scanner.close();
        }catch ( IOException ex ) {
        }
        System.out.println(content);
*/