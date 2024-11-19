package com.mycompany.webcrawler_using_bfs;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class WebCrawler_Using_BFS {
    
    private static final int MAX_DEPTH = 2;
        
        private HashSet<String>urlLinks;
        
        public WebCrawler_Using_BFS() { urlLinks = new HashSet<>();  }
       // BFS Algorithm
        public void getPageLinks (String startURL){
           // Create Queue
            Queue<Page>queue = new LinkedList<>();
            
            queue.add(new Page(startURL,0));
            
            urlLinks.add(startURL);
            
            while(!queue.isEmpty())
            {  
                Page currentPage = queue.poll();
                
                if(currentPage.depth > MAX_DEPTH) { continue; }
             
                System.out.println(">>Depth: " + currentPage.depth +"[" +currentPage.url +"]");
                
                try{
                    Document doc= Jsoup.connect(currentPage.url).get();
                    
                    Elements availableLinkOnPage = doc.select("a[href],img[src],link[href],script[src]");
                    
                    int nextDepth = currentPage.depth+1;
                    for(Element page : availableLinkOnPage){    
                        String absUrl = page.attr("abs:href");
                        String absSrc = page.attr("abs:src");
                        
                         if(!urlLinks.contains(absSrc) && !absSrc.isEmpty() )
                             {
                        
                                urlLinks.add(absSrc);
                                queue.add(new Page(absSrc,nextDepth));
                             }
                     }
                
                 }catch (IOException e){
                      System.err.println("For '"+ currentPage.url+"':"+e.getMessage());
                     }
             }
         }
        private static class Page {
            String url;
            int depth;
        public Page(String url, int depth) {
            this.url = url;
            this.depth = depth;
            }
        }

    public static void main(String[] args) {
        
        WebCrawler_Using_BFS object = new WebCrawler_Using_BFS();
        
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the URL to Web Crawler : ");
        
        String startURL = scanner.nextLine();
        
        if(startURL.isEmpty()){
          System.out.println("Please enter a valid URL ");
         }else{
          object.getPageLinks(startURL);
         }
        scanner.close();
     }
 }




// link using               https://www.javatpoint.com/digital-electronics/
//                          https://en.wikipedia.org/wiki/Website