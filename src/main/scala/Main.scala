import org.jsoup._
import scala.jdk.CollectionConverters._
import scalatags.Text.all._

@main def hello(): Unit =
  println("Hello world!")

  val bus_route_page = Jsoup.connect("https://www.indygo.net/route/3-michigan-street/").get()
  val bus_route = bus_route_reader(bus_route_page)
  
  println("Attempt to print out the bus route in an organized way:")
  for ((weekdarity, direction), stops) <- bus_route
  do
    println(weekdarity)
    println(direction)
    for (stop_name, stop_times) <- stops
    do
      println(stop_name)
      println(stop_times)
    
def bus_route_reader(bus_route_page : org.jsoup.nodes.Document) =
  for schedule <- bus_route_page.select("#stop_lists > *").asScala 
  yield
    var schedule_id_info = schedule.attr("id").split("-")
    var day_of_week = schedule_id_info(1).capitalize
    var direction = if schedule_id_info(2) == "1" then "Westbound" else "Eastbound"

    ((day_of_week, direction) ->
      (
      for card <- schedule.select("[class=card]").asScala
      yield
        var stop_name = card.select("div h5 button span").text
        var stop_times = card.select("li").asScala.map(_.text)
        (stop_name -> stop_times)
      )
    )
    