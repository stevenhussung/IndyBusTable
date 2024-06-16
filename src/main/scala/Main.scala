import org.jsoup._
import scala.jdk.CollectionConverters._
import scalatags.Text.all._
import scala.reflect.ClassTag

import java.io._

@main def hello(): Unit =
  println("Hello world!")

  val bus_route_page = Jsoup.connect("https://www.indygo.net/route/3-michigan-street/").get()
  val bus_stop_times = bus_route_reader(bus_route_page).sortBy(_(0)).reverse
  
  println("\n\nAnd now, some html:")
  val html_content = bus_route_to_html(bus_stop_times)

  val output_file = File("output.html")
  val file_writer = BufferedWriter(FileWriter(output_file))
  file_writer.write(html_content.toString)
  file_writer.close()
  
  /*
  //Next step: Finding the individual routes.
  //Remembering how to unpack the data structure.
  for ((weekdarity, direction), stops) <- bus_stop_times
  do
    //Will need to traverse this going across: need to get first stop_time for each stop_loc
    for (stop_name, stop_times) <- stops
    do
      for time <- stop_times
      do
        println(weekdarity.toString() + " " + direction + " " + stop_name + " " + time)
  */
  
def bus_route_reader(bus_route_page : org.jsoup.nodes.Document) : 
  scala.collection.mutable.Buffer[
    scala.Tuple2[
      scala.Tuple2[
        java.lang.String, java.lang.String
      ], 
      scala.collection.immutable.List[
        scala.Tuple2[
          java.lang.String,
          scala.collection.mutable.Buffer[Time]
        ]
      ]
    ]
  ]
  =
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
        var stop_times = card.select("li").asScala.map(_.text).map(s => Time(s))
        (stop_name -> stop_times)
      ).toList
    )
    
def bus_route_to_html(bus_route :
  scala.collection.mutable.Buffer[
    scala.Tuple2[
      scala.Tuple2[
        java.lang.String, java.lang.String
      ], 
      scala.collection.immutable.List[
        scala.Tuple2[
          java.lang.String,
          scala.collection.mutable.Buffer[Time]
        ]
      ]
    ]
  ]): scalatags.Text.TypedTag[String] =
  html(
    head(
      script("some script"),
      link(rel:="stylesheet",href:="./index.css")
    ),
    body
    (
      h1("Route 3: Michigan St."),
      (
      for ((weekdarity, direction), stops) <- bus_route
      yield
        div(
          h2(weekdarity ++ ": " ++ direction),
          table(cls:="styled-table",
            for (stop_name, stop_times) <- stops
            yield
              tr(
                td(stop_name), 
                (for time <- stop_times
                yield td(time.toString)).toList
            )
          )
        )
      ).toList
    )
  )


def getTypeAsString[T](v: T)(implicit ev: ClassTag[T]) = 
  ev.toString