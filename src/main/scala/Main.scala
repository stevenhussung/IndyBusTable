import org.jsoup._
import scala.jdk.CollectionConverters._
import scalatags.Text.all._

import java.io._

@main def hello(): Unit =
  println("Hello world!")

  val bus_route_page = Jsoup.connect("https://www.indygo.net/route/3-michigan-street/").get()
  val bus_stop_times = bus_route_reader(bus_route_page).sortBy(_(0)).reverse

  
  // for ((weekdarity, direction), stops) <- bus_route
  // do
    // println(weekdarity)
    // println(direction)
    // for (stop_name, stop_times) <- stops
    // do
      // println(stop_name)
      // println(stop_times)
  
  println("\n\nAnd now, some html:")
  val html_content = 
    html(
      head(
        script("some script")
      ),
      body
      (
        h1("Route 3: Michigan St."),
        (
        for ((weekdarity, direction), stops) <- bus_stop_times
        yield
          div(
            h2(weekdarity ++ ": " ++ direction),
            table(
              for (stop_name, stop_times) <- stops
              yield
                tr(
                  td(stop_name), 
                  (for time <- stop_times
                  yield td(time)).toList
              )
            )
          )
        ).toList
      )
    )

  val output_file = File("output.html")
  val file_writer = BufferedWriter(FileWriter(output_file))
  file_writer.write(html_content.toString)
  file_writer.close()

    
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
      ).toList
    )
    