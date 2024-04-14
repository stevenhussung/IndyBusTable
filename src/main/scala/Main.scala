import org.jsoup._
import scala.jdk.CollectionConverters._

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

  val bus_route_page = Jsoup.connect("https://www.indygo.net/route/3-michigan-street/").get()
  // println("\nBus route 3")
  // println(bus_route.select("#accordion-weekday-0"))
  
  // println(bus_route.select("#accordion-weekday-0-collapse-1 > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > ul:nth-child(2) > li:nth-child(1)"))
  // println(bus_route.select("#accordion-weekday-0-collapse-1 > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > ul:nth-child(2)"))
  
  val bus_route = 
  for schedule <- bus_route_page.select("#stop_lists > *").asScala 
  yield
    var schedule_id_info = schedule.attr("id").split("-")
    var day_of_week = schedule_id_info(1).capitalize
    var direction = if schedule_id_info(2) == "0" then "Westbound" else "Eastbound"

    var stop_list =
      (
      for card <- schedule.select("[class=card]").asScala
      yield
        var stop_name = card.select("div h5 button span").text
        var stop_times = card.select("li").asScala.map(_.text)
        (stop_name -> stop_times)
      )
    ((day_of_week, direction) -> stop_list)
  
  println("Attempt to print out the bus route in an organized way:")
  for ((weekdarity, direction), stops) <- bus_route
  do
    println(weekdarity)
    println(direction)
    for (stop_name, stop_times) <- stops
    do
      println(stop_name)
      // println(s"There are ${stop_times.length} times at this stop")
      println(stop_times)