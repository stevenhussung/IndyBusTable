import org.jsoup._
import scala.jdk.CollectionConverters._

object JsoupScraper: 

  @main def hello(): Unit =
    println("Hello world!")
    println(msg)

    //Beginning to test jsoup. Following https://www.scrapingbee.com/blog/web-scraping-scala/
    /*
    val wiki = Jsoup.connect("http://en.wikipedia.org/").get()
    print("\nLet's parse ... ")
    println(wiki.title())
    println("\nIn the news:")
    println(wiki.select("#mp-itn b a"))
    println("\nDid you know:")
    println(wiki.select("#mp-dyk b a"))
    // */
    //
    val bus_route = Jsoup.connect("https://www.indygo.net/route/3-michigan-street/").get()
    println("\nBus route 3")
    println(bus_route.select("#accordion-weekday-0"))
    
    println("\nOne stop on the route: Michigan and Rural")
    println(bus_route.select("#accordion-weekday-0-collapse-1 > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > ul:nth-child(2) > li:nth-child(1)"))
    println(bus_route.select("#accordion-weekday-0-collapse-1 > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > ul:nth-child(2)"))
    
    val bus_stop = bus_route.select("#accordion-weekday-0-collapse-1 > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > ul:nth-child(2) > li").asScala
    for time <- bus_stop do
      println("Hey! I'm an element")
      println(time)
      println(time.text)
  def msg = "I was compiled by Scala 3. :)"
