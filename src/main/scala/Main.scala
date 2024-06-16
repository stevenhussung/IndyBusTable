import org.jsoup._
import scala.jdk.CollectionConverters._
import scalatags.Text.all._
import scala.reflect.ClassTag
import util.control.Breaks._

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
  
  for ((weekdarity, direction), stops) <- bus_stop_times.take(1)
  do
    //Assemble list of bus runs for this weekdarity + route pair.
    var run_list = scala.collection.mutable.Buffer[scala.collection.mutable.Buffer[Stop]]()
    
    //Reminder: each element of stops is a tuple containing a String and a List of Time's.
    while stops(0)(1).length > 0 
    do
      var run = scala.collection.mutable.Buffer[Stop]()
      
      breakable(
        for i <- 0 to stops.length - 1
        do
          //We proceed backwards through the stop locations so that we accurately account for runs that begin partway through the route.
          var stop_raw = stops.reverse(i)
          
          //We remove stop times as they are added to runs, so we must check for time existence.
          if stop_raw(1).length > 0 then
            var next_stop = Stop(stop_raw(0), stop_raw(1)(0))
            
            //Add stop if appropriate.
            if run.length == 0 || next_stop.earlierThan(run.last) then
              run.append(next_stop)
              stops.reverse(i)(1).remove(0)
            else if !next_stop.earlierThan(run.last) then
              // println(s"The stop at ${next_stop} must be a different run of this route. Ending")
              break
      )
        
      run_list += run.reverse
    
    for run <- run_list
    do
      println(run)

      
class Stop(stop_name : String, stop_time : Time):
  def time() : Time =
    stop_time

  def name() : String =
    stop_name

  def earlierThan(that : Stop): Boolean = 
    this.stop_time <= that.time()
  
  override def toString(): String =
    s"${stop_name} at ${stop_time}"
  

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