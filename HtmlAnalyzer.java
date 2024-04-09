import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class HtmlAnalyzer {
    // url to be analyzed
    private String urlStr;
    // website's html content
    private String htmlContent;

    // constructor that takes the website's url
    public HtmlAnalyzer(String urlStr) {
        this.urlStr = urlStr;
    }

    // method that analyzes the website
    public void analyze() {
        // fetch its content
        this.htmlContent = this.fetchHtmlContent();
        // analyzes content
        this.getContent(this.htmlContent);
    }

    // method that fetches html's content
    private String fetchHtmlContent() {
        StringBuilder res = new StringBuilder();
        try {
            URL url = new URL(this.urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // reads the content
            try (BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String resStr = null;
                while ((resStr = buffer.readLine())!= null) {
                    res.append(resStr);
                }
            }
        } catch(Exception UrlConnectionException) {
            System.out.println("URL connection error");
        }
        return res.toString();
    }

    // method that analyzes html's content
    private void getContent(String html) {
        int tagPosition = 0;                                                                                          
        int openTags = 0;
        boolean foundDeepestTag = false;
        int currentDepth = 0;
        int maxDepth = 0;
        String deepestTag = "";
        String content = "";

        // html's content loop
        while ((tagPosition = html.indexOf('<', tagPosition)) != -1) {                                                        
            if (html.charAt(tagPosition + 1) == '/') {                                                                 
                openTags--;                                                                                    
                currentDepth--;
            } else {
                openTags++;                                                                                    
                currentDepth++;
                int endtagPosition = html.indexOf('>', tagPosition);                                                           
                String tag = html.substring(tagPosition + 1, endtagPosition);                                                  
                if (!tag.equals("html") && !foundDeepestTag) {                                                                     
                    if (currentDepth > maxDepth) {                                                                                 
                        maxDepth = currentDepth;
                        deepestTag = tag;                                                                                  
                        int startContenttagPosition = endtagPosition + 1;                                                                  
                        int endContenttagPosition = html.indexOf('<', startContenttagPosition);
                        content = html.substring(startContenttagPosition, endContenttagPosition).trim();                                   
                    }
                }
            }
            tagPosition++;
        }
        
        // checks if the html is malformed
        if (openTags != 0) {                                                                                  
            System.out.println("malformed HTML");
            return;
        }

        // prints content
        System.out.println(content);
    }

    // main method that creates an instance of the analyzer and starts the analysis
    public static void main(String[] args) {
        HtmlAnalyzer analyzer = new HtmlAnalyzer(args[0]);
        analyzer.analyze();
    }
}
