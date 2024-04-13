import org.jsoup._
import scala.jdk.CollectionConverters._

object JsoupScraper: 

  @main def hello(): Unit =
    println("Hello world!")

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

    val bus_route = Jsoup.connect("https://www.indygo.net/route/3-michigan-street/").get()
    // println("\nBus route 3")
    // println(bus_route.select("#accordion-weekday-0"))
    
    // println(bus_route.select("#accordion-weekday-0-collapse-1 > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > ul:nth-child(2) > li:nth-child(1)"))
    // println(bus_route.select("#accordion-weekday-0-collapse-1 > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > ul:nth-child(2)"))
    
    for schedule <- bus_route.select("#stop_lists > *").asScala 
    do
      println("\nSCHEDULE ELEMENT HERE")
      println(schedule.attr("id"))
      // println(schedule.text)
      for card <- schedule.select("[class=card]").asScala
      do
        println(card.select("div h5 button span").text)
        // This lists through the stops, which is too much information at this point.
        // for stop <- schedule.select("li").asScala
        // do
          // println(stop.text)

