import org.jsoup._
import scala.jdk.CollectionConverters._

object JsoupScraper: 

  @main def hello(): Unit =
    println("Hello world!")
    println(msg)

    //Beginning to test jsoup. Following https://www.scrapingbee.com/blog/web-scraping-scala/
    val doc = Jsoup.connect("http://en.wikipedia.org/").get()
    print("\nLet's parse ... ")
    println(doc.title())
    println("\nIn the news:")
    println(doc.select("#mp-itn b a"))
    println("\nDid you know:")
    println(doc.select("#mp-dyk b a"))
    
  def msg = "I was compiled by Scala 3. :)"
